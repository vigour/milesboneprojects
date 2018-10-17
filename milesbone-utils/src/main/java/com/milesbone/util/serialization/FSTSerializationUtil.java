package com.milesbone.util.serialization;

import org.nustaq.serialization.FSTConfiguration;

import com.milesbone.common.util.ISerializationUtil;

/**
 * fast serialization 序列化工具类
 * @Title FSTSerializationUtil.java
 * @Package com.milesbone.util.serialization
 * @Description TODO
 * @author miles
 * @date 2018-10-15 15:14
 */
public class FSTSerializationUtil implements ISerializationUtil {
	static FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
	
	public static <T> byte[] serialize(T obj) {
		return configuration.asByteArray(obj);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] data) {
		return (T) configuration.asObject(data);
	}
}