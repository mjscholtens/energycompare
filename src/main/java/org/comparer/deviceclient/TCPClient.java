package org.comparer.deviceclient;
import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * @author Marijn Scholtens & Bob Reimink
 * This is a subclass of the device, containing the abilities to send information to the tcpserver of the meterclient
 */
public class TCPClient extends Device implements Runnable {
	
	static String SERVER_ADDRESS = "localhost";
	int SERVER_PORT = 7374;
	Socket s = null;
	DataOutputStream out;
	DataInputStream in;
	
	private Date tempDay;
	private String tempDeviceName;
	private int tempEnergyRate;
	private double tempPlayTime;
	private boolean submitNow = false;
	
	public TCPClient(String deviceName, int energyRate, int port) {
		super(deviceName, energyRate);
		SERVER_PORT = port;
	}

	@Override
	public void submitEntry(Date day, String deviceName, int energyRate, double playTime) {
		this.tempDay = day;
		this.tempDeviceName = deviceName;
		this.tempEnergyRate = energyRate;
		this.tempPlayTime = playTime;
		submitNow = true;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			if(submitNow) {
				s = new Socket(SERVER_ADDRESS, SERVER_PORT);
				out = new DataOutputStream(s.getOutputStream());
				out.writeUTF(Long.toString(tempDay.getTime()) + " " + tempDeviceName + " " + tempEnergyRate + " " + tempPlayTime);
				submitNow = false;
			}
		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if(s != null) {
				try {
					s.close();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
		}
	}
}
