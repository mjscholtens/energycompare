package org.comparer.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Databse object that contains the customers in the servlet.
 * @author Marijn Scholtens & Bob Reimink
 *
 */
public enum EnergyDAO {
	instance;
	
	private List<EnergyCustomer> customers = new ArrayList<EnergyCustomer>();
	
	private EnergyDAO() { }
	
	public List<EnergyCustomer> getCustomers(){
		return customers;
	}
	
	public EnergyCustomer getCustomer(int id){
		for(int i=0; i<customers.size(); i++){
			if(customers.get(i).getId() == id){
				return customers.get(i);
			}
		}
		return null;
	}
}
