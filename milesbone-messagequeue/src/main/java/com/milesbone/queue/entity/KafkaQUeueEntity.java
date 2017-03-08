package com.milesbone.queue.entity;

/**
 * kafka 队列实体
 * 
 * @author miles
 * @date 2016-08-22 下午7:38:37
 */
public class KafkaQUeueEntity implements QueueEntity {

	private static final long serialVersionUID = -3987853424637679591L;

	private String message;

	private String topic;

	private String groupId;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
