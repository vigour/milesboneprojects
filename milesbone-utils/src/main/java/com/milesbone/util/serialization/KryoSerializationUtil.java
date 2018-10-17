package com.milesbone.util.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import com.milesbone.common.util.ISerializationUtil;


/**
 * Kryo序列化工具类
 * @Title  KryoSerializationUtil.java
 * @Package com.milesbone.util.serialization
 * @Description    TODO
 * @author miles
 * @date   2018-10-15 15:12
 */
public class KryoSerializationUtil implements ISerializationUtil{
	
	private static final Integer DEFAULT_BUFFER_SIZE = 2048;

	final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
		
		// 由于kryo不是线程安全的，所以每个线程都使用独立的kryo
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.register(classType, new BeanSerializer<>(kryo, classType));
			return kryo;
		}
	};
	
	
	final ThreadLocal<Output> outputLocal = new ThreadLocal<Output>();
	
	final ThreadLocal<Input> inputLocal = new ThreadLocal<Input>();
	
	private Class<?> classType = null;
	
	public KryoSerializationUtil(Class<?> classType) {
		this.classType = classType;
	}
	
	
	public <T> void serialize(T obj,byte[] bytes){
		Kryo kryo = getKryo();
		Output output = getOutput(bytes);
		kryo.writeObjectOrNull(output, obj,  obj.getClass());
		output.flush();
	}
	
	
	 public static <T> byte[] serialize(T obj) {
		 Kryo kryo = new Kryo();
			byte[] buffer = new byte[2048];
			try(
					Output output = new Output(buffer);
					) {
				
				kryo.writeClassAndObject(output, obj);
				return output.toBytes();
			} catch (Exception e) {
			}
			return buffer;
	 }
	 
	 
	 public <T> T deserialize(byte[] bytes) {
			return deserialize(bytes, 0, bytes.length);
	 }
	 
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] bytes, int offset, int count) {
		Kryo kryo = getKryo();
		Input input = getInput(bytes, offset, count);
		return (T) kryo.readObjectOrNull(input, classType);
	}


	
	/**
	 * 获取Input
	 * @param bytes
	 * @param offset
	 * @param count
	 * @return
	 */
	private Input getInput(byte[] bytes, int offset, int count) {
		Input input = null;
		if ((input = inputLocal.get()) == null) {
			input = new Input();
			inputLocal.set(input);
		}
		if (bytes != null) {
			input.setBuffer(bytes, offset, count);
		}
		return input;
	}

	static Kryo kryo = new Kryo(); 
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] data, Class<T> cls) {
		try(
		Input input = new Input(data);
				){
			return (T) kryo.readClassAndObject(input);
		}catch (Exception e) {
		}
		return (T) kryo;
	 }

	

	
	/**
	 * 获取Output并设置初始数组
	 * @param bytes 
	 * @return
	 */
	private Output getOutput(byte[] bytes) {
		Output output = null;
		if ((output = outputLocal.get()) == null) {
			output = new Output();
			outputLocal.set(output);
		}
		if (bytes != null) {
			output.setBuffer(bytes);
		}else {
			output.setBuffer(new byte[DEFAULT_BUFFER_SIZE]);
		}
		return output;
	}


	/**
	 * 获取kryo
	 * @return
	 */
	private Kryo getKryo() {
		return kryoLocal.get();
	}


	public Class<?> getClassType() {
		return classType;
	}

	public void setClassType(Class<?> classType) {
		this.classType = classType;
	}
	
}
