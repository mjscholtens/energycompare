package org.comparer.servlet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.comparer.datamodel.*;

import static org.comparer.constant.Constants.*;

/**
 * 
 * @author Marijn Scholtens & Bob Reimink
 * This class sets the data of energyconsumption and returns the graph for the logged in customer
 * compared to the rest.
 */
@WebServlet("/CompareChart")
public class CompareChart extends Chart {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Member which keeps track how many customers has data on a given date.
	 */
	private int[] dataSize;
	
	public CompareChart(){
		super();
	}

	/**
	 * Process data of one customer in the data array.
	 * @param list data of customer
	 */
	private void addDataPerson(List<List<EnergyRecord>> list){
		int i=0, j, n=0;
		Date temp;

		Calendar c = Calendar.getInstance();
		// Fill days till end that have no entry in the customers data.
		temp = start;
		while (temp.compareTo(list.get(0).get(0).getStartDate()) < 0 ){
			c.setTime(temp);
		    c.add(Calendar.DATE, 1);
			temp = c.getTime();
			if(dataSize[n] > 1){
				data.set(n, data.get(n)*dataSize[n]/(dataSize[n]-1));
				devices.set(n, devices.get(n)*dataSize[n]/(dataSize[n]-1));
			}
			dataSize[n] = dataSize[n]-1;
			n++;
		}
		
		if(cat == -1){
			while(i < list.get(0).size()){
				if(list.get(0).get(i).getStartDate().compareTo(start) >= 0  
				  && list.get(0).get(i).getStartDate().compareTo(end) <= 0){	
					double en = 0.0;
					double dev = 0.0;
					for(j=0; j< CAT_COUNT; j++){
						en += list.get(j).get(i).getEnergy();
						dev += list.get(j).get(i).getDevices();
					}
					data.set(n, data.get(n)-en/dataSize[n]);
					devices.set(n, devices.get(n)-dev/dataSize[n]);
					n++;
				}
				i++;
			}
		} else {
			List<EnergyRecord> listcat = list.get(cat);
			while(i<listcat.size()){
				if(listcat.get(i).getStartDate().compareTo(start) >= 0  
				  && listcat.get(i).getStartDate().compareTo(end) <= 0){
					double en = listcat.get(i).getEnergy();
					double dev = listcat.get(i).getDevices();
					data.set(n, data.get(n)-en/dataSize[n]);
					devices.set(n, devices.get(n)-dev/dataSize[n]);
					n++;
				}
				i++;
			}
		}	
		
		// Fill days till end that have no entry in the customers data.
		temp = list.get(0).get(list.get(0).size()-1).getStartDate();
		c.setTime(temp);
	    c.add(Calendar.DATE, 1);
	    temp = c.getTime();
		while (temp.compareTo(end) < 0){
			c.setTime(temp);
		    c.add(Calendar.DATE, 1);
			temp = c.getTime();
			if(dataSize[n] > 1){
				data.set(n, data.get(n)*dataSize[n]/(dataSize[n]-1));	
				devices.set(n, devices.get(n)*dataSize[n]/(dataSize[n]-1));
			}
			dataSize[n] = dataSize[n]-1;
			n++;
		}
	}
	
	/**
	 * Function which calculates the data for the graph. First it determines
	 * the amount of points, then the data of the logged in customer and finally
	 * the data of other customers.
	 */
	@Override
	protected void calculateData() {

		Calendar c = Calendar.getInstance();
		int i = 0, j, n = 0;
		Date temp;
		data.clear();
		devices.clear();
		yaxis[0] = Double.POSITIVE_INFINITY;
		yaxis[2] = Double.NEGATIVE_INFINITY;

		if(start.compareTo(end) >= 0){
			return;
		}
		
		List<EnergyCustomer> ecs = EnergyDAO.instance.getCustomers();
		
		List<List<EnergyRecord>> list = EnergyDAO.instance.getCustomer(id).getEnergyRecords();
		
		if(list.get(0).size() == 0){
			return;
		}
		
		/* Initialize data with zeroes */
		
		temp = start;
		// Fill days from start that have no entry in the customers data.
		while (temp.compareTo(list.get(0).get(0).getStartDate()) < 0 ){
			c.setTime(temp);
		    c.add(Calendar.DATE, 1);
			temp = c.getTime();
			data.add(0.0);
			devices.add(0.0);
			n++;
		}
		for(i=0; i<list.get(0).size(); i++){
			if(list.get(0).get(i).getStartDate().compareTo(start) >= 0  
			  && list.get(0).get(i).getStartDate().compareTo(end) <= 0){
				data.add(0.0);
				devices.add(0.0);
			}
		}
		
		temp = list.get(0).get(list.get(0).size()-1).getStartDate();
		c.setTime(temp);
	    c.add(Calendar.DATE, 1);
	    temp = c.getTime();
		while (temp.compareTo(end) < 0 ){
			c.setTime(temp);
		    c.add(Calendar.DATE, 1);
			temp = c.getTime();
			data.add(0.0);
			devices.add(0.0);
		}
		
		dataSize = new int[data.size()];
		for(i=0; i<dataSize.length; i++){
			dataSize[i] = ecs.size()-1;
		}
		
		/* Fill data of other customers */	
		for(i=0; i<ecs.size(); i++){
			if(id != ecs.get(i).getId()){
				addDataPerson(ecs.get(i).getEnergyRecords());
			}
		}
		
		/* Add own data */
		if(cat == -1){
			for(i=0; i<list.get(0).size(); i++){
				if(list.get(0).get(i).getStartDate().compareTo(start) >= 0  
				  && list.get(0).get(i).getStartDate().compareTo(end) <= 0){	
					double en = 0.0;
					double dev = 0.0;
					for(j=0; j< CAT_COUNT; j++){
						en += list.get(j).get(i).getEnergy();
						dev += list.get(j).get(i).getDevices();
					}
					data.set(n, data.get(n) + en);
					devices.set(n, devices.get(n) + dev);
					n++;
				}
			}
		} else {
			List<EnergyRecord> listcat = list.get(cat);
			for(i=0; i<listcat.size(); i++){
				if(listcat.get(i).getStartDate().compareTo(start) >= 0  
				  && listcat.get(i).getStartDate().compareTo(end) <= 0){
					data.set(n, data.get(n) + listcat.get(i).getEnergy());
					devices.set(n,  devices.get(n) + listcat.get(i).getDevices());
					n++;
				} 
			}
		}
		
		for(i=0; i<data.size(); i++){
			if(data.get(i) > yaxis[2]){
				yaxis[2] = data.get(i);
			}
			if(data.get(i) < yaxis[0]){
				yaxis[0] = data.get(i);
			}
		}
		yaxis[1] = 0;
	}

}
