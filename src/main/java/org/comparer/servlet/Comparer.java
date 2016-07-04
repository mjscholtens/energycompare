package org.comparer.servlet;

import javax.servlet.http.HttpServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.comparer.datamodel.*;

import static org.comparer.constant.Constants.*;

/**
 * 
 * @author Marijn Scholtens & Bob Reimink
 * Class representing the html page of the information of the energy consumption
 */
@WebServlet("/Comparer")
public class Comparer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	EnergyDAO e = EnergyDAO.instance;
	Date start = null, end = null;
	int cat = -1;
	int id;
	
	private EnergyCustomer ec = null;

	public Comparer() {
		super();
	}

	/**
	 * Looks if there are cookies set. If so, it initalizes the variables.
	 * @param cookies
	 */
	private void processCookies(Cookie[] cookies){
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		for(Cookie cookie : cookies){
		    if("id".equals(cookie.getName())){
		    	id = Integer.parseInt(cookie.getValue());
		        ec = e.getCustomer(id);
		    }
		    if("startDateP".equals(cookie.getName())){
		    	try {
					start = sdfDate.parse(cookie.getValue());
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    }
		    if("endDateP".equals(cookie.getName())){
		    	try {
					end = sdfDate.parse(cookie.getValue());
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    }
		    if("catP".equals(cookie.getName())){
		    	cat = Integer.parseInt(cookie.getValue());
		    }
		}		
	}
	
	/**
	 * Function which returns the page for compare.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processCookies(request.getCookies());
		if(request.getParameter("id") != null){	 
			id = Integer.parseInt(request.getParameter("id"));
			ec = e.getCustomer(id);
			Cookie cookie = new Cookie("id", String.valueOf(id));
			cookie.setMaxAge(24*60*60);
			response.addCookie(cookie);			
		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		if(ec == null){
			out.println("ID is not filled in or ID does not exist.");
			response.setHeader("Refresh", "3; URL=http://localhost:8080/EnergyCompare");
			return;
		}
		String context = request.getContextPath();
		String title = "Information Energy Consumption";
		String docType =
		"<!doctype html public \"-//w3c//dtd html 4.0 " +
		"transitional//en\">\n";
		out.println(docType +
			"<html><link rel='stylesheet' type='text/css' href='" + context + "/css/style.css'>" +
			"<head><title>" + title + "</title></head>" +
			"<body>\n" +
			"<h1 align=\"center\">" + title + "</h1>" +
			"<div class='outer'>" +
			getIntroduction() +
			getEnergyInformation() +
			"</div><div class='outer'>" +
			getChartPart(request, response) +
			"</div></body></html>");			
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * Introduction part of the page.
	 * @return html in string notation.
	 */
	private String getIntroduction(){
		String ret = "<div class='inner' >" +
				"Hello " + ec.getName() + ",<br><br>" +
				"On this page you can find information about your Energy consumption and " +
				"compare it to the energy consumption of others.</div>";
		
		return ret;	
	}
	
	/**
	 * Table of last data.
	 * @return html in string notation.
	 */
	private String getEnergyInformation(){
		String ret = "<div class='inner' >";
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy");
		List<List<EnergyRecord>> list = ec.getEnergyRecords();
		Date first;
		
		if(list.get(0).size() == 0){
			ret += "There is no information yet available.";
		} else {
			if(list.get(0).size() > 1){
				first = list.get(0).get(list.get(0).size()-2).getStartDate();
			} else {
				first = ec.getStartDate();
			}
			ret += "Overview of the consumption per category over the last period.<br><br>" +
				"From <b>" + sdfDate.format(first) + "</b> till <b>" +
				sdfDate.format(list.get(0).get(list.get(0).size()-1).getStartDate()) + "</b><br><br>" +
				"<table></tr><th>Max Power in Watt</th>"+
				"<th>Energy in kWh</th><th>Total of different devices</th></tr>";
			for(int i=0; i<list.size(); i++){
				ret += "<tr><td>" +
					CATEGORIES[i] + " - " + CATEGORIES[i+1] +
					"</td><td>" +
					String.format("%.2f", list.get(i).get(list.get(i).size()-1).getEnergy()) +
					"</td><td>" +
					String.valueOf(list.get(i).get(list.get(i).size()-1).getDevices()) +
					"</td></tr>";
			}
			ret = ret + "</table>";
		}
		ret += "</div>";
		
		return ret;
	}
	
	/**
	 * Creates the outline of the charts.
	 * @param request
	 * @param response
	 * @return html in string notation.
	 */
	private String getChartPart(HttpServletRequest request, HttpServletResponse response){
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		
		if(request.getParameter("startDateP") != null){
			try {
				Cookie cookie = new Cookie("startDateP", request.getParameter("startDateP"));
				cookie.setMaxAge(24*60*60);
				response.addCookie(cookie);			
				start = sdfDate.parse(request.getParameter("startDateP"));
			} catch (ParseException e) {
				start = new Date();
			}
		} else if(start == null){
			List<EnergyRecord> erl = ec.getEnergyRecords().get(0);
			start = erl.get(erl.size()-1).getStartDate();
		}
		
		if(request.getParameter("endDateP") != null){
			try {
				Cookie cookie = new Cookie("endDateP", request.getParameter("startDateP"));
				cookie.setMaxAge(24*60*60);
				response.addCookie(cookie);			
				end = sdfDate.parse(request.getParameter("endDateP"));
			} catch (ParseException e) {
				end = new Date();
			}
		} else if(end == null){
			List<EnergyRecord> erl = ec.getEnergyRecords().get(0);
			end = erl.get(erl.size()-1).getStartDate();
		}
		
		if(request.getParameter("catP") != null){
			Cookie cookie = new Cookie("catP", request.getParameter("catP"));
			cookie.setMaxAge(24*60*60);
			response.addCookie(cookie);			
			cat = Integer.parseInt(request.getParameter("catP"));
		}
		
		String ret = "<div class='inner'>";
		ret += "<table class='borderless'><tr><td class='borderless'>";
		
		ret += retrieveCharts();
		ret += "</td>";
		ret += "<td class='borderless'><form method='POST' action='http://localhost:8080/EnergyCompare/Comparer'>"
				+ "Category: <select name='catP'>"
				+ "<option value='-1'>All</option><option value='0'>0-15</option>"
				+ "<option value='1'>15-50</option><option value='2'>50-100</option>"
				+ "<option value='3'>100-200</option><option value='4'>200-500</option>"
				+ "<option value='5'>500-1000</option><option value='6'>1000-</option>"
				+ "</select><br>"
				+ "<br>Start date: <input type='text' size='10' name='startDateP' value='"+ sdfDate.format(start) + "'/><br>"
				+ "<br>End date: <input type='text' size='10' name='endDateP' value='"+ sdfDate.format(end) + "'/><br>"
				+ "<br><input type='submit' value='Generate chart'></form></td>";
		ret += "</table></div>";
		return ret;
	}
	
	/**
	 * Functions which will retrieve the charts.
	 * @return html in string notation.
	 */
	private String retrieveCharts() {

		GetMethod getMethod = new GetMethod("http://localhost:8080/EnergyCompare/PersonalChart");
		
		NameValuePair[] pairs = new NameValuePair[]{
				new NameValuePair("start", Long.toString(start.getTime())),
				new NameValuePair("end", Long.toString(end.getTime())),
				new NameValuePair("cat", Integer.toString(cat)),
				new NameValuePair("id", Integer.toString(id))
		};
		getMethod.setQueryString(pairs);
		
		String ret = "";
		ret += "Your energy consumption history:<br>";
		try {
			ret += "<img src='" + getMethod.getURI().getEscapedURI() + "' /><br><br>";
		} catch (URIException e) {
			e.printStackTrace();
		}
		
		getMethod = new GetMethod("http://localhost:8080/EnergyCompare/CompareChart");
		getMethod.setQueryString(pairs);
		ret += "Your energy consumption history compared to the mean:<br>";
		try {
			ret += "<img src='" + getMethod.getURI().getEscapedURI() + "' />";
		} catch (URIException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}
