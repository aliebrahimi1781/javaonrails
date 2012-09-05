package me.jor.util.policy;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 创建新线程清除空引用
 */
public abstract class ClearInNewThreadPolicy<E> implements ClearPolicy<E>{
	protected AtomicBoolean notClearing=new AtomicBoolean(true);
	private ClearPolicy<E> clearPolicy;
	
	public ClearInNewThreadPolicy(ClearPolicy<E> clearPolicy){
		this.clearPolicy=clearPolicy;
	}
	@Override
	public void clear(E e){
		if(notClearing.getAndSet(false)){
			exeClear(e);
			notClearing.set(true);
		}
	}
	
	protected void exeClear(E e){
		new Thread(new ClearRunnable(e)).start();
	}
	
	protected class ClearRunnable implements Runnable{
		private Object o;
		
		public ClearRunnable(Object o){
			this.o=o;
		}
		public void run(){
			clearPolicy.clear((E)o);
		}
	};
}
