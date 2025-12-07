package com.art.batch.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.art.batch.service.BatchService;

public class WorkerLambdaHandler implements RequestHandler<String, String> {

	private final BatchService service;

	public WorkerLambdaHandler() {
		this.service = new BatchService(); // normal Lambda runtime
	}

	// For unit tests
	public WorkerLambdaHandler(BatchService service) {
		this.service = service;
	}

	@Override
	public String handleRequest(String message, Context context) {
		service.processTask(message);
		return "Processed and stored successfully!";
	}
}