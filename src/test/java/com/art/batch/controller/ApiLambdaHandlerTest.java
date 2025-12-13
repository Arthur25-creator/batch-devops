package com.art.batch.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.art.batch.model.TaskRecord;
import com.art.batch.repository.TaskRepository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class ApiLambdaHandlerTest {

	@Test
	public void testApiLambdaSendsMessage() {

		// Mock SQS
		SqsClient mockSqs = mock(SqsClient.class);

		// Mock DynamoDB Enhanced client AND table
		DynamoDbEnhancedClient mockEnhanced = mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<TaskRecord> mockTable = mock(DynamoDbTable.class);

		when(mockEnhanced.table(anyString(), any(TableSchema.class))).thenReturn(mockTable);

		TaskRepository repo = new TaskRepository(mockEnhanced);

		ApiLambdaHandler handler = new ApiLambdaHandler(mockSqs, repo, "dummyQueueUrl");

		// 🔥 REALISTIC API Gateway event
		APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent().withHttpMethod("POST")
				.withBody("{\"data\":\"Hello World\"}");

		// Run handler
		APIGatewayProxyResponseEvent response = handler.handleRequest(event, mock(Context.class));

		// VERIFY DynamoDB write
		verify(mockTable, times(1)).putItem(any(TaskRecord.class));

		// VERIFY SQS send
		verify(mockSqs, times(1)).sendMessage(any(SendMessageRequest.class));

		// VERIFY response
		assertEquals(200, response.getStatusCode());
		assertNotNull(response.getBody()); // taskId
	}
}
