package com.art.batch.service;

import java.nio.file.Paths;

import org.springframework.stereotype.Service;

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

	public void processTask(String message) {
		// Example processing
		String output = "Processed: " + message;

		// Save to S3
		String key = "output-" + System.currentTimeMillis() + ".txt";
		s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(), Paths.get("/tmp/" + key)); // write
																														// locally
																														// then
																														// upload
	}
}
