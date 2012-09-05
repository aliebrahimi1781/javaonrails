package me.jor.util.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 针对同一个类、对象或工具的初始化和反初始化发生并发冲突时使用此类<br/>
 * 一般这类操作在运行时只需要在工程生命周期内运行一次，
 * 或在某一时间段内只运行一次，
 * 或在同一批次的操作内只运行一次。
 * 所以有必要控制并发执行时确保这类操作只执行一次，
 * 最好用于初始化或反初始化很耗时的时候<br/>
 * 
 * 最好确保key与initask、endtask一一对应，
 * 因为相同的key被认为是相同的初始化与反初始化任务
 * */
public class ConcurrentInitAndEnd {
	
	private static final Map<String, AtomicInteger> concurrent=new ConcurrentHashMap<String, AtomicInteger>();
	/**
	 * 初始化
	 * 此类维护一个ConcurrentHashMap，以参数key值为键，AtomicInteger为值，每调用一次init键对应的整数值就增1；
	 * 每调用一次end就减1。<br/>
	 * 如果key对应的AtomicInteger不存在，才会执行传入init的Runnable对象，
	 * 直到对应的AtomicInteger值为0时，才会真正执行传入end的Runnable对象。
	 * @param key
     *        依据key的值惟一确定要执行的任务是否同一个，因此key必须与initask一一对应<br/>
     *        如下代码保证key为"CodeDetectUtil"对应的Runnable对象执行且只执行一次：<br/>
     *        ConcurrentInitAndEnd.init("CodeDetectUtil",new Runnable(){<br/>
     *             public void run(){<br/>
     *                 CodeDetectUtil.init();
     *             }<br/>
     *        }});
	 * @param initask
	 *        试图执行的Runnable
	 * @throws InterruptedException
	 * @return void
	 */
	public static void init(String key, Runnable initask) throws InterruptedException{
		if(!concurrent.containsKey(key)){
			synchronized(key){
				if(!concurrent.containsKey(key)){
					concurrent.put(key, new AtomicInteger(1));
					initask.run();
				}else{
					concurrent.get(key).getAndIncrement();
				}
			}
		}else{
			concurrent.get(key).getAndIncrement();
		}
	}
	/**
	 * 反初始化
	 * 详细说明参考init
	 * @throws InterruptedException 
	 * */
	public static void end(String key, Runnable endtask) throws InterruptedException{
		try{
			if(concurrent.get(key).get()>0){
				concurrent.get(key).getAndDecrement();
			}
		}catch(NullPointerException e){
			return;
		}
		synchronized(key){
			if(concurrent.get(key).get()<=0){
				concurrent.remove(key);
				endtask.run();
			}
		}
	}
}
