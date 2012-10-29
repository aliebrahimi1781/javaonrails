package me.jor.util.concurrent;

import me.jor.common.Task;
import me.jor.util.Cache;

import me.jor.exception.ReinstatableExecutionAwaitSignaledException;

/**
 * 可以认为是me.jor.util.concurrent.ExecutingOnce的一个轻量级实现，但是比ExecutingOnce更灵活、更强大。
 * 可以为这类场景提供更大自由度的实现。
 * 只需要在代码中使用条件判断和调用由类创建的对象的await(), signal()方法即可实现。
 * 另外可通过reinstate()恢复对象的初始状态，以达到反复使用的目的。
 * 在运行过程中，如果执行线程发生了异常，可调用setThrowable()，然后等待线程可通过getThrowable()得到这个异常<br/>
 * class Test{<br/>
 *     private await=new ExecutionAwait();<br/>
 *     //有并发线程时，只有await()返回false的那个线程才会执行signal(), 其它线程都会等待，直到signal()返回。<br/>
 *     public void test1(){<br/>
 *         boolean w=await.await();<br/>
 *         if(w){<br/>
 *             //do sth<br/>
 *         }else{//如果else子句从未被执行过，则在并发调用时会有一个线程执行else子句，其它线程等待此线程执行else结束时再执行if。<br/>
 *               //如果采用无参构造器，else子句只会在await对象的生命周期内执行一次，执行else时并发的其它线程等待else执行结束再执行if；以后每次调用只执行if子句<br/>
 *               //如果采用一参构造器且实参为true，else子句会在await对象的生命周期内，每次发生并发访问时或单线程访问时都会执行一次else子句，其它线程等待else结束时再执行if<br/>
 *               //如果采用一参构造器且实参为false，则等价于采用无参构造器<br/>
 *               //如果是new ExecutionAwait(true, false)，则每次发生并发访问或单线程访问时都会执行一次else，并发的其它线程不会等待而是立即执行if
 *               //如果是new ExecutionAwait(false, false)，则else只会执行一次，以后每次调用只执行if，而且永远不会等待
 *               //第二个参数传入true时，等价于调用单参构造器
 *         	   try{<br/>
 *                 //do sth else<br/>
 *             }catch(Exception e){<br/>
 *                 //do sth
 *                 await.setThrowable(e);
 *                 //do sth
 *             }finally{<br/>
 *                 await.signal();<br/>//如果单参传true,或双参第一个传true，则在调用signal()后，应使用新的ExecutionAwait对象调用await()<br/>
 *             }<br/>
 *         }<br/>
 *     }<br/>
 * }<br/>
 * */
public class ExecutionAwait {
	
	private volatile boolean await;
	private volatile boolean wait;
	private volatile boolean reinstate;
	private volatile boolean signaled;
	private volatile Throwable throwable;
	private volatile Object result;
	
	public ExecutionAwait(){
		this(false);
	}
	public ExecutionAwait(boolean reinstate){
		this(reinstate, true);
	}
	public ExecutionAwait(boolean reinstate, boolean wait){
		this.await=false;
		this.wait=wait;
		this.reinstate=reinstate;
	}
	
	public boolean await() throws InterruptedException{
		if(signaled && reinstate){
			throw new ReinstatableExecutionAwaitSignaledException("Reinstatable ExecutionAwait object has been signaled, a new object should be created");
		}
		boolean await;
		synchronized(this){
			await=this.await;
			this.await=true;
			if(await && wait){
				this.wait();
			}
		}
		return await;
	}
	public void signal(){
		if(!signaled){
			synchronized(this){
				if(!signaled){
					signaled=true;
					this.notifyAll();
					if(reinstate){
						this.await=false;
					}else{
						this.wait=false;
					}
				}
			}
		}
	}
	/**
	 * 判断是否已经调用过
	 * @exception
	 * @return boolean
	 * @see
	 */
	public boolean needNewInstance(){
		return signaled&&reinstate;
	}
	private <T> T innerExecute(Task task)throws Throwable{
		try{
			result=task.execute();
			return (T)result;
		}catch(Throwable throwable){
			this.throwable=throwable;
			throw throwable;
		}finally{
			signal();
		}
	}
	/**
	 * 
	 * @param task 在else子句执行，执行结束所有并发线程返回task的执行结果
	 * @return
	 * @throws Throwable
	 */
	public <T> T execute(Task task) throws Throwable{
		if(!this.await()){
			result=innerExecute(task);
		}
		return (T)result;
	}
	/**
	 * 
	 * @param waitTask   在if子句执行
	 * @param nowaitTask 在else子句执行
	 * @return
	 * @throws Throwable
	 */
	public <T> T execute(Task waitTask, Task nowaitTask) throws Throwable{
		if(this.await()){
			if(throwable==null){
				return waitTask.execute();
			}else{
				throw throwable;
			}
		}else{
			return innerExecute(nowaitTask);
		}
	}
	
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
	private static ExecutionAwait getExecutionAwait(String name, boolean reinstate, boolean wait){
		ExecutionAwait await=(ExecutionAwait)Cache.getCache("me.jor.util.concurrent.ExecutionAwait.getExecutionAwait").get(name);
		if(await==null){
			await=(ExecutionAwait)Cache.getCache("me.jor.util.concurrent.ExecutionAwait.getExecutionAwait").putIfAbsent(name, new ExecutionAwait(reinstate,wait));
		}else if(reinstate && await.signaled){
			await=(ExecutionAwait)Cache.getCache("me.jor.util.concurrent.ExecutionAwait.getExecutionAwait").replace(name, new ExecutionAwait(reinstate,wait));
		}
		return await;
	}
	public static <T> T execute(String name, boolean reinstate, boolean wait, Task task) throws Throwable{
		return getExecutionAwait(name, reinstate, wait).execute(task);
	}
	public static <T> T execute(String name, boolean reinstate, boolean wait, Task waitTask, Task nowaitTask) throws Throwable{
		return getExecutionAwait(name, reinstate, wait).execute(waitTask, nowaitTask);
	}
	public static <T> T execute(String name, boolean reinstate, Task task) throws Throwable{
		return getExecutionAwait(name, reinstate, true).execute(task);
	}
	public static <T> T execute(String name, boolean reinstate, Task waitTask, Task nowaitTask) throws Throwable{
		return getExecutionAwait(name, reinstate, true).execute(waitTask, nowaitTask);
	}
	public static <T> T execute(String name, Task task) throws Throwable{
		return getExecutionAwait(name, false, true).execute(task);
	}
	public static <T> T execute(String name, Task waitTask, Task nowaitTask) throws Throwable{
		return getExecutionAwait(name, false, true).execute(waitTask, nowaitTask);
	}
}
