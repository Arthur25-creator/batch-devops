package com.art.batch.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.art.batch.service.BatchService;

public class WorkerLambdaHandlerTest {

	@Test
	public void testWorkerLambdaProcess() {
		// Mock BatchService
		BatchService mockService = mock(BatchService.class);

		// Inject mock into handler
		WorkerLambdaHandler handler = new WorkerLambdaHandler(mockService);

		// Invoke handler
		String result = handler.handleRequest("Hello", null);

		// Verify
		verify(mockService, times(1)).processTask("Hello");
		assertEquals("Processed and stored successfully!", result);
	}
}