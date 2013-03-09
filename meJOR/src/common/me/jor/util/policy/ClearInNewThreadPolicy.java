package me.jor.util.policy;

import java.util.concurrent.atomic.AtomicBoolean;

import me.jor.common.Task;
import me.jor.util.Log4jUtil;
import me.jor.util.concurrent.ExecutingOnce;

import org.apache.commons.logging.Log;

/**
 * 创建新线程清除空引用
 */
public abstract class ClearInNewThreadPolicy<E> implements ClearPolicy<E>{
	private  static final Log log=Log4jUtil.getLog(ClearInNewThreadPolicy.class);
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
			try {
				ExecutingOnce.executeAndReturnImmediately(ClearInNewThreadPolicy.class.getName(), new Task(){
					@Override
					public <T> T execute() throws Throwable {
						clearPolicy.clear((E)o);
						return null;
					}
					
				});
			} catch (Throwable e) {
				log.error(e.getMessage(),e);
			}
		}
	};
}
