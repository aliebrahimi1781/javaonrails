package me.jor.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 此类缓存ReentrantLock和ReentrantReadWriteLock.ReadLock ReentrantReadWriteLock.WriteLock
 * 如果要得到一对读写锁，读锁与写锁必须使用同样的key.
 * 一个key对应一个ReentrantReadWriteLock对象或ReentrrantLock对象
 **/
public class LockCache {
	private static final String LOCK_CACHE;
	private static final String RW_LOCK_CACHE;

	static{
		String className=LockCache.class.getName();
		LOCK_CACHE=className+".lockCache";
		RW_LOCK_CACHE=className+".rwLockCache";
	}
	/**
	 * 重复锁
	 */
	public static final int REENTRANT_LOCK=0;
	/**
	 * 读锁
	 */
	public static final int READ_LOCK=1;
	/**
	 * 写锁
	 */
	public static final int WRITE_LOCK=2;
	/**
	 * 读写锁
	 */
	public static final int READ_WRITE_LOCK=3;
	
	/**
	 * 从缓存中得到读写锁对象如果缓存中不存在此锁对象，将此锁对象放入缓存
	 * @param name 锁名称
	 * @return ReentrantReadWriteLock 读写锁对象
	 * @throws 
	 * @exception
	 */
	public static ReentrantReadWriteLock getReadWriteLock(String name){
		Cache<ReentrantReadWriteLock> cache=Cache.getCache(RW_LOCK_CACHE);
		
		ReentrantReadWriteLock lock=cache.get(name);
		if(lock!=null){
			return lock;
		}else{
			return cache.putIfAbsent(name, new ReentrantReadWriteLock());
		}
	}
	/**
	 * 得到锁对象
	 * @param name 锁名称
	 * @param lockType 锁类型
	 * @return Lock 锁对象
	 * @throws 
	 * @exception
	 */
	private static Lock get(String name, int lockType){
		switch(lockType){
		case REENTRANT_LOCK:
			Cache<Lock> cache=Cache.getCache(LOCK_CACHE);
			Lock lock=cache.get(name);
			if(lock!=null){
				return lock;
			}else{
				return cache.putIfAbsent(name, new ReentrantLock());
			}
		case READ_LOCK:
			return getReadWriteLock(name).readLock();
		case WRITE_LOCK:
			return getReadWriteLock(name).writeLock();
		default:
			throw new IllegalArgumentException("Illegal lock type: #LOCK_TYPE# " +
				"only LockCache.REENTRENT_LOCK LockCache.READ_LOCK LockCache.WRITE_LOCK are legal"
				.replace("#LOCK_TYPE#", String.valueOf(lockType)));
		}
	}
	/**
	 * 得到重复锁
	 * @param name 锁名称
	 * @return ReentrantLock 重复锁对象
	 * @throws 
	 * @exception
	 */
	public static ReentrantLock getReentrantLock(String name){
		return (ReentrantLock)get(name,REENTRANT_LOCK);
	}
	/**
	 * 得到只读锁
	 * @param name 锁名称
	 * @return ReentrantReadWriteLock.ReadLock 只读锁对象
	 * @throws 
	 * @exception
	 */
	public static ReentrantReadWriteLock.ReadLock getReadLock(String name){
		return (ReentrantReadWriteLock.ReadLock)get(name,READ_LOCK);
	}
	/**
	 * 得到写锁
	 * @param name 锁名称
	 * @return ReentrantReadWriteLock.WriteLock 写锁对象
	 * @throws 
	 * @exception
	 */
	public static ReentrantReadWriteLock.WriteLock getWriteLock(String name){
		return (ReentrantReadWriteLock.WriteLock)get(name,WRITE_LOCK);
	}
	/**
	 * 从缓存中移除锁对象
	 * @param name 锁名称
	 * @param lockType void 锁类型
	 * @throws 
	 * @exception
	 */
	private static void remove(String name, int lockType){
		switch(lockType){
		case REENTRANT_LOCK:
			synchronized(name){
				Cache.getCache(LOCK_CACHE).remove(name);
			}
			break;
		case READ_LOCK:case WRITE_LOCK:case READ_WRITE_LOCK:
			synchronized(name){
				Cache.getCache(RW_LOCK_CACHE).remove(name);
			}
			break;
		}
	}
	/**
	 * 从缓存中移除重复锁
	 * @param name void
	 * @throws 
	 * @exception
	 */
	public static void removeReentrantLock(String name){
		remove(name, REENTRANT_LOCK);
	}
	/**
	 * 从缓存中移除读写锁
	 * @param name void
	 * @throws 
	 * @exception
	 */
	public static void removeReadWriteLock(String name){
		remove(name, READ_WRITE_LOCK);
	}
}
