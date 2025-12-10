package com.art.batch.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.art.batch.repository.TaskRepository;
import com.art.batch.service.BatchService;

public class WorkerLambdaHandler implements RequestHandler<String, String> {

	private final BatchService batchService;
	private final TaskRepository taskRepository;

	// Runtime constructor
	public WorkerLambdaHandler() {
		this.batchService = new BatchService();
		this.taskRepository = new TaskRepository();
	}

	// Test constructor
	public WorkerLambdaHandler(BatchService batchService, TaskRepository taskRepository) {
		this.batchService = batchService;
		this.taskRepository = taskRepository;
	}

	@Override
	public String handleRequest(String taskId, Context context) {
		String outputPath = batchService.processTask(taskId);

		taskRepository.updateStatus(taskId, "DONE", outputPath);

		return "Processed: " + taskId;
	}
}