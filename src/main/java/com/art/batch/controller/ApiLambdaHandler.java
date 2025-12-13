package com.art.batch.controller;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.art.batch.model.TaskRecord;
import com.art.batch.repository.TaskRepository;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class ApiLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final SqsClient sqs;
	private final TaskRepository taskRepository;
	private final String queueUrl;

	// Runtime constructor
	public ApiLambdaHandler() {
		this.sqs = SqsClient.builder().build();
		this.taskRepository = new TaskRepository();
		this.queueUrl = System.getenv("QUEUE_URL");
	}

	// Test constructor
	public ApiLambdaHandler(SqsClient sqs, TaskRepository repo, String queueUrl) {
		this.sqs = sqs;
		this.taskRepository = repo;
		this.queueUrl = queueUrl;
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

		String body = event.getBody(); // JSON STRING
		// parse body JSON here (Jackson/Gson)
		String taskId = UUID.randomUUID().toString();

		TaskRecord task = new TaskRecord();
		task.setTaskId(taskId);
		task.setInput(body);
		task.setStatus("QUEUED");
		task.setCreatedAt(System.currentTimeMillis());

		taskRepository.createTask(task);

		sqs.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(taskId).build());

		return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(taskId);
	}
}
