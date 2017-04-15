package com.milesbone.queue.event;

import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerEvent implements IKafkaEvent {

	private RecordMetadata recordMetadata;
	
	private String message;
	
	private Exception exception;

	public RecordMetadata getRecordMetadata() {
		return recordMetadata;
	}

	public void setRecordMetadata(RecordMetadata recordMetadata) {
		this.recordMetadata = recordMetadata;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	
}
