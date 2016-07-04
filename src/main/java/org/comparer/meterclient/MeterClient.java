package org.comparer.meterclient;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Marijn Scholtens & Bob Reimink
 * This is the main meterclient class to call the frame
 */
public class MeterClient {
	
	public MeterClient() {
		final int port = givePort();
		SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		        new MeterClientFrame(port).setVisible(true);
		      }
		});
	}
	
	public static void main(String[] args) {
		new MeterClient();		
	}
	
	private int givePort() {
    	int port = 0;
    	while(port < 1 || port > 9999) {
    		port = Integer.parseInt(JOptionPane.showInputDialog("Enter a port number (1-9999).", 0));
    	}
    	return port;
    }
}
