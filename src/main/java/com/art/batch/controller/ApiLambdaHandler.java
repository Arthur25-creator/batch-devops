package com.art.batch.controller;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class ApiLambdaHandler implements RequestHandler<Map<String, String>, String> {

	private final SqsClient sqsClient;
	private final String queueUrl;

	// Default constructor used by real Lambda
	public ApiLambdaHandler() {
		this.sqsClient = SqsClient.builder().build(); // real client only in AWS
		this.queueUrl = System.getenv("QUEUE_URL");
	}

	// Constructor for unit tests (MOCKS)
	public ApiLambdaHandler(SqsClient sqsClient, String queueUrl) {
		this.sqsClient = sqsClient;
		this.queueUrl = queueUrl;
	}

	@Override
	public String handleRequest(Map<String, String> event, Context context) {
		String message = event.get("data");

		sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(message).build());

		return "Message queued successfully!";
	}
}
