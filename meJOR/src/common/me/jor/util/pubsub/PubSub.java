package me.jor.util.pubsub;

import java.util.concurrent.ConcurrentHashMap;

import me.jor.common.CommonConstant;
import me.jor.common.GlobalObject;

public class PubSub {
	private static PubSub pubsub=new PubSub();
	private ConcurrentHashMap<String, SubExecutor> registed=new ConcurrentHashMap<String,SubExecutor>();
	
	private PubSub(){
		GlobalObject.getExecutorService().submit(new Runnable(){
			public void run(){
				for(SubExecutor executor:registed.values()){
					synchronized(this){
						try {
							wait(CommonConstant.getLongConstant("me.jor.pubsub.checktimeout", 60000));
						} catch (InterruptedException e) {}
					}
					executor.checkTimeout();
				}
			}
		});
	}
	
	public static PubSub getPubSub(){
		return pubsub;
	}
	
	private SubExecutor getExecutor(String name){
		SubExecutor executor=registed.get(name);
		if(executor==null){
			synchronized(registed){
				registed.putIfAbsent(name, new SubExecutor());
				executor=registed.get(name);
			}
		}
		return executor;
	}
	
	public void pub(String name, Object data){
		SubExecutor executor=getExecutor(name);
		executor.attach(data);
		synchronized(executor){
			executor.notifyAll();
		}
	}
	
	public void sub(String name, SubTask task){
		getExecutor(name).regist(task);
	}
	
	public void destroy(String name){
		registed.remove(name).shutdown();
	}
	
	public void desub(String name, SubTask task){
		SubExecutor executor=registed.get(name);
		if(executor!=null){
			executor.unregist(task);
		}
	}
}
