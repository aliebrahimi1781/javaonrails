package me.jor.util.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import me.jor.common.Task;
import me.jor.util.LockCache;

/**
 * 实践证明高并发情况下可能会导致程序不再响应，可能是由于尚未发现的死锁引起。目前还没有能力解决
 * 
 * 所有注册在此类的对象中的线程执行且只执行一次指定的Task实现。
 * 只要key相同，就被认为执行的任务相同，所以虽然不是必须，但是最好确保key与task严格一一对应。
 * 所有持有相同key的线程将会竞争执行任务的机会，
 * 一旦有线程获得执行机会，其它线程将会等待任务执行完成。<br/>
 * 
 * 如果key与task不一一对应，则获得被执行的task对象是获得任务执行机会的线程所提交的那个task对象，
 * 其它同时提交的和在执行过程中提交的同key的task对象会被忽略。<br/>
 * 
 * 如果任务执行过程中发生了异常，所有其它正在等待的线程将被唤醒并抛出ExecutingOnce.ThrowableOccuredOnWaitingException，
 * 执行任务的线程抛出ThrowableOccuredOnExecutingException，
 * 这两个异常都封装了任务执行过程中实际抛出的异常。<br/>
 * 
 * 最好用于并发情况下，只要执行一次即可的非常耗时的操作或任务；
 * 或者用于并发情况下，不是非常耗时，但必须保证一定要执行且只能被执行一次的操作或任务。<br/>
 * 
 * 在非并发情况下不必使用此类，
 * 在并发情况下，如果不是严格要求必须且只能被执行一次的操作或任务不必使用此类，
 * 在并发情况下，多次执行同一操作或任务不会影响结果，且耗时极短的操作或任务不必使用此类。<br/>
 * 
 * 只需要调用静态的executeAndWait(String key, Task task)或executeAndReturnImmediately(String key, Task task)。
 * */
@Deprecated
public class ExecutingOnce implements Task{
	private static final Map<String, Task> map=new ConcurrentHashMap<String, Task>();
	private CountDownLatch finished=new CountDownLatch(1);
	private Task task;
	private Object result;
	private AtomicBoolean executing=new AtomicBoolean(false);
	private String key;
	private Throwable throwable;
	private ThreadLocal<Boolean> returnImmediately=new ThreadLocal<Boolean>();
	private ThreadLocal<Long> threadId=new ThreadLocal<Long>();
	private ExecutingOnce(String key,Task task){
		this.key=key;
		this.task=task;
	}
	/**
	 * 只要key相同，就被认为执行的任务相同，所以虽然不是必须，但是最好确保key与task严格一一对应。
	 * 所有持有相同key的线程将会竞争执行任务的机会，
	 * 一旦有线程获得执行机会，其它线程将会等待任务执行完成。<br/>
	 * 
	 * 如果key与task不一一对应，则获得被执行的task对象是获得任务执行机会的线程所提交的那个task对象，
	 * 其它同时提交的和在执行过程中提交的同key的task对象会被忽略。<br/>
	 * 
	 * 如果执行过程中发生了异常，所有其它正在等待的线程将被唤醒并抛出ExecutingOnce.ThrowableOccuredOnWaitingException，
	 * 执行任务的线程抛出ThrowableOccuredOnExecutingException，
	 * 这两个异常都封装了任务执行过程中实际抛出的异常。<br/>
	 * 
	 * <b>注意:</b> 如果一定要在Task对象处理Task.execute产生的异常，请在处理完成后抛出，否则持有相同key的并发线程将无法保持运行结果的一致性<br/>
	 * <b>注意:</b> 如果用try块包含ExecutingOnce.execute(String, Task)，并捕获此调用抛出的异常，则无法保证在产生异常时并发线程会保持运行结果的一致性。
	 *              推荐做法是使用Thread.currentThread().setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)处理此调用产生的异常
	 * @param key
	 *        以此参数惟一确定一个任务，最好确保与task一一对应
	 * @param task
	 *        待执行的任务
	 * */
	public static <T> T executeAndWait(String key, Task task) throws Throwable{
		return confirmTask(key,task,false).execute();
	}
	/**
	 * 
	 * @param key
	 * @param task
	 * @param returnImmediately 
	 *        如果是true此方法立即返回，是否等待task执行完成并得到执行结果由调用方决定。
	 *        如果要等待task执行完成并得到执行结果可调用await()方法。
	 * @return 当前被执行的ExecutingOnce对象
	 * @throws Throwable
	 * @see ExecutingOnce.execute(String key, Task task)
	 */
	public static ExecutingOnce executeAndReturnImmediately(String key, Task task) throws Throwable{
		return confirmTask(key,task,true).execute();
	}
	private static Task confirmTask(String key, Task task, boolean returnImmediately){
		Task intask=map.get(key);
		if(intask==null){
			Lock lock=LockCache.getReentrantLock(key);
			try{
				lock.lock();
				intask=map.get(key);
				if(intask==null){
					intask=new ExecutingOnce(key,task);
					map.put(key, intask);
				}
				return intask;
			}finally{
				lock.unlock();
			}
		}
		((ExecutingOnce)intask).returnImmediately.set(returnImmediately);
		return intask;
	}
	/**
	 * 
	 * @return 如果调用了ExecutingOnce.executeAndReturnImmediately(String key, Task task);此时调用此方法会立即返回。
	 *         如果此时任务刚好已经执行完成，就会返回执行结果，否则返回null。
	 *         如果要等待任务执行完成并得到结果，最好调用此类的await()成员方法，或调用ExecutingOnce.executeAndWait(String key, Task task);
	 * @throws Throwable
	 * @see me.jor.common.Task#execute()
	 */
	@SuppressWarnings("unchecked")
	public <T> T execute() throws Throwable{
		Thread ct=Thread.currentThread();
		long ctid=ct.getId();
		if(null==threadId.get()){
			threadId.set(ctid);
			if(executing.getAndSet(true)==false){
				final Thread.UncaughtExceptionHandler tueh=ct.getUncaughtExceptionHandler();
				ct.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						throwable=e;
						map.remove(key);
						finished.countDown();
						tueh.uncaughtException(t, new ThrowableOccuredOnExecutingException(e));
					}
				});
				result=task.execute();
				map.remove(key);
				finished.countDown();
			}else{
				Boolean immediately=returnImmediately.get();
				if(immediately==null || !immediately){
					await();
				}else{
					return (T)this;//只在内部使用
				}
			}
		}
		return (T)result;
	}
	/**
	 * 等待任务执行完成并返回执行结果，只在调用方需要立即返回或由调用方决定是否等待执行结果时使用
	 * @return 任务执行结果
	 * @throws InterruptedException
	 */
	public <T> T await() throws InterruptedException{
		finished.await();
		try{
			if(throwable!=null){
				throw new ThrowableOccuredOnWaitingException(throwable);
			}
			return (T)result;
		}finally{
			returnImmediately.remove();
		}
	}
	
	public static class ThrowableOccuredOnWaitingException extends RuntimeException{
		private static final long serialVersionUID = 8343209855349540409L;
		public ThrowableOccuredOnWaitingException(Throwable cause) {
			super(cause);
		}
	}
	public static class ThrowableOccuredOnExecutingException extends RuntimeException{
		private static final long serialVersionUID = 3800198587264329948L;
		public ThrowableOccuredOnExecutingException(Throwable cause) {
			super(cause);
		}
	}
}
