package org.comparer.meterclient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author Marijn Scholtens & Bob Reimink
 * This is the metermodel that keeps track of device entries
 */
public class MeterModel extends Observable {

	protected int port;
	private ArrayList<DeviceEntry> deviceEntries = new ArrayList<DeviceEntry>();
	private int balance;
	private int id;
	private int index;
	
	public MeterModel(int port) {
		this.id = 0;
		this.port = port;
		balance = 0;
		setChanged();
		notifyObservers();
		index = 0;
	}
	
	public void setId(int number) {
		this.id = number;
		setChanged();
		notifyObservers();
	}
	
	public int getId() {
		return id;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public void resetBalance() {
		index = 0;
		balance = 0;
		setChanged();
		notifyObservers();
	}
	
	public ArrayList<DeviceEntry> getDeviceEntries() {
		return deviceEntries;
	}
	
	public synchronized void addDeviceEntry(Date day, String deviceName, int energyRate, double consumedTime) {
		deviceEntries.add(new DeviceEntry(day, deviceName, energyRate, consumedTime));
		calculateBalance();
		setChanged();
		notifyObservers();
	}
	
	public void calculateBalance() {
		int i;
		// for consumedTime, one second equalizes one hour
		double subBalance = 0;
		for(i=index; i< deviceEntries.size(); i++){
			subBalance += (double)deviceEntries.get(i).getEnergyRate() * deviceEntries.get(i).getConsumedTime(); 
		}
		index = i;
		balance += (int)subBalance;
		setChanged();
		notifyObservers();
	}
	
	public void resetIndex(){
		index = 0;
	}
	
	public void purgeDeviceEntries() {
		deviceEntries.clear();
		setChanged();
		notifyObservers();
	}
	
	public void newBalance() {
		purgeDeviceEntries();
		resetBalance();
	}
	
	public void saveBalance() {
		String fileName = enterSaveFileName();
		fileName += ".txt";
		BufferedWriter writer = null;
		JFileChooser saveFile = new JFileChooser();
		saveFile.setSelectedFile(new File(fileName));
		int sf = saveFile.showSaveDialog(saveFile);
		
		if(sf == JFileChooser.APPROVE_OPTION) {
			try {
				writer = new BufferedWriter(new FileWriter(saveFile.getSelectedFile()));				
				writer.write(balance + "\n"); // balance
				for(DeviceEntry element : deviceEntries) {
					writer.write(element.getDayNumber().getTime() + " " + element.getDeviceName()
							+ " " + element.getEnergyRate() + " " + element.getConsumedTime() + "\n");
				}				
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Saving balance failed: Exception 1 (Bad file).");
			} finally {
				try {
					if(writer != null) {
						writer.close();
						JOptionPane.showMessageDialog(null, "Saving balance successful.");
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Saving balance failed: Exception 2 (Failed close).");
				}
			}			
		} else if(sf == JFileChooser.CANCEL_OPTION){
			JOptionPane.showMessageDialog(null, "Saving balance failed: cancelled.");
		}
	}
	
	private String enterSaveFileName() {
    	String name = null;
    	while(name == null) {
    		name = JOptionPane.showInputDialog("Enter a save file name without extension", null);
    	}
    	return name;
    }
	
	public void loadBalance() {		
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
					JOptionPane.showMessageDialog(null, "Loading balance failed: Input file must be of type .txt");
				} else {
					this.port = givePort();
					loadSuccess = loadSetDevices(reader);				
				}
				
				// exception handling and closing
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Loading balance failed: Exception 1 (Bad file).");
			} finally {
				try {
					if(reader != null) {
						reader.close();
						if(loadSuccess) {
							JOptionPane.showMessageDialog(null, "Loading balance successful.");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Loading balance failed: Exception 2 (Failed close).");
				}
			}			
		} else if(lf == JFileChooser.CANCEL_OPTION){
			JOptionPane.showMessageDialog(null, "Loading devices failed: cancelled.");
		}
	}
	
	private int givePort() {
    	int portNr = 0;
    	while(portNr < 1 || portNr > 9999) {
    		port = Integer.parseInt(JOptionPane.showInputDialog("Enter a port number (1-9999).", 0));
    	}
    	return portNr;
    }
	
	private boolean loadSetDevices(BufferedReader reader) throws IOException {
		try {
			this.balance = Integer.parseInt(reader.readLine());
			Date dayNr;
			String deviceName;
			int enRate;			
			double consumedTime;
					
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {				
				String[] parts = line.split(" ");
				dayNr = new Date(Long.parseLong(parts[0]));
				deviceName = parts[1];
				enRate = Integer.parseInt(parts[2]);
				consumedTime = Integer.parseInt(parts[3]);
				addDeviceEntry(dayNr, deviceName, enRate, consumedTime);
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
