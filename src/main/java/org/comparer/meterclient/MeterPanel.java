package org.comparer.meterclient;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

/**
 * @author Marijn Scholtens & Bob Reimink
 * In this panel we draw the meter balance
 * Also we create the metermodel and a tcpserver
 */
@SuppressWarnings("serial")
public class MeterPanel extends JPanel implements Observer {

	private MeterModel meterModel = null;
	private int fontSize1 = 14;
	private int fontSize2 = 48;
	private int fontSpace = 17;
	private String fontType = "Arial";
	private TCPServer server;
	
	public MeterPanel(int port) {
		meterModel = new MeterModel(port);
		setMeterModel(meterModel);
		server = new TCPServer(meterModel);
	}
	
	public TCPServer getTCPServer() {
		return server;
	}
	
	public void setMeterModel(MeterModel meterModel) {
		if(this.meterModel != null) {
			this.meterModel.deleteObserver(this);
		}
		this.meterModel = meterModel;
		this.meterModel.addObserver(this);
	}
	
	public MeterModel getMeterModel() {
		return meterModel;
	}
	
	public void paintComponent(Graphics g) {		
		super.paintComponent(g);
		
		// draw port
		int x = 30;
		int y = 30;
		g.setFont(new Font(fontType, Font.PLAIN, fontSize1));
		int port = meterModel.getPort();
		int houseNr = meterModel.getId();
		String portText = "Port: " + port;
		String houseText = "HouseNr: " + houseNr;
		g.drawString(portText, x, y);
		y += fontSpace;
		g.drawString(houseText, x, y);
		
		// draw balance
		x = 30;
		y = 180;
		g.setFont(new Font(fontType, Font.BOLD, fontSize2));
		g.drawString(Integer.toString(meterModel.getBalance()) + " W", x, y);	
	}
	
    public void update(Observable arg0, Object arg1) {
    	repaint();
    }
}
