package org.comparer.RabbitMQ;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import static org.comparer.constant.Constants.*;

/**
 * 
 * @author Marijn Scholtens & Bob Reimink
 * Worker which will retrieve messages from the queue and create messages
 * for the webservice.
 */
public class Worker implements Runnable {
	private static final String ENERGY_QUEUE = "en_queue";
	String address;
	HttpClient client;
	
	/**
	 * Constructor starting the thread and a httpclient. 
	 * @param address of webservice.
	 */
	public Worker(String address){
		this.address = address;	
		new Thread(this).start();
		client = new HttpClient();
	}
	
	/**
	 * Body of the worker. Will look for work in the queue and
	 * execute the work if available.
	 */
	public void run() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection;
		try {
			connection = factory.newConnection();

			Channel channel = connection.createChannel();
			channel.queueDeclare(ENERGY_QUEUE, true, false, false, null);
			channel.basicQos(1);
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(ENERGY_QUEUE, false, consumer);
			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				doWork(message);
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Function which will read the message, split it - for each day one.
	 * Creates an xml for each day and sends it to the webservice.
	 * @param message to be converted. 
	 * @throws InterruptedException
	 */
	private void doWork(String message) throws InterruptedException {
		StringRequestEntity entity = null;
		int i;
		int recordAmount = CAT_COUNT*2+1;
		String[] main = message.split(" ");
		int refId = Integer.parseInt(main[0]);
		String ew;
		
		for(i = 1; i < main.length; i += recordAmount) {
			PostMethod post = new PostMethod(address);
			ew = parseDay(main, i, refId);
			try {
				entity = new StringRequestEntity(ew,"application/xml","UTF-8");
				post.setRequestEntity(entity);
				int result = client.executeMethod(post);
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				post.releaseConnection();
			}
		}
	}
	
	/**
	 * Function parsing part of main (one day) to xml.
	 * @param main information of the energy
	 * @param index start of day
	 * @param refId id of customer
	 * @return returns formatted xml
	 */
	private String parseDay(String[] main, int index, int refId) {
		String ew = "<energyWrapper>";
		ew += "<id>" + refId + "</id>";
		ew += "<er>";
		
		for(int i=1; i<CAT_COUNT*2; i += 2){
			ew += "<record>";
			ew += "<energy>" + main[i+index] + "</energy>";
			ew += "<devices>" + main[i+index+1] + "</devices>";
			ew += "<startDate>" + main[index] + "</startDate>";
			ew += "</record>";
		}
		
		ew += "</er>";
		ew += "</energyWrapper>";
		
		return ew;
	}
}
