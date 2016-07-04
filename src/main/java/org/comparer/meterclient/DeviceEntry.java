package org.comparer.meterclient;

import java.util.Date;

/**
 * @author Marijn Scholtens & Bob Reimink
 * A device entry is an entry that has been sent from the deviceclient to the meterclient
 * It contains for example information about the consumed energy
 * A collection of the device entries, with their respective consumed energies, can be used to calculate a balance
 */
public class DeviceEntry {
	
	private Date dayNumber;
	private String deviceName;
	private int energyRate;
	private double consumedTime;
	
	public DeviceEntry(Date day, String deviceName, int energyRate, double consumedTime) {
		this.dayNumber = day;
		this.deviceName = deviceName;
		this.energyRate = energyRate;
		this.consumedTime = consumedTime;
	}
		
	public Date getDayNumber() {
		return dayNumber;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public int getEnergyRate() {
		return energyRate;
	}
	
	public double getConsumedTime() {
		return consumedTime;
	}	
}
