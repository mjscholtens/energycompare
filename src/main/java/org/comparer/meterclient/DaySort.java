package org.comparer.meterclient;

import java.util.Comparator;

/**
 * @author Marijn Scholtens & Bob Reimink
 * Sort the device entries on day
 */
public class DaySort implements Comparator<DeviceEntry> {
	public int compare(DeviceEntry d1, DeviceEntry d2) {
		if(d1.getDayNumber().after(d2.getDayNumber())) {
			return 1;
		} else {
			return -1;
		}
	}
}
