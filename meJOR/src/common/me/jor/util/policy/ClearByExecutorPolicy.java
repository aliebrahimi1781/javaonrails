package me.jor.util.policy;

import java.util.concurrent.Executor;

/**
 * 使用指定线程池清除空引用
 */
public class ClearByExecutorPolicy<E> extends ClearInNewThreadPolicy<E>{
	private Executor executor;
	public ClearByExecutorPolicy(Executor executor, ClearPolicy<E> clearPolicy){
		super(clearPolicy);
		this.executor=executor;
	}
	@Override
	protected void exeClear(E e){
		executor.execute(new ClearRunnable(e));
	}
}
