package org.comparer.deviceclient;
import java.awt.Point;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Marijn Scholtens & Bob Reimink
 * A device object contains the main and simulation properties of the respective device
 */
public abstract class Device extends Thread {
	
	private String deviceName;
	private int energyRate;
	private boolean isPlaying = false;
	private double playTime;
	
	public Device(String deviceName, int energyRate) {
		this.deviceName = deviceName;
		this.energyRate = energyRate;
	}
	public String getDeviceName() {
		return deviceName;
	}
	
	public int getEnergyRate() {
		return energyRate;
	}
	
	public boolean getIsPlaying() {
		return isPlaying;
	}
	
	public void startDevice() {
		isPlaying = true;
		turnOnTimer();
	}
	
	public void stopDevice(Date day) {
		isPlaying = false;
		if(playTime != 0.0){
			submitEntry(day, deviceName, energyRate, playTime);
		}
		resetTime();
	}
	
	public void submitDay(Date day) {
		if(playTime != 0.0){
			submitEntry(day, deviceName, energyRate, playTime);
			resetTime();
		}
	}
	
	public abstract void submitEntry(Date day, String deviceName, int energyRate, double playTime);
	
	public void resetTime() {
		playTime = 0.0;
	}
	
	public void turnOnTimer() {
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	    executorService.scheduleAtFixedRate(new Runnable() {
	        public void run() {
	        	if(isPlaying) {
		        	playTime += 0.5;
	        	} else {
	        		executorService.shutdown();
	        	}
	        }
	    }, 0, 500, TimeUnit.MILLISECONDS);
	}
	
	public boolean contains(Point p, int index, int drawX, int drawY, int fontSpace, int recWidth) {
		double pointX = p.getX();
		double pointY = p.getY();
		double leftX = (double)drawX;
		double upY = (double)drawY;
		double recHeight = (double)fontSpace;
		double deviceIndex = (double)index;
		upY += (deviceIndex-1) * recHeight;
		double rightX = leftX + recWidth;
		double bottomY = upY + recHeight;
		
		// check if selected point is within the rectangle starting from (leftX, upY) with width 'width' and height space
		if((pointX > leftX && pointX < rightX) && (pointY > upY && pointY < bottomY)) {
			return true;
		} else {
			return false;
		}
	}
}