package com.art.batch.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class ApiLambdaHandlerTest {

	@Test
	public void testApiLambdaSendsMessage() {
		// Mock SQS client
		SqsClient mockSqs = mock(SqsClient.class);

		// Create handler using mock SQS
		ApiLambdaHandler handler = new ApiLambdaHandler(mockSqs, "dummyQueueUrl");

		// Input event
		Map<String, String> event = new HashMap<>();
		event.put("data", "Hello World");

		// Execute Lambda handler
		String result = handler.handleRequest(event, null);

		// Verify SQS sendMessage was called once
		verify(mockSqs, times(1)).sendMessage(any(SendMessageRequest.class));

		assertEquals("Message queued successfully!", result);
	}
}
