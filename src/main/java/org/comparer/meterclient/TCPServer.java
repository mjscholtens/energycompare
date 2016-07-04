package org.comparer.meterclient;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/* Categories used: 0-15, 15-50, 50-100, 100-250, 250-500, 500-1000, 1000+
 * These categories are hard-coded (0-6)
 */

/**
 * @author Marijn Scholtens & Bob Reimink
 * With the TCPServer we receive messages from the clients about the energy consumption and add device entries from them
 * Also we can submit the current metermodel constellation to the RabbitMQ with submitBalance()
 */
public class TCPServer implements Runnable {

	private int port = 0;
	private String connectionserver = "http://localhost:8080/EnergyCompare/rest/energyrecs";
	private MeterModel meterModel = null;
	private static final String QUEUE_NAME = "en_queue";

	
	public TCPServer(MeterModel meterModel) {
		this.meterModel = meterModel;
		this.port = meterModel.getPort();
		retrieveId();
		new Thread(this).start();
	}
	
	public void submitBalance() throws java.io.IOException {

		ArrayList<DeviceEntry> devicesList = meterModel.getDeviceEntries();
		if(devicesList.size() == 0){
			return;
		}
		Calendar c = Calendar.getInstance();
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);		
		ArrayList<String> deviceNames = new ArrayList<String>();
		
		String message = "" + meterModel.getId();
		devicesList = sortDays(devicesList);
		Date first = devicesList.get(0).getDayNumber();
		Date last = devicesList.get(devicesList.size()-1).getDayNumber();
		int catB0 = 0, catB1 = 0, catB2 = 0, catB3 = 0, catB4 = 0, catB5 = 0, catB6 = 0;
		int catD0 = 0, catD1 = 0, catD2 = 0, catD3 = 0, catD4 = 0, catD5 = 0, catD6 = 0;
		
		while(first.compareTo(last) <= 0) {
			message += " " + Long.toString(first.getTime());
			catB0 = 0; catB1 = 0; catB2 = 0; catB3 = 0; catB4 = 0; catB5 = 0; catB6 = 0;
			catD0 = 0; catD1 = 0; catD2 = 0; catD3 = 0; catD4 = 0; catD5 = 0; catD6 = 0;
			for(DeviceEntry element : devicesList) {
				if(element.getDayNumber().compareTo(first) == 0) {
					
					int rate = element.getEnergyRate();
					if(rate <= 15) {
						catB0 += (int)((double)rate * element.getConsumedTime());
						if(!namePresent(element.getDeviceName(), deviceNames)) {
							deviceNames.add(element.getDeviceName());
							++catD0;
						}
					} else if(rate <= 50) {
						catB1 += (int)((double)rate * element.getConsumedTime());
						if(!namePresent(element.getDeviceName(), deviceNames)) {
							deviceNames.add(element.getDeviceName());
							++catD1;
						}
					} else if(rate <= 100) {
						catB2 += (int)((double)rate * element.getConsumedTime());
						if(!namePresent(element.getDeviceName(), deviceNames)) {
							deviceNames.add(element.getDeviceName());
							++catD2;
						}
					} else if(rate <= 250) {
						catB3 += (int)((double)rate * element.getConsumedTime());
						if(!namePresent(element.getDeviceName(), deviceNames)) {
							deviceNames.add(element.getDeviceName());
							++catD3;
						}
					} else if(rate <= 500) {
						catB4 += (int)((double)rate * element.getConsumedTime());
						if(!namePresent(element.getDeviceName(), deviceNames)) {
							deviceNames.add(element.getDeviceName());
							++catD4;
						}
					} else if(rate <= 1000) {
						catB5 += (int)((double)rate * element.getConsumedTime());
						if(!namePresent(element.getDeviceName(), deviceNames)) {
							deviceNames.add(element.getDeviceName());
							++catD5;
						}
					} else {
						catB6 += (int)((double)rate * element.getConsumedTime());
						if(!namePresent(element.getDeviceName(), deviceNames)) {
							deviceNames.add(element.getDeviceName());
							++catD6;
						}
					}					
				}
			}
			message += " " + catB0 + " " + catD0 + " " + catB1 + " " + catD1 + " " + catB2 + " " + catD2 + " " + catB3
					+ " " + catD3 + " " + catB4 + " " + catD4 + " " + catB5 + " " + catD5 + " " + catB6 + " " + catD6;
			deviceNames.clear();
			meterModel.resetIndex();
			c.setTime(first);
			c.add(Calendar.DATE, 1);
			first = c.getTime();
		}
		devicesList.clear();
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());		
		channel.close();
		connection.close();
	}
	
	public boolean namePresent(String name, ArrayList<String> deviceNames) {
		for(String s : deviceNames) {
			if(s.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<DeviceEntry> sortDays(ArrayList<DeviceEntry> devices) {
		Collections.sort(devices,new DaySort());
		return devices;
	}
	
	private void retrieveId() {
		int number = 0;
		int response;
		String name = enterName();
		
		HttpClient httpclient = new HttpClient();
		StringBuilder requestUrl = new StringBuilder(connectionserver);
		requestUrl.append("/" + name);
		GetMethod httpGet = new GetMethod(requestUrl.toString());
		
		try {
			response = httpclient.executeMethod(httpGet);
			if(response == 200) {
				number = Integer.parseInt(httpGet.getResponseBodyAsString());
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		meterModel.setId(number);
	}
	
	private String enterName() {
    	String name = null;
    	while(name == null) {
    		name = JOptionPane.showInputDialog("Please enter your name", null);
    	}
    	return name;
    }
	
	@SuppressWarnings("resource")
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);			
			while(true) {
				Socket clientSocket = serverSocket.accept();
				new ConnectionThread(clientSocket);
		    }		    
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void receiveDeviceEntry(Date day, String deviceName, int energyRate, double consumedTime) {
		meterModel.addDeviceEntry(day, deviceName, energyRate, consumedTime);
	}
		
	class ConnectionThread extends Thread {
		DataInputStream in;
		DataOutputStream out;
		Socket clientSocket;
		
		public ConnectionThread(Socket aSocket) {
			try {
				clientSocket = aSocket;
				in = new DataInputStream(clientSocket.getInputStream());
				this.start();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		public void run() {
		
			try {
				String data = in.readUTF();
				String[] parts = data.split(" ");

				receiveDeviceEntry(new Date(Long.parseLong(parts[0])), parts[1], Integer.parseInt(parts[2]), Double.parseDouble(parts[3]));
				
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}finally {
				try {
					clientSocket.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}
	}
}