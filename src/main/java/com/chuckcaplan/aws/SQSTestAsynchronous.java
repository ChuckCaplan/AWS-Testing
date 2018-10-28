package com.chuckcaplan.aws;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSTestAsynchronous implements MessageListener {
	public static void main(String[] args) throws JMSException {

		// start a listener, send a lot of messages, and confirm they are received in
		// order.

		String queueUrl = args[0];
		String queueName = queueUrl.substring(queueUrl.lastIndexOf('/') + 1);
		System.out.println(queueName);

		// log in
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		// Create a new connection factory with all defaults (credentials and region)
		// set automatically
		SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), sqs);

		// Create the connection.
		SQSConnection connection = connectionFactory.createConnection();

		// Create the nontransacted session with AUTO_ACKNOWLEDGE mode
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Queue queue = session.createQueue(queueName);

		// Create a consumer for the queue.
		MessageConsumer consumer = session.createConsumer(queue);

		// Instantiate and set the message listener for the consumer.
		consumer.setMessageListener(new SQSTestAsynchronous());

		// Start receiving incoming messages.
		connection.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// send a lot of messages
		for (int i = 1; i <= 10; i++) {
			// add a message
			System.out.println("sending message " + i);
			SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl)
					.withMessageGroupId("test").withMessageDeduplicationId(new Date().getTime() + "")
					.withMessageBody("hello world" + i);
			sqs.sendMessage(send_msg_request);
		}

	}

	@Override
	public void onMessage(javax.jms.Message message) {
		try {
			// Cast the received message as TextMessage and print the text to screen.
			System.out.println("Received: " + ((TextMessage) message).getText());
			message.acknowledge();
			System.out.println("Acknowledged: " + message.getJMSMessageID());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
