package org.comparer.deviceclient;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

/**
 * @author Marijn Scholtens & Bob Reimink
 * This is the devicepanel on which we draw our objects, like the list of devices
 * Also we create a devicemodel that keeps track of the constellation of devices
 * And we create a devicecontroller for panel adaptations
 */
@SuppressWarnings("serial")
public class DevicePanel extends JPanel implements Observer {

	private DeviceModel deviceModel = null;
	private DeviceController deviceController;
	private int fontSpace = 17;
	private int fontSize = 14;
	private int recWidth = 40;
	private int mainX = 460;
	private int mainY = 140;
	private String fontType = "Arial";
	
	public DevicePanel(Date day, int port) {
		deviceModel = new DeviceModel(day, port);
		setDeviceModel(deviceModel);
		deviceController = new DeviceController(deviceModel, mainX, mainY, fontSpace, recWidth);
		setDeviceController(deviceController);
	}
	
	public void setDeviceController(DeviceController mousec) {
		this.addMouseListener(mousec);
		this.addMouseMotionListener(mousec);
	}
	
	public void setDeviceModel(DeviceModel deviceModel) {
		if(this.deviceModel != null) {
			this.deviceModel.deleteObserver(this);
		}
		this.deviceModel = deviceModel;
		this.deviceModel.addObserver(this);
	}
	
	public DeviceModel getDeviceModel() {
		return deviceModel;
	}
	
	public void paintComponent(Graphics g) {		
		super.paintComponent(g);
		int x = 30;
		int y = 30;
		int i;
		
		// paint overhead
		int port = deviceModel.getPort();
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
		Date startedDay = deviceModel.getStartedDay();
		Date currentDay = deviceModel.getCurrentDay();
		double time = deviceModel.getTime();
		String portText = "Port: " + port;
		String startingDayText = "Started Day: " + sdfDate.format(startedDay);
		String currentDayText = "Current Day: " + sdfDate.format(currentDay);
		String timeText = "Time: " + time;
		
		g.setFont(new Font(fontType, Font.PLAIN, fontSize));
		g.drawString(portText, x, y);
		y += fontSpace;
		g.drawString(startingDayText, x, y);
		y += fontSpace;
		g.drawString(currentDayText, x, y);
		y += fontSpace;
		g.drawString(timeText, x, y);
	
		
		// paint devices
		ArrayList<TCPClient> devicesList = deviceModel.getDevicesList();
		if(!devicesList.isEmpty()) {
			
			g.setFont(new Font(fontType, Font.BOLD, fontSize));
			y = 120;
			x = 30;
			g.drawString("ID", x, y);
			x = 60;
			g.drawString("Device Name", x, y);
			x = 310;
			g.drawString("W Usage", x, y);
			if(deviceModel.getIsSimulating()) {
				x = mainX;
				g.drawString("Turn On/Off", x, y);
			}
			g.setFont(new Font(fontType, Font.PLAIN, fontSize));
			y = mainY;
			i = 1;
			for(TCPClient element : devicesList) {
				x = 30;
				g.drawString((i + "."), x, y);
				x = 60;
				g.drawString(element.getDeviceName(), x, y);
				x = 310;
				g.drawString(String.valueOf(element.getEnergyRate()), x, y);
				if(deviceModel.getIsSimulating()) {
					x = 460;
					if(element.getIsPlaying()) {
						g.drawString("STOP", x, y);
					} else {
						g.drawString("PLAY" ,x, y);
					}
				}					
				++i;
				y += fontSpace;
			}
		}
	}
	
    public void update(Observable arg0, Object arg1) {
    	repaint();
    }
}
