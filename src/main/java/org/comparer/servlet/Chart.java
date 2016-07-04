package org.comparer.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.comparer.constant.Constants.*;

/**
 * 
 * @author Marijn Scholtens & Bob Reimink
 * Abstract class for drawing the chart using data of which the implementation will be done
 * in subclasses.
 */
public abstract class Chart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Some data for the chart.
	 */
	private static int W = 500, H = 380;
	private static int W_GRAPH = 440, H_GRAPH = 300; 
	protected int id, cat;
	protected Date start, end;
	
	private int leftOffset = 30, topOffset = 60;
	private double interval, difference;
	
	protected List<Double> data;
	
	protected List<Double> devices;
	
	protected double[] yaxis;
	
	public Chart() {
		super();
		yaxis = new double[3];
		data = new ArrayList<Double>();
		devices = new ArrayList<Double>();
	}
	
	/**
	 * The actual drawing of the chart using the data.
	 * @param response image of chart.
	 */
	protected void drawChart(HttpServletResponse response) throws IOException{
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
		int i;
		double x = leftOffset;
		int y1 = 0, y2 = 0;
		
	    response.setContentType("image/jpeg");
	    
	    // Create an Image
	    BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

	    // Get the Image's Graphics, and draw.
	    Graphics2D g = img.createGraphics();

	    // In real life this would call some charting software...
	    g.setColor(Color.white);
	    g.fillRect(0,0, W, H);
	    g.setColor(Color.black);
	    g.setFont(new Font("Serif", Font.BOLD, 15));
	    if(cat == -1){
	    	g.drawString("All Categories", 5, 15);
	    } else {
	    	g.drawString("Category: " + CATEGORIES[cat] + "-" + CATEGORIES[cat+1], 5, 15);
	    }
	    if(data.size() > 0 && difference != 0){
		    g.drawLine(leftOffset, topOffset, leftOffset, topOffset + H_GRAPH);
		    g.drawLine(leftOffset, topOffset+H_GRAPH, leftOffset+W_GRAPH, topOffset+H_GRAPH);
		    g.setFont(new Font("Serif", Font.BOLD, 10));
		    g.drawString(sdfDate.format(start), 20, H-10);
		    g.drawString(sdfDate.format(end), W-70, H-10);

		    g.drawString(Integer.toString((int)yaxis[2]), 5, topOffset);
		    y1 = (int)((1-(yaxis[1]-yaxis[0])/difference)*H_GRAPH);
		    g.drawString(Integer.toString((int)yaxis[1]), 5, topOffset+ y1);
		    g.drawString(Integer.toString((int)yaxis[0]), 5, topOffset+H_GRAPH);
		    
		    // Draw zero line
		    if(yaxis[1] == 0){
		    	g.drawLine(5, topOffset + y1, leftOffset+W_GRAPH, topOffset + y1);
		    }
		    
		    // Draw the graph
		    for(i=0; i<data.size()-1; i++){
		    	if(data.get(i) == Double.POSITIVE_INFINITY){
		    		continue;
		    	}
		    	y1 = (int)((1 - (data.get(i) - yaxis[0])/(difference))*H_GRAPH);
		    	g.drawOval((int)x-2, topOffset+y1-2, 4, 4);
		    	g.drawString(Double.toString(devices.get(i)), (int)x-10, 30);
		    	if(data.get(i+1) == Double.POSITIVE_INFINITY){
		    		break;
		    	}
		    	y2 = (int)((1 - (data.get(i+1)- yaxis[0])/(difference))*H_GRAPH);
		    	
		    	g.drawLine((int)x, topOffset+y1, (int)(x + interval), topOffset + y2);
		    	x = x + interval;
		    }
		    if(data.get(i) != Double.POSITIVE_INFINITY){
		    	g.drawOval((int)x-2, topOffset+y2-2, 4, 4);
		    	g.drawString(Double.toString(devices.get(i)), (int)x-10, 30);
		    }
	    } else {
	    	g.drawString("No data available", leftOffset+W_GRAPH/3, topOffset+H_GRAPH/2);
	    }
	    g.dispose();
	    // Write the output
	    OutputStream os = response.getOutputStream();
	    ImageOutputStream ios = ImageIO.createImageOutputStream(os);
	    
	    ImageIO.write(img, "jpeg", ios);

      ios.close();
      os.close();
	}
	
  /**
   *  Function that resturns a graphical chart to request 
   */
  public void doGet(HttpServletRequest request,
    HttpServletResponse response)
  throws IOException {
	  id = Integer.parseInt(request.getParameter("id"));
	  cat = Integer.parseInt(request.getParameter("cat"));
	  start = new Date(Long.parseLong(request.getParameter("start")));
	  end = new Date(Long.parseLong(request.getParameter("end")));
	  
	  calculateData();
	  if(data.size() > 1){
		  interval = W_GRAPH/(data.size()-1);
	  }
	  difference = yaxis[2]-yaxis[0];
	  drawChart(response);
  }
  
  /**
   * Functions which will calculate the data.
   */
  protected abstract void calculateData();
}