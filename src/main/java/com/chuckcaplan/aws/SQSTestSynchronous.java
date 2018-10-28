package com.chuckcaplan.aws;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSTestSynchronous {
	public static void main(String[] args) throws JMSException {
		String queueUrl = args[0];

		// log in
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		// list queues
		ListQueuesResult lq_result = sqs.listQueues();
		System.out.println("Your SQS Queue URLs:");
		for (String url : lq_result.getQueueUrls()) {
			System.out.println(url);
		}

		// add a message
		SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl).withMessageGroupId("test")
				.withMessageDeduplicationId(new Date().getTime() + "").withMessageBody("hello world");
		sqs.sendMessage(send_msg_request);

		List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();

		for (Message m : messages) {
			System.out.println(m.getBody());
			sqs.deleteMessage(queueUrl, m.getReceiptHandle());
		}

	}
}
