package org.comparer.meterclient;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.*;

/**
 * @author Marijn Scholtens & Bob Reimink
 * Here we create the meterclient frame, also here we initialize the panel
 */
@SuppressWarnings("serial")
public class MeterClientFrame extends JFrame {
	
	private MeterPanel meterPanel;
	private JPanel contentPane;
	private CardLayout cardLayout;
	private int port;
	private final int width = 400;
	private final int height = 400;
	
	// menu buttons
	private AbstractAction newBalanceAction;
	private AbstractAction loadBalanceAction;
	private AbstractAction saveBalanceAction;
	private AbstractAction submitBalanceAction;
	private AbstractAction quitBalanceAction;
	private AbstractAction showAboutAction;
	
	public MeterClientFrame(int port) {
		this.port = port;
		initGUI();
	}
	
	private void initGUI() {
    	this.setFocusable(true);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.setSize(width, height);
    	this.setResizable(false);
    	this.setTitle("Smart Energy Meters - Meter");
    	
    	contentPane = new JPanel();
    	contentPane.setBackground(Color.decode("#B6B6B6"));
    	cardLayout = new CardLayout();
    	contentPane.setLayout(cardLayout);
    	
    	meterPanel = new MeterPanel(port);
    	contentPane.add(meterPanel, "Meter Panel");
    	
    	initActions();
    	
    	JMenuBar menuBar = new JMenuBar();
    	JMenu menu1 = new JMenu("File");
    	JMenu menu2 = new JMenu("Other");
    	menuBar.add(menu1);
    	menuBar.add(menu2);
    	
    	menu1.add(this.newBalanceAction);
    	menu1.add(this.loadBalanceAction);
    	menu1.add(this.saveBalanceAction);
    	menu1.add(this.submitBalanceAction);
    	menu1.add(this.quitBalanceAction);
    	menu2.add(this.showAboutAction);
    	
    	this.setJMenuBar(menuBar);    	
    	this.setContentPane(contentPane);
    	this.setVisible(true);
    }
	
	private void initActions() {
		
		this.newBalanceAction = new AbstractAction("New Balance") {
			public void actionPerformed(ActionEvent arg0) {
				meterPanel.getMeterModel().newBalance();
			}
		};
		
		this.loadBalanceAction = new AbstractAction("Load Balance") {
			public void actionPerformed(ActionEvent arg0) {
				meterPanel.getMeterModel().loadBalance();
			}
		};
		
		this.saveBalanceAction = new AbstractAction("Save Balance") {
			public void actionPerformed(ActionEvent arg0) {
				meterPanel.getMeterModel().saveBalance();
			}
		};
		
		this.submitBalanceAction = new AbstractAction("Submit Balance") {
			public void actionPerformed(ActionEvent arg0) {
				try {
					meterPanel.getTCPServer().submitBalance();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		this.quitBalanceAction = new AbstractAction("Quit") {
		    public void actionPerformed(ActionEvent arg0) {
		    	System.exit(0);
		    }
		};
		
		this.showAboutAction = new AbstractAction("About") {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Smart Energy Meters - MeterClient\n\n"
						+ "Net-Computing Spring 2015 - Rijksuniversiteit Groningen\n\n"
						+ "Created by Bob Reimink (S2370190) and Marijn Scholtens (S2344173)");
			}
		};
	}
}
