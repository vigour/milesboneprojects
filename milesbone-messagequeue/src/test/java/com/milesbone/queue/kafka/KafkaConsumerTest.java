package com.milesbone.queue.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.queue.kafka.consumer.KafkaMessageConsumer;


public class KafkaConsumerTest {
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerTest.class);

	@Test
	public void testStartConsumer() throws InterruptedException{
		CountDownLatch downLatch = new CountDownLatch(1);
		KafkaMessageConsumer consumer = new KafkaMessageConsumer();
		consumer.start();
		downLatch.await();
		
	}
	
	
	
	/**
	 * 消费者自动提交
	 */
	@Test
	public void testConsumerAutoCommit() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.4.128:9092,192.168.4.128:9093,192.168.4.128:9094");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		try {
			consumer.subscribe(Arrays.asList("test-replicated-topic"));
			// SaveOffsetsOnRebalance s = new SaveOffsetsOnRebalance(consumer);
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records)
					logger.info(String.format("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			consumer.close();
		}
	}

	/**
	 * 消费者手动提交
	 */
	@Test
	public void testConsummerManualCommit() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.4.128:9092,192.168.4.128:9093,192.168.4.128:9094");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "milesbone");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		try {
			consumer.subscribe(Arrays.asList("test-replicated-topic"));
			final int minBatchSize = 10;
			List<ConsumerRecord<String, String>> buffer = new ArrayList<ConsumerRecord<String, String>>();
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				for (ConsumerRecord<String, String> record : records) {
					logger.info("received record:{}", record);
					buffer.add(record);
				}
				consumer.commitSync();
				if (buffer.size() >= minBatchSize) {
					buffer.clear();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			consumer.close();
		}
	}
	
	/**
	 * 消费者手动提交
	 */
	@Test
	public void testConsummerPartitionManualCommit() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.4.128:9092,192.168.4.128:9093,192.168.4.128:9094");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "milesbone");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		try {
			consumer.subscribe(Arrays.asList("test-replicated-topic"));
			final int minBatchSize = 10;
			List<ConsumerRecord<String, String>> buffer = new ArrayList<ConsumerRecord<String, String>>();
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				for(TopicPartition partition : records.partitions()){
					List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
					for (ConsumerRecord<String, String> record : partitionRecords) {
						logger.info("received record:{}", record);
						buffer.add(record);
					}
						 // 取得当前读取到的最后一条记录的offset
		                long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
		                // 提交offset，记得要 + 1
		                consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
					
				}
				if(buffer.size() >= minBatchSize){
					buffer.clear();
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			consumer.close();
		}
	}
}
