package org.comparer.deviceclient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author Marijn Scholtens & Bob Reimink
 * This is the devicemodel that contains a list of all current devices
 * Also keeps track of several other model properties, and the simulation process
 * Create here new devices (as tcpclients) when adding a new device and add to the list
 */
public class DeviceModel extends Observable {

	private Date startedDay;
	private Date currentDay;
	private int port;
	private double time;
	private ArrayList<TCPClient> devicesList = new ArrayList<TCPClient>();
	private boolean isSimulating;
	
	public DeviceModel(Date day, int port) {
		this.startedDay = day;
		this.currentDay = day;
		this.port = port;
		time = 0;
		setChanged();
		notifyObservers();
	}	

	public int getPort() {
		return port;
	}
	
	public Date getCurrentDay() {
		return currentDay;
	}
	
	public Date getStartedDay() {
		return startedDay;
	}
	
	public double getTime() {
		return time;
	}
	
	public void addDevice(int energyRate, String deviceName) {
		devicesList.add(new TCPClient(deviceName, energyRate, port));
		setChanged();
		notifyObservers();
	}
	
	public void removeDevice(int index) {
		if(index > devicesList.size()) {
			JOptionPane.showMessageDialog(null, "Error: Invalid index. No device has been removed.");
		} else {
			devicesList.remove(index-1);
			setChanged();
			notifyObservers();
		}
	}
	
	public void purgeDevices() {
		devicesList.clear();
		setChanged();
		notifyObservers();
	}
	
	public ArrayList<TCPClient> getDevicesList() {
		return devicesList;
	}
	
	public boolean getIsSimulating() {
		return isSimulating;
	}
	
	public void turnOnTimer() {
		final Calendar c = Calendar.getInstance();
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	    executorService.scheduleAtFixedRate(new Runnable() {
	        public void run() {
	        	if(isSimulating) {
		        	time += 0.5;
					if(time == 24) {
						submitDayResults();
						time = 0;
						c.setTime(currentDay);
					    c.add(Calendar.DATE, 1);
						currentDay = c.getTime();
					}
					setChanged();
					notifyObservers();
	        	} else {
	        		executorService.shutdown();
	        	}
	        }
	    }, 0, 500, TimeUnit.MILLISECONDS);
	}
	
	public void startSimulating() {
		isSimulating = true;
		turnOnTimer();
		setChanged();
		notifyObservers();
	}
	
	public void stopSimulating() {
		isSimulating = false;
		turnOffSimulations();
		setChanged();
		notifyObservers();
	}
	
	public void submitDayResults() {
		for(TCPClient element : devicesList) {
			if(element.getIsPlaying()) {
				element.submitDay(currentDay);
			}
		}
	}
	
	public void turnOffSimulations() {
		for(TCPClient element : devicesList) {
			element.stopDevice(currentDay);
		}
		setChanged();
		notifyObservers();
	}
	
	public void setNewDays(int day) {
		Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    c.add(Calendar.DATE, day);
		this.startedDay = c.getTime();
		this.currentDay = c.getTime();
		setChanged();
		notifyObservers();
	}
	
	public void saveDevices() {
		String fileName = enterSaveFileName();
		fileName += ".txt";
		BufferedWriter writer = null;
		JFileChooser saveFile = new JFileChooser();
		saveFile.setSelectedFile(new File(fileName));
		int sf = saveFile.showSaveDialog(saveFile);
		
		if(sf == JFileChooser.APPROVE_OPTION) {
			try {
				writer = new BufferedWriter(new FileWriter(saveFile.getSelectedFile()));				
				writer.write(startedDay + "\n"); // starting day
				writer.write(currentDay + "\n"); // current day
				writer.write(time + "\n"); // the time
				for(TCPClient element : devicesList) {
					writer.write(element.getDeviceName() + " " + element.getEnergyRate() + "\n");
				}				
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Saving devices failed: Exception 1 (Bad file).");
			} finally {
				try {
					if(writer != null) {
						writer.close();
						JOptionPane.showMessageDialog(null, "Saving devices successful.");
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Saving devices failed: Exception 2 (Failed close).");
				}
			}			
		} else if(sf == JFileChooser.CANCEL_OPTION){
			JOptionPane.showMessageDialog(null, "Saving devices failed: cancelled.");
		}
	}
	
	private String enterSaveFileName() {
    	String name = null;
    	while(name == null) {
    		name = JOptionPane.showInputDialog("Enter a save file name without extension", null);
    	}
    	return name;
    }
	
	public void loadDevices() {		
		BufferedReader reader = null;
		JFileChooser loadFile = new JFileChooser();
		int lf = loadFile.showOpenDialog(loadFile);
		boolean loadSuccess = false;
		
		if(lf == JFileChooser.APPROVE_OPTION) {
			try {
				reader = new BufferedReader(new FileReader(loadFile.getSelectedFile()));
				String propDevices = loadFile.getSelectedFile().toString();
				
				// check for correctness
				if(!propDevices.endsWith(".txt")) {
					JOptionPane.showMessageDialog(null, "Loading devices failed: Input file must be of type .txt");
				} else {
					stopSimulating();
					purgeDevices();
					this.port = givePort();
					loadSuccess = loadSetDevices(reader);				
				}
				
				// exception handling and closing
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Loading devices failed: Exception 1 (Bad file).");
			} finally {
				try {
					if(reader != null) {
						reader.close();
						if(loadSuccess) {
							JOptionPane.showMessageDialog(null, "Loading devices successful.");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Loading devices failed: Exception 2 (Failed close).");
				}
			}			
		} else if(lf == JFileChooser.CANCEL_OPTION){
			JOptionPane.showMessageDialog(null, "Loading devices failed: cancelled.");
		}
	}
	
	private int givePort() {
    	int portNr = 0;
    	while(portNr < 1 || portNr > 9999) {
    		portNr = Integer.parseInt(JOptionPane.showInputDialog("Enter a port number (1-9999).", 0));
    	}
    	return portNr;
    }
	
	private boolean loadSetDevices(BufferedReader reader) throws IOException {
		
		try {
			this.startedDay = new Date(Long.parseLong(reader.readLine()));
			this.currentDay = new Date(Long.parseLong(reader.readLine()));
			this.time = Double.parseDouble(reader.readLine());
			int enRate;
			String propName;
					
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {				
				String[] parts = line.split(" ");
				propName = parts[0];
				enRate = Integer.parseInt(parts[1]);
				addDevice(enRate, propName);
			}
			
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
