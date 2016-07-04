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
 * This class sets the data of energyconsumption and returns the graph for the logged in customer.
 */
@WebServlet("/PersonalChart")
public class PersonalChart extends Chart {

	private static final long serialVersionUID = 1L;
	
	public PersonalChart(){
		super();
	}
	
	/**
	 * Function calculating the data of the given customer according to a category and
	 * start- and enddate 
	 */
	@Override
	protected void calculateData() {
		Calendar c = Calendar.getInstance();
		int i, j;
		Date temp;
		data.clear();
		devices.clear();
		yaxis[0] = Double.POSITIVE_INFINITY;
		yaxis[2] = Double.NEGATIVE_INFINITY;
		
		if(start.compareTo(end) >= 0){
			return;
		}
		
		List<List<EnergyRecord>> list = EnergyDAO.instance.getCustomer(id).getEnergyRecords();
		
		if(!list.get(0).isEmpty()){
			temp = start;
			// Fill days from start that have no entry in the customers data.
			while (temp.compareTo(list.get(0).get(0).getStartDate()) < 0 ){
				c.setTime(temp);
			    c.add(Calendar.DATE, 1);
				temp = c.getTime();
				data.add(0.0);
				devices.add(0.0);
			}
			if(data.size() > 0){
				yaxis[0] = 0.0;
				yaxis[2] = 0.0;
			}
		}
		if(cat == -1){
			double en, dev;
			for(i=0; i<list.get(0).size(); i++){
				if(list.get(0).get(i).getStartDate().compareTo(start) >= 0  
				  && list.get(0).get(i).getStartDate().compareTo(end) <= 0){	
					 en = 0.0;
					 dev = 0.0;
					for(j=0; j< CAT_COUNT; j++){
						en += list.get(j).get(i).getEnergy();
						dev += list.get(j).get(i).getDevices();
					}
					data.add(en);
					devices.add(dev);
					if(en > yaxis[2]){
						yaxis[2] = en;
					}
					if(en < yaxis[0]){
						yaxis[0] = en;
					}
				}
			}
		} else {
			List<EnergyRecord> listcat = list.get(cat);
			double en;
			for(i=0; i<listcat.size(); i++){
				if(listcat.get(i).getStartDate().compareTo(start) >= 0  
				  && listcat.get(i).getStartDate().compareTo(end) <= 0){
					en = listcat.get(i).getEnergy();
					data.add(en);
					devices.add((double)listcat.get(i).getDevices());
					if(en > yaxis[2]){
						yaxis[2] = en;
					}
					if(en < yaxis[0]){
						yaxis[0] = en;
					}
				}
			}
		}
		
		// Fill days till end that have no entry in the customers data.
		if(!list.get(0).isEmpty()){
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
			if(data.get(data.size()-1) == 0.0){
				if(0.0 > yaxis[2]){
					yaxis[2] = 0.0;
				}
				if(0.0 < yaxis[0]){
					yaxis[0] = 0.0;
				}
			}
		}
		yaxis[1] = (yaxis[2]+yaxis[0])/2;
	}

}
