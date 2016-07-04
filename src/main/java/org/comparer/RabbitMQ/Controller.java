package org.comparer.RabbitMQ;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marijn Scholtens & Bob Reimink
 * Class which will create the workers for the RabbitMQ
 */
public class Controller {
	
	static List<Worker> workers;
	private static final int AMOUNT = 1;
	private static String address = "http://localhost:8080/EnergyCompare/rest/energyrecs";
	
	public static void main(String[] args) {
		int i;
		
		workers = new ArrayList<Worker>();
		for(i=0; i<AMOUNT; i++){
			workers.add(new Worker(address));
		}
	}

}
