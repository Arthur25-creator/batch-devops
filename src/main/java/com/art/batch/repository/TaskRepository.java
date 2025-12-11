package com.art.batch.repository;

import org.springframework.stereotype.Repository;

import com.art.batch.model.TaskRecord;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class TaskRepository {

	private final DynamoDbTable<TaskRecord> table;

	// Runtime constructor
	public TaskRepository() {
		String tableName = System.getenv("TASK_TABLE");
		String regionName = System.getenv("AWS_REGION"); // Lambda automatically sets this

		DynamoDbClient client = DynamoDbClient.builder().region(Region.of(regionName)).build();

		DynamoDbEnhancedClient enhanced = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();

		this.table = enhanced.table(tableName, TableSchema.fromBean(TaskRecord.class));
	}

	// Test constructor (mock incoming)
	public TaskRepository(DynamoDbEnhancedClient enhanced) {
		this.table = enhanced.table("TasksTable", TableSchema.fromBean(TaskRecord.class));
	}

	public void createTask(TaskRecord record) {
		table.putItem(record);
	}

	public void updateStatus(String taskId, String status, String outputPath) {
		TaskRecord record = table.getItem(Key.builder().partitionValue(taskId).build());
		record.setStatus(status);
		record.setOutputLocation(outputPath);
		table.updateItem(record);
	}
}