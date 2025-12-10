package com.art.batch.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

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

		// When .table(...) is called, return the mocked table
		when(mockEnhanced.table(anyString(), any(TableSchema.class))).thenReturn(mockTable);

		// Pass the mocked enhanced client
		TaskRepository repo = new TaskRepository(mockEnhanced);

		// Create handler
		ApiLambdaHandler handler = new ApiLambdaHandler(mockSqs, repo, "dummyQueueUrl");

		// Create fake event
		Map<String, String> event = new HashMap<>();
		event.put("data", "Hello World");

		// Run handler
		String taskId = handler.handleRequest(event, null);

		// VERIFY: repo.createTask() internally calls table.putItem(...)
		verify(mockTable, times(1)).putItem(any(TaskRecord.class));

		// VERIFY: SQS sendMessage was called
		verify(mockSqs, times(1)).sendMessage(any(SendMessageRequest.class));

		assertNotNull(taskId);
	}
}
