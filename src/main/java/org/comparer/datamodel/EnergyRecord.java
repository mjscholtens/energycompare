package org.comparer.datamodel;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This class represents one energy record.
 * @author Marijn Scholtens & Bob Reimink
 *
 */
@XmlRootElement(name="record")
public class EnergyRecord {
	
	private double energy;
	
	private int devices;

	private Date startDate;
	
	public double getEnergy() {
		return energy;
	}

	@XmlElement
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	
	public int getDevices() {
		return devices;
	}

	@XmlElement
	public void setDevices(int devices) {
		this.devices = devices;
	}
	public Date getStartDate() {
		return startDate;
	}
	
	@XmlJavaTypeAdapter(DateAdapter.class)
	@XmlElement
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	
}
