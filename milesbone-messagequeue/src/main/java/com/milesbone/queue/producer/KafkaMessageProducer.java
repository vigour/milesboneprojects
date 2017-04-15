package com.milesbone.queue.producer;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.queue.config.KafkaConfiguration;
import com.milesbone.queue.exception.QueueOperateException;

/**
 * 消息生产者
 * @author miles
 * @date 2016-08-23 上午11:14:35
 */
public class KafkaMessageProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);

	private  static Producer<String, String> producer;
	
	private static KafkaMessageProducer messageProducer;
	
	
	private KafkaMessageProducer() {
		logger.debug("初始化KafkaProducer开始...");
		Properties producerProps = KafkaConfiguration.getInstance().getProducerProp();
		producer = new KafkaProducer<String, String>(producerProps);
		logger.debug("初始化KafkaProducer完成");
	}

	private KafkaMessageProducer(Properties props) {
		logger.debug("初始化KafkaProducer开始...");
		Properties producerProps = KafkaConfiguration.getInstance().getProducerProp();
		if(props != null){
			producerProps.putAll(props);
		}
		producer = new KafkaProducer<String, String>(props);
		logger.debug("初始化KafkaProducer完成");
	}

	
	/**
	 * 获取producer
	 * @param props
	 * @return
	 */
	public static KafkaMessageProducer getKafkaMessageProducer(Properties props){
		logger.debug("初始化KafkaMessageProducer...");
		if(messageProducer == null){
			messageProducer = new KafkaMessageProducer(props);
		}
		logger.debug("初始化KafkaMessageProducer完成");
		return messageProducer;
	}
	
	
	/**
	 * 获取producer
	 * @return
	 */
	public static KafkaMessageProducer getKafkaMessageProducer(){
		logger.debug("初始化KafkaMessageProducer...");
		if(messageProducer == null){
			messageProducer = new KafkaMessageProducer();
		}
		logger.debug("初始化KafkaMessageProducer完成");
		return messageProducer;
	}
	
	
	/**
	 * 重新加载producer
	 */
	public static void reloadProducer(){
		synchronized (producer) {
			logger.debug("重新加载producer...");
			if(producer == null){
				messageProducer = null;
				messageProducer = new KafkaMessageProducer();
			}
			logger.debug("重新加载producer完成");
		}
	}
	
	/**
	 * 关闭producer
	 */
	public void close(){
		logger.debug("开始关闭producer...");
		if(producer != null){
			producer.close();
			producer = null;
		}
		logger.debug("关闭producer完成");
	}
	
	/**
	 * kafka发送一条消息 不阻塞 
	 * @param message
	 * @param topic
	 * @throws QueueOperateException
	 */
	public void queue(String message , String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			producer.send(new ProducerRecord<String, String>(topic,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
		
	}
	
	

	/**
	 * kafka发送一条消息 不阻塞 带返回值
	 * @param message
	 * @param topic
	 * @return
	 * @throws QueueOperateException
	 */
	public Future<RecordMetadata> queueResult(String message , String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			return result;
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * kafka发送一条带key的消息 不阻塞
	 * @param key
	 * @param message
	 * @param topic
	 * @throws QueueOperateException
	 */
	public void queue(String key,String message ,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		
		try {
			logger.debug("开始向kafka中push生产消息");
			producer.send(new ProducerRecord<String, String>(topic,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * kafka发送一条带key的消息 不阻塞 带返回值
	 * @param key
	 * @param message
	 * @param topic
	 * @return
	 * @throws QueueOperateException
	 */
	public Future<RecordMetadata>  queueResult(String key,String message ,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			return result;
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
		
	}
	
	
	/**
	 * 发送一条指定partition 并且有key的消息
	 * @param key
	 * @param partition
	 * @param message
	 * @param topic
	 * @throws QueueOperateException
	 */
	public void queue(String key,int partition ,String message,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(partition <= 0){
			logger.error("向kafka中生产消息异常:partition不能为负值");
			throw new QueueOperateException("向kafka中生产消息异常:partition不能为负值");
		}
		
		try {
			logger.debug("开始向kafka中push生产消息");
			producer.send(new ProducerRecord<String, String>(topic,partition,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	
	/**
	 * 发送一条指定partition 并且有key的消息 带返回值
	 * @param key
	 * @param partition
	 * @param message
	 * @param topic
	 * @return
	 * @throws QueueOperateException
	 */
	public Future<RecordMetadata> queueResult(String key,int partition ,String message,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(partition <= 0){
			logger.error("向kafka中生产消息异常:partition不能为负值");
			throw new QueueOperateException("向kafka中生产消息异常:partition不能为负值");
		}
		
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,partition,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			return result;
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * kafka发送一条消息 阻塞 
	 * @param message
	 * @param topic
	 * @throws QueueOperateException
	 */
	public void queueBlock(String message,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka...");
			result.get();
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	
	/**
	 * kafka发送一条带key消息 阻塞 
	 * @param key
	 * @param msg
	 * @param topic
	 * @throws QueueOperatException
	 */
	public void queueBlock(String key,String message,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka...");
			result.get();
			
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * kafka发送一条带key的消息 阻塞 带返回值
	 * @param key
	 * @param message
	 * @param topic
	 * @return
	 * @throws QueueOperateException
	 */
	public RecordMetadata queueBlockResult(String key,String message,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka...");
			RecordMetadata meta = result.get();
			return meta;
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	
	/**
	 * 发送一条指定partition 并且有key的消息 阻塞
	 * @param key
	 * @param partition
	 * @param message
	 * @param topic
	 * @throws QueueOperateException
	 */
	public void queueBlock(String key,int partition ,String message,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(partition <= 0){
			logger.error("向kafka中生产消息异常:partition不能为负值");
			throw new QueueOperateException("向kafka中生产消息异常:partition不能为负值");
		}
		
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result =  producer.send(new ProducerRecord<String, String>(topic,partition,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka...");
			result.get();
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * 发送一条指定partition 并且有key的消息 阻塞 带返回值
	 * @param key
	 * @param partition
	 * @param message
	 * @param topic
	 * @return
	 * @throws QueueOperateException
	 */
	public RecordMetadata queueBlockResult(String key,int partition ,String message,String topic) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(partition <= 0){
			logger.error("向kafka中生产消息异常:partition不能为负值");
			throw new QueueOperateException("向kafka中生产消息异常:partition不能为负值");
		}
		
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result =  producer.send(new ProducerRecord<String, String>(topic,partition,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka...");
			RecordMetadata meta = result.get();
			
			return meta;
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	/**
	 * kafka发送一条消息 指定阻塞时间
	 * @param message
	 * @param topic
	 * @param timeout
	 * @throws QueueOperateException
	 */
	public void queueBlock(String message,String topic,long timeout) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(timeout <= 0){
			logger.error("向kafka中生产消息异常:timeout不能小于0");
			throw new QueueOperateException("向kafka中生产消息异常:timeout不能小于0");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka{}ms...",timeout);
			result.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * kafka发送一条消息 阻塞指定时间 带返回值
	 * @param message
	 * @param topic
	 * @param timeout
	 * @return
	 * @throws QueueOperateException
	 */
	public RecordMetadata queueBlockResult(String message,String topic,long timeout) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(timeout <= 0){
			logger.error("向kafka中生产消息异常:timeout不能小于0");
			throw new QueueOperateException("向kafka中生产消息异常:timeout不能小于0");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka{}ms...",timeout);
			RecordMetadata meta = result.get(timeout, TimeUnit.MILLISECONDS);
			return meta;
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * kafka发送一条带key的消息 指定阻塞时间 
	 * @param key
	 * @param message
	 * @param topic
	 * @param timeout
	 * @throws QueueOperateException
	 */
	public void queueBlock(String key,String message,String topic,long timeout) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(timeout <= 0){
			logger.error("向kafka中生产消息异常:timeout不能小于0");
			throw new QueueOperateException("向kafka中生产消息异常:timeout不能小于0");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka{}ms...",timeout);
			result.get(timeout, TimeUnit.MILLISECONDS);			
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	/**
	 * kafka发送一条带key的消息 阻塞指定时间 带返回值
	 * @param key
	 * @param message
	 * @param topic
	 * @param tmout
	 * @return
	 * @throws QueueOperateException
	 */
	public RecordMetadata queueBlockResult(String key,String message,String topic,long timeout) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(timeout <= 0){
			logger.error("向kafka中生产消息异常:timeout不能小于0");
			throw new QueueOperateException("向kafka中生产消息异常:timeout不能小于0");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result = producer.send(new ProducerRecord<String, String>(topic,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka{}ms...",timeout);
			RecordMetadata meta = result.get(timeout, TimeUnit.MILLISECONDS);
			return meta;	
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * 发送一条指定partition 并且有key的消息 阻塞指定时间
	 * @param key
	 * @param partition
	 * @param message
	 * @param topic
	 * @param timeout
	 * @throws QueueOperateException
	 */
	public void queueBlock(String key,int partition ,String message,String topic,long timeout) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(partition <= 0){
			logger.error("向kafka中生产消息异常:partition不能为负值");
			throw new QueueOperateException("向kafka中生产消息异常:partition不能为负值");
		}
		if(timeout <= 0){
			logger.error("向kafka中生产消息异常:timeout不能小于0");
			throw new QueueOperateException("向kafka中生产消息异常:timeout不能小于0");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result =  producer.send(new ProducerRecord<String, String>(topic,partition,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka{}ms...",timeout);
			result.get(timeout, TimeUnit.MILLISECONDS);		
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	
	/**
	 * 发送一条指定partition 并且有key的消息 阻塞指定时间 带返回值
	 * @param key
	 * @param partition
	 * @param message
	 * @param topic
	 * @param timeout
	 * @return
	 * @throws QueueOperateException
	 */
	public RecordMetadata queueBlockResult(String key,int partition ,String message,String topic,long timeout) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(partition <= 0){
			logger.error("向kafka中生产消息异常:partition不能为负值");
			throw new QueueOperateException("向kafka中生产消息异常:partition不能为负值");
		}
		if(timeout <= 0){
			logger.error("向kafka中生产消息异常:timeout不能小于0");
			throw new QueueOperateException("向kafka中生产消息异常:timeout不能小于0");
		}
		try {
			logger.debug("开始向kafka中push生产消息");
			Future<RecordMetadata> result =  producer.send(new ProducerRecord<String, String>(topic,partition,key,message));
			producer.flush();
			logger.debug("向kafka中push生产消息完成");
			logger.debug("阻塞kafka{}ms...",timeout);
			RecordMetadata meta = result.get(timeout, TimeUnit.MILLISECONDS);
			return meta;		
		} catch (Exception e) {
			logger.error("向kafka中生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中生产消息异常",e);
		}
	}
	
	
	/**
	 * kafka发送一条消息 异步回调
	 * @param msg
	 * @param topic
	 * @param callBcak
	 * @throws QueueOperateException
	 */
	public void queueCallBack(String message,String topic,Callback callBcak) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		try {
			logger.debug("开始向kafka中异步push生产消息");
			producer.send(new ProducerRecord<String, String>(topic,message),callBcak);
			producer.flush();
			logger.debug("向kafka中异步push生产消息完成");
		} catch (Exception e) {
			logger.error("向kafka中异步生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中异步生产消息异常",e);
		}
	}
	
	/**
	 * kafka发送一条带key的消息 异步回调
	 * @param key
	 * @param message
	 * @param topic
	 * @param callBcak
	 * @throws QueueOperateException
	 */
	public void queueCallBack(String key,String message,String topic,Callback callBcak) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		try {
			logger.debug("开始向kafka中异步push生产消息");
			producer.send(new ProducerRecord<String, String>(topic,key,message),callBcak);
			producer.flush();
			logger.debug("向kafka中异步push生产消息完成");
		} catch (Exception e) {
			logger.error("向kafka中异步生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中异步生产消息异常",e);
		}
	}
	
	/**
	 * 发送一条指定partition 并且有key的消息 异步回调
	 * @param key
	 * @param partition
	 * @param message
	 * @param topic
	 * @param callBcak
	 * @throws QueueOperateException
	 */
	public void queueCallBack(String key,int partition ,String message,String topic,Callback callBcak) throws QueueOperateException{
		if(StringUtils.isNotBlank(topic)){
			logger.error("向kafka中生产消息异常:主题topic不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:主题不能为空");
		}
		if(StringUtils.isNotBlank(key)){
			logger.error("向kafka中生产消息异常:key不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:key不能为空");
		}
		if(StringUtils.isNotBlank(message)){
			logger.error("向kafka中生产消息异常:消息message不能为空");
			throw new QueueOperateException("向kafka中生产消息异常:消息不能为空");
		}
		if(partition <= 0){
			logger.error("向kafka中生产消息异常:partition不能为负值");
			throw new QueueOperateException("向kafka中生产消息异常:partition不能为负值");
		}
		try {
			logger.debug("开始向kafka中异步push生产消息");
			producer.send(new ProducerRecord<String, String>(topic,partition,key,message),callBcak);
			producer.flush();
			logger.debug("向kafka中异步push生产消息完成");
		} catch (Exception e) {
			logger.error("向kafka中异步生产消息异常:{}",e.getMessage());
			throw new QueueOperateException("向kafka中异步生产消息异常",e);
		}
	}
	
	
}
