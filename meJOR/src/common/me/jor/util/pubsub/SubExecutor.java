package me.jor.util.pubsub;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import me.jor.common.GlobalObject;

public class SubExecutor implements Runnable{
	private static ExecutorService executor=GlobalObject.getExecutorService();
	private Queue<Object> datas=new ConcurrentLinkedQueue<Object>();
	private Set<SubTask> tasks=Collections.synchronizedSet(new HashSet<SubTask>());
	private AtomicBoolean executing=new AtomicBoolean(true);
	
	public SubExecutor(){
		executor.submit(this);
	}
	
	@Override
	public void run(){
		for(;executing.get();){
			while(!datas.isEmpty()){
				final Object data=datas.poll();
				executor.submit(new Runnable(){
					@Override
					public void run(){
						for(SubTask task:tasks){
							task.execute(data);
						}
					}
				});
			}
			synchronized(this){
				try {
					this.wait();
				} catch (InterruptedException e) {}
			}
		}
	}
	public void shutdown(){
		executing.set(false);
		for(SubTask t:tasks){
			t.complete();
		}
	}
	public void attach(Object data){
		if(executing.get()){
			datas.offer(data);
		}
	}
	public void regist(SubTask task){
		if(executing.get()){
			for(SubTask t:tasks){
				if(t.equals(task)){
					t.complete();
					tasks.remove(t);
					break;
				}
			}
			tasks.add(task);
		}
	}
	public void unregist(SubTask task){
		tasks.remove(task);
	}
	public void checkTimeout(){
		Iterator<SubTask> iterator=tasks.iterator();
		while(iterator.hasNext()){
			SubTask t=iterator.next();
			if(t instanceof AbstractSubTask && ((AbstractSubTask)t).isTimeout()){
				iterator.remove();
				t.complete();
			}
		}
	}
}
