package org.comparer.deviceclient;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/**
 * @author Marijn Scholtens & Bob Reimink
 * The devicecontroller is used for toggling on and off the devices in a simulation mode
 */
public class DeviceController extends MouseAdapter {
	
	DeviceModel deviceModel;
	private int drawX;
	private int drawY;
	private int fontSpace;
	private int recWidth;
	
	public DeviceController(DeviceModel deviceModel, int drawX, int drawY, int fontSpace, int recWidth) {
		this.deviceModel = deviceModel;
		this.drawX = drawX;
		this.drawY = drawY;
		this.fontSpace = fontSpace;
		this.recWidth = recWidth;
	}
	
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		int index = 0;
		
			if(SwingUtilities.isLeftMouseButton(e) && !e.isConsumed()) {
				e.consume();
				if(deviceModel.getIsSimulating()) {
					for(TCPClient element : deviceModel.getDevicesList()) {
						if(element.contains(p, index, drawX, drawY, fontSpace, recWidth)) {
							if(element.getIsPlaying()) {
								element.stopDevice(deviceModel.getCurrentDay());
							} else {
								element.startDevice();
							}
						}
						++index;
					}
				}
			}
			
		Component source = (Component)e.getSource();
		source.repaint();
	}
}
