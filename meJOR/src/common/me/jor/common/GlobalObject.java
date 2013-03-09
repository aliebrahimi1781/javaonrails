package me.jor.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.jor.util.Help;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 此类包含一些全局对象
 * 不可实例化，不可继承
 * 意义在于，在基于架构中不需要重复创建的且一些基础代码需要使用的对象
 */
public final class GlobalObject {
	private GlobalObject(){}
	
	public static final sun.misc.BASE64Encoder BASE64_ENCODER=new sun.misc.BASE64Encoder();
	public static final sun.misc.BASE64Decoder BASE64_DECODER=new sun.misc.BASE64Decoder();
	private volatile static ExecutorService executorService;
	private volatile static ObjectMapper jsonMapper;
	
	/**
	 * 全局ExecutorService对象，对于基础代码中需要用到的线程池可使用此对象
	 * 常量定义文件的key是：properties.dev.project.globalobject.executorservice取值有：正整数 SINGLE CACHE
	 * 分别创建Executors.newFixedThreadPool() Executors.newSingleThreadPool() Executors.newCachedThreadPool();
	 * 默认是CACHE。
	 * @throws IllegalArgumentException
	 */
	public static ExecutorService getExecutorService(){
		if(executorService==null){
			synchronized(ExecutorService.class){
				if(executorService==null){
					String cmd=Help.convert(
						CommonConstant.getPROPERTIES().getProperty("properties.dev.project.globalobject.executorservice"),
						"CACHE");
					if(cmd.equals("CACHE")){
						executorService=Executors.newCachedThreadPool();
					}else if(cmd.equals("SINGLE")){
						executorService=Executors.newSingleThreadExecutor();
					}else if(cmd.matches("^\\d+$")){
						executorService=Executors.newFixedThreadPool(Integer.parseInt(cmd));
					}else{
						throw new IllegalArgumentException("illegal constant defination:"+cmd);
					}
				}
			}
		}
		return executorService;
	}
	
	/**
	 * 通用json解析对象，这是jackson包的入口
	 */
	public static ObjectMapper getJsonMapper(JsonFactory... jsonFactory){
		if(jsonMapper==null){
			synchronized(ObjectMapper.class){
				if(jsonMapper==null){
					if(Help.isEmpty(jsonFactory)){
						jsonMapper=new ObjectMapper();
					}else{
						jsonMapper=new ObjectMapper(jsonFactory[0]);
					}
				}
			}
		}
		return jsonMapper;
	}
}
