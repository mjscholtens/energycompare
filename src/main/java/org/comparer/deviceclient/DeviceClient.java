package org.comparer.deviceclient;

import javax.swing.JOptionPane;

/**
 * @author Marijn Scholtens & Bob Reimink
 * This is the main class of the device client where the client is started and the frame launched
 */
public class DeviceClient {
	
	public DeviceClient() {
		int day = giveDay();
		int port = givePort();
		new DeviceClientFrame(day, port);
	}
	
	public static void main(String[] args) {
		new DeviceClient();		
	}
	
	private int giveDay() {
		int day = 0;
		while(day < 1 || day > 9999) {
			day = Integer.parseInt(JOptionPane.showInputDialog("Which day is it today (1-9999).", 0));
		}
		return day;
	}
	
	private int givePort() {
    	int port = 0;
    	while(port < 1 || port > 9999) {
    		port = Integer.parseInt(JOptionPane.showInputDialog("Enter a port number (1-9999).", 0));
    	}
    	return port;
    }
}
