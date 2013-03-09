package me.jor.pool;

import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import me.jor.common.Task;
import me.jor.util.Cache;
import me.jor.util.LockCache;
import me.jor.util.concurrent.ExecutingOnce;
import me.jor.util.policy.ClearByExecutorPolicy;
import me.jor.util.policy.ClearPolicy;

/**
 * 软引用对象池<br/>
 * 可使用此类的实例，保存软引用对象。<br/>
 * 使对象可重复使用。内存将满时软引用将被回收。
 * @see java.lang.ref.SoftReference
 */
public class SoftReferencePool<E> {
	private SoftReference<Deque<SoftReference<E>>> stack;
	
	private String lockName;
	private ClearPolicy<SoftReferencePool<E>> clearPolicy;
	private AtomicBoolean empty=new AtomicBoolean(true);
	
	/**
	 * 创建对象池实例，使用默认的SoftReferencePool.ClearInCurrentThreadPolicy清除空引用
	 * @param lockName
	 *        作为锁定池对象的锁名称
	 */
	public SoftReferencePool(String lockName){
		this("me.jor.pool.SoftReferencePool-"+lockName,new ClearInCurrentThreadPolicy<E>());
	}
	/**
	 * 创建对象池实例，使用SoftReferencePool.ClearByExecutorPolicy清除空引用
	 * @param lockName
	 *        作为锁定池对象的锁名称
	 * @param clearExecutor
	 *        执行清除空引用的线程池对象
	 *        
	 */
	public SoftReferencePool(String lockName, Executor clearExecutor){
		this(lockName, new ClearByExecutorPolicy<SoftReferencePool<E>>(clearExecutor,new ClearInCurrentThreadPolicy<E>()));
	}
	/**
	 * 创建对象池实例，使用指定的ClearPolicy实例作为清除策略清除空引用
	 * @param lockName
	 *        作为锁定池对象的锁名称
	 * @param clearPolicy
	 *        清除空引用策略
	 */
	public SoftReferencePool(String lockName, ClearPolicy<SoftReferencePool<E>> clearPolicy){
		this.lockName="me.jor.pool.SoftReferencePool-"+lockName;
		this.clearPolicy=clearPolicy;
	}
	
	private void clear(){
		clearPolicy.clear(this);
	}
	private Deque<SoftReference<E>> getStack(){
		Deque<SoftReference<E>> stack=this.stack.get();
		if(stack==null){
			stack=new LinkedList<SoftReference<E>>();
			this.stack=new SoftReference<Deque<SoftReference<E>>>(stack);
			empty.set(true);
		}
		return stack;
	}
	/**
	 * 获取软引用的对象
	 * @return E
	 *         返回值不是软引用对象本身，而是软引用所引用的对象
	 */
	public E get(){
		Lock lock=LockCache.getReentrantLock(lockName);
		try{
			lock.lock();
			Deque<SoftReference<E>> stack=getStack();
			if(stack.isEmpty()){
				empty.set(true);
				return null;
			}
			return stack.pop().get();
		}finally{
			clear();
			lock.unlock();
		}
	}
	/**
	 * 将参数引用的对象推入池
	 * @param e 
	 */
	public void put(E e){
		Lock lock=LockCache.getReentrantLock(lockName);
		try{
			lock.lock();
			clear();
			getStack().push(new SoftReference<E>(e));
			empty.set(false);
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * 获取软引用对象池实例
	 * @param poolName  池名
	 * @param lockName  锁名
	 * @return SoftReferencePool<E>
	 */
	public static <K,E> SoftReferencePool<E> getPool(K poolName,String lockName){
		Cache<K,SoftReferencePool<E>> poolCache=Cache.getCache("me.jor.pool.SoftReferencePool.getPool");
		SoftReferencePool<E> pool=poolCache.get(poolName);
		if(pool==null){
			poolCache.putIfAbsent(poolName, new SoftReferencePool<E>(lockName));
			return poolCache.get(poolName);
		}
		return pool;
	}
	
	/**
	 * 在当前线程清除空引用
	 *
	 */
	public static class ClearInCurrentThreadPolicy<E> implements ClearPolicy<SoftReferencePool<E>>{
		@Override
		public void clear(SoftReferencePool<E> pool) {
			final Deque<SoftReference<E>> deque=pool.getStack();
			try {
				ExecutingOnce.executeAndReturnImmediately("CLEAR_NULLREF_IN_me.jor.pool.SoftReferencePool", new Task(){
					@Override
					public <T> T execute() throws Throwable {
						for(SoftReference<E> sr=deque.peekLast();sr!=null && sr.get()==null;sr=deque.peekLast()){
							deque.removeLast();
						}
						return null;
					}
				});
			} catch (Throwable e) {}
		}
	}

	/**
	 * 当池内存储的最后一个对象是空的软引用时清除所有
	 */
	public static class ClearAllOnNullReferenceFoundPolicy<E> implements ClearPolicy<SoftReferencePool<E>>{
		@Override
		public void clear(final SoftReferencePool<E> pool) {
			try {
				ExecutingOnce.executeAndReturnImmediately("CLEAR_ALL_REF_IN_me.jor.pool.SoftReferencePool", new Task(){
					@Override
					public <T> T execute() throws Throwable {
						if(!pool.empty.get() && pool.getStack().peekLast().get()==null){
							pool.getStack().clear();
						}
						return null;
					}
				});
			} catch (Throwable e) {}
		}
		
	}
}
