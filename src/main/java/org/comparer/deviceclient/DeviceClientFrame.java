package org.comparer.deviceclient;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

/**
 * @author Marijn Scholtens & Bob Reimink
 * Here we initialize the frame of the deviceclient and build the panel
 */
@SuppressWarnings("serial")
public class DeviceClientFrame extends JFrame {
	
	private DevicePanel devicePanel;
	private JPanel contentPane;
	private CardLayout cardLayout;
	private Date day;
	private int port;
	private final int width = 600;
	private final int height = 600;
	
	// menu buttons
	private AbstractAction newDevicesAction;
	private AbstractAction loadDevicesAction;
	private AbstractAction saveDevicesAction;
	private AbstractAction quitDevicesAction;
	private AbstractAction addDeviceAction;
	private AbstractAction removeDeviceAction;
	private AbstractAction startSimulateAction;
	private AbstractAction stopSimulateAction;
	private AbstractAction showAboutAction;
	
	public DeviceClientFrame(int day, int port) {
		Calendar c = Calendar.getInstance();
	    c.setTime(new Date());
	    c.add(Calendar.DATE, day);
		this.day = c.getTime();
		this.port = port;
		initGUI();
	}
	
	private void initGUI() {
    	this.setFocusable(true);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.setSize(width, height);
    	this.setResizable(false);
    	this.setTitle("Smart Energy Meters - Devices");
    	
    	contentPane = new JPanel();
    	contentPane.setBackground(Color.decode("#B6B6B6"));
    	cardLayout = new CardLayout();
    	contentPane.setLayout(cardLayout);
    	
    	devicePanel = new DevicePanel(day, port);
    	contentPane.add(devicePanel, "Device Panel");
    	
    	initActions();
    	
    	JMenuBar menuBar = new JMenuBar();
    	JMenu menu1 = new JMenu("File");
    	JMenu menu2 = new JMenu("Devices");
    	JMenu menu3 = new JMenu("Simulation");
    	JMenu menu4 = new JMenu("Other");
    	menuBar.add(menu1);
    	menuBar.add(menu2);
    	menuBar.add(menu3);
    	menuBar.add(menu4);
    	
    	menu1.add(this.newDevicesAction);
    	menu1.add(this.loadDevicesAction);
    	menu1.add(this.saveDevicesAction);
    	menu1.add(this.quitDevicesAction);    	
    	menu2.add(this.addDeviceAction);
    	menu2.add(this.removeDeviceAction);
    	menu3.add(this.startSimulateAction);
    	menu3.add(this.stopSimulateAction);
    	menu4.add(this.showAboutAction);
    	
    	this.setJMenuBar(menuBar);    	
    	this.setContentPane(contentPane);
    	this.setVisible(true);
    }
	
	private void initActions() {
		
		this.newDevicesAction = new AbstractAction("New Devices") {
			public void actionPerformed(ActionEvent arg0) {
				devicePanel.getDeviceModel().stopSimulating();
				devicePanel.getDeviceModel().purgeDevices();
				devicePanel.getDeviceModel().setNewDays(selectStartingDay());
			}
		};
		
		this.loadDevicesAction = new AbstractAction("Load Devices") {
			public void actionPerformed(ActionEvent arg0) {
				devicePanel.getDeviceModel().loadDevices();
			}
		};
		
		this.saveDevicesAction = new AbstractAction("Save Devices") {
			public void actionPerformed(ActionEvent arg0) {
				devicePanel.getDeviceModel().saveDevices();
			}
		};
		
		this.quitDevicesAction = new AbstractAction("Quit") {
		    public void actionPerformed(ActionEvent arg0) {
		    	System.exit(0);
		    }
		};
		
		this.addDeviceAction = new AbstractAction("Add Device") {
			public void actionPerformed(ActionEvent arg0) {
				String name = addDeviceName();
				int energyRate = addDeviceEnergyRate();
				devicePanel.getDeviceModel().addDevice(energyRate, name);
			}
		};
		
		this.removeDeviceAction = new AbstractAction("Remove Device") {
			public void actionPerformed(ActionEvent arg0) {
				int removeIndex = removeDeviceIndex();
				devicePanel.getDeviceModel().removeDevice(removeIndex);
			}
		};
		
		this.startSimulateAction = new AbstractAction("Start Simulation") {
			public void actionPerformed(ActionEvent arg0) {
				devicePanel.getDeviceModel().startSimulating();
			}
		};
		
		this.stopSimulateAction = new AbstractAction("Stop Simulation") {
			public void actionPerformed(ActionEvent arg0) {
				devicePanel.getDeviceModel().stopSimulating();
			}
		};
		
		this.showAboutAction = new AbstractAction("About") {
			//@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Smart Energy Meters - DeviceClient\n\n"
						+ "Net-Computing Spring 2015 - Rijksuniversiteit Groningen\n\n"
						+ "Created by Bob Reimink (S2370190) and Marijn Scholtens (S2344173)");
			}
		};
	}
	
	private String addDeviceName() {
    	String name = null;
    	while(name == null) {
    		name = JOptionPane.showInputDialog("Enter a device name", null);
    	}
    	return name;
    }
	
	private int addDeviceEnergyRate() {
    	int energyRate = 0;
    	while(energyRate < 1 || energyRate > 9999) {
    		energyRate = Integer.parseInt(JOptionPane.showInputDialog("Enter the device's W rate (1-9999).", 0));
    	}
    	return energyRate;
    }
	
	private int removeDeviceIndex() {
		int removeIndex = 0;
		while(removeIndex < 1 || removeIndex > 9999) {
			removeIndex = Integer.parseInt(JOptionPane.showInputDialog("Enter the device number that you wish to remove.", 0));
		}
		return removeIndex;
	}
	
	private int selectStartingDay() {
		int day = 0;
		while(day < 1 || day > 9999) {
			day = Integer.parseInt(JOptionPane.showInputDialog("Select a new starting day (1-9999).", 0));
		}
		return (int)day;
	}
}
