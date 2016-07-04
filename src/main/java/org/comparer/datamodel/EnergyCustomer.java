package org.comparer.datamodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class representing the customer
 * @author Marijn Scholtens & Bob Reimink
 *
 */
public class EnergyCustomer {

	private final static int CATEGORIES = 7;
	
	private int id;
	private String name;
	private Date startDate;
	private List<List<EnergyRecord>> energierecs = new ArrayList<List<EnergyRecord>>();
	
	public EnergyCustomer(int id, String name){
		for(int i=0; i<CATEGORIES; i++){
			energierecs.add(new ArrayList<EnergyRecord>());
		}
		this.id = id;
		this.name = name;
		this.startDate = new Date();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void addEnergyRecord(EnergyRecord e, int category){
		energierecs.get(category).add(e);
	}
	
	public List<List<EnergyRecord>> getEnergyRecords(){
		return energierecs;
	}
}
