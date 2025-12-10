package com.art.batch.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.art.batch.repository.TaskRepository;
import com.art.batch.service.BatchService;

public class WorkerLambdaHandlerTest {

	@Test
	public void testWorkerLambda() {

		BatchService mockService = mock(BatchService.class);
		TaskRepository mockRepo = mock(TaskRepository.class);

		when(mockService.processTask("123")).thenReturn("output-123.txt");

		WorkerLambdaHandler handler = new WorkerLambdaHandler(mockService, mockRepo);

		String result = handler.handleRequest("123", null);

		verify(mockRepo, times(1)).updateStatus("123", "DONE", "output-123.txt");
		assertEquals("Processed: 123", result);
	}
}