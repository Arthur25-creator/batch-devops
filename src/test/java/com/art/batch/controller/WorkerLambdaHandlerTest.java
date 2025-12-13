package com.art.batch.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.art.batch.repository.TaskRepository;
import com.art.batch.service.BatchService;

public class WorkerLambdaHandlerTest {

	@Test
	public void testWorkerLambda() {

		BatchService mockService = mock(BatchService.class);
		TaskRepository mockRepo = mock(TaskRepository.class);

		when(mockService.processTask("123")).thenReturn("output-123.txt");

		WorkerLambdaHandler handler = new WorkerLambdaHandler(mockService, mockRepo);

		// 🔥 Build fake SQS event
		SQSEvent.SQSMessage msg = new SQSEvent.SQSMessage();
		msg.setBody("123");

		SQSEvent event = new SQSEvent();
		event.setRecords(List.of(msg));

		// Invoke handler
		handler.handleRequest(event, mock(Context.class));

		// VERIFY
		verify(mockService, times(1)).processTask("123");
		verify(mockRepo, times(1)).updateStatus("123", "DONE", "output-123.txt");
	}
}