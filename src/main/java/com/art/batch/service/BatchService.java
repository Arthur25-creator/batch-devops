package com.art.batch.service;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class BatchService {

	private final S3Client s3Client;
	private final String bucketName;

	public BatchService(S3Client s3Client) {
		this.s3Client = s3Client;
		this.bucketName = System.getenv("S3_BUCKET_NAME");
	}

	public BatchService() {
		this(S3Client.builder().build());
	}

	public String processTask(String taskId) {
		String output = "Processed task: " + taskId;
		String key = "output-" + taskId + ".txt";

		s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(),
				RequestBody.fromString(output));

		return key; // return S3 key
	}
}
