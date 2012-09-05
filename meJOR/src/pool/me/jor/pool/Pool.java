package me.jor.pool;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.jor.exception.PoolException;

public abstract class Pool<T> implements Runnable{
	
	private int maxSize;//池的大小，池内对象数不能高于此值
	private long lastRequest;//上次从池请求对象的时间
	private long longestPeriod;//最长请求时间间隔
	private long checkPeriod;//每隔这么多毫秒数检查查一次是否需要清理池，如果持续未请求池的时间超过上限就开始清理
	private int minCheckPeriod;//最小检查时间间隔
	private int minSize;//池内对象最小数目，池内对象数不能低此值
	private int minIdelSize;//池内最小空闲对象数
	private boolean autoChangeSize;
	private boolean toClose;
	private BlockingQueue<T> idel;//空闲对象
	private BlockingQueue<T> working;//正在使用的对象
	
	private DecreaseRate decreaseRate;
	private IncreaseRate increaseRate;
	private CheckRate checkRate;
	
	private Thread observerThread;
	
	protected Pool(){
		this(60000, 60000, 3600000, 1, 1, 0, true, true);
		toClose=false;
	}
	protected Pool(boolean defaultRate){
		this(60000, 60000, 3600000, 1, 1, 0, defaultRate, true);
		toClose=false;
	}
	public Pool(long checkPeriod, int minCheckPeriod, long longestPeriod, 
		    int maxSize, int minIdelSize, int minSize, boolean defaultRate){
		this(checkPeriod, minCheckPeriod, longestPeriod,
			 maxSize, minIdelSize, minSize, defaultRate, true);
	}
	
	
	public Pool(long checkPeriod, int minCheckPeriod, long longestPeriod, 
			    int maxSize, int minIdelSize, int minSize, 
			    boolean defaultRate, boolean autoChangeSize) {
		this.checkPeriod = checkPeriod;
		this.minCheckPeriod=minCheckPeriod;
		this.longestPeriod = longestPeriod;
		this.maxSize = maxSize;
		this.minIdelSize = minIdelSize;
		this.minSize = minSize;
		this.autoChangeSize=autoChangeSize;
		
		idel=new LinkedBlockingQueue<T>();
		working=new LinkedBlockingQueue<T>();
		
		if(defaultRate){
			createRates();
		}
		if(autoChangeSize){
			observerThread= new SetObserverThread(this);
			observerThread.setDaemon(true);
			observerThread.start();
		}
	}
	private void createRates(){
		decreaseRate=new DecreaseRate(){
			public int rate() {
				int size=idel.size();
				return maxSize>size?size/2:size-maxSize+maxSize/2;
			}
		};
		increaseRate=new IncreaseRate(){
			public int rate(){
				int size=idel.size();
				int rate=size/2;
				int end=maxSize-size-working.size();
				return rate>end?end:rate;
			}
		};
		checkRate=new CheckRate(){
			public int rate(){
				int rate=((int)(System.nanoTime()-lastRequest))/2;
				return rate<minCheckPeriod?minCheckPeriod:rate;
			}
		};
	}


	/**
	 * 根据需要由内部线程调用以决定是否要向池内增加新对象
	 */
	protected void increase(){
		increase(beforeIncrease());
	}
	protected void increase(T t){
		if(!toClose){
			try {
				idel.put(t);
			} catch (InterruptedException e) {
				throw new PoolException(e);
			}
		}
	}
	protected abstract T beforeIncrease();
	protected T decrease(){
		try{
			T t=idel.take();
			decrease(t);
			return t;
		}catch(InterruptedException e){
			throw new PoolException(e);
		}
	}
	protected void decrease(T t){
		idel.remove(t);
		afterDecrease(t);
	}
	
	protected abstract void afterDecrease(T t);
	
	protected final boolean toCompletlyClose(T t){
		return !(this.returnToPool(t) || this.contains(t));
	}
	
	public void close(){
		close(true);
	}
	public void close(boolean wait){
		toClose=true;
		for(;wait && working.size()>0;){
			try {
				synchronized(this){
					this.wait(200);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		clearPool(working);
		clearPool(idel);
	}
	private void clearPool(BlockingQueue<T> queue){
		while(!queue.isEmpty()){
			try {
				T t=queue.take();
				Method cm=t.getClass().getMethod("close");
				if(cm!=null){
					cm.invoke(t);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public final T get(){
		if(toClose){
			return null;
		}
		try{
			if(!idel.isEmpty()){
				T t=idel.take();
				working.put(t);
				return t;
			}else if(working.size()>=maxSize){
				return null;
			}else{
				synchronized(observerThread){
					observerThread.notify();
				}
				try{
					synchronized(this){
						this.wait();
					}
				}catch(Exception e){}
				if(idel.isEmpty()){
					return null;
				}else{
					return get();
				}
			}
		}catch(InterruptedException e){
			throw new PoolException(e);
		}finally{
			this.lastRequest=System.nanoTime();
		}
	}
	
	public boolean returnToPool(T t){
		if(working.remove(t)){
			idel.add(t);
			return true;
		}
		return false;
	}
	
	public final int getCurrentPoolSize(){
		return this.idel.size()+this.working.size();
	}
	public final boolean contains(T t){
		return idel.contains(t);
	}

	private class SetObserverThread extends Thread{
		private Pool<T> pool;
		public SetObserverThread(Pool<T> pool){
			this.pool=pool;
		}
		public void run(){
			for(;;){
				int idelSize=idel.size();
				int workingSize=working.size();
				int size=idelSize+workingSize;
				long period=System.nanoTime()-lastRequest;
				if(idelSize<minIdelSize && size<maxSize && (lastRequest<=0 || period<longestPeriod)){
					for(int s=increaseRate.rate();s>0;s--){
						increase();
					}
				}else if(period>=longestPeriod){
					for(int s=decreaseRate.rate();s>0;s--){
						decrease();
					}
				}
				synchronized(pool){
					pool.notifyAll();
				}
				pool.run();
				try {
					synchronized(this){
						wait((long)checkRate.rate());
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public static abstract interface Rate{
		public int rate();
	}
	public static interface DecreaseRate extends Rate{}
	public static interface IncreaseRate extends Rate{}
	public static interface CheckRate extends Rate{}
	
	public void run(){}
	
	protected Iterator<T> idelIterator(){
		return idel.iterator();
	}
	protected Iterator<T> workingIterator(){
		return working.iterator();
	}
	
	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}
	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	/**
	 * @return the lastRequest
	 */
	public long getLastRequest() {
		return lastRequest;
	}
	/**
	 * @param lastRequest the lastRequest to set
	 */
	public void setLastRequest(long lastRequest) {
		this.lastRequest = lastRequest;
	}
	/**
	 * @return the checkPeriod
	 */
	public long getCheckPeriod() {
		return checkPeriod;
	}
	/**
	 * @param checkPeriod the checkPeriod to set
	 */
	public void setCheckPeriod(long checkPeriod) {
		this.checkPeriod = checkPeriod;
	}
	/**
	 * @return the minSize
	 */
	public int getMinSize() {
		return minSize;
	}
	/**
	 * @param minSize the minSize to set
	 */
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}
	/**
	 * @return the minIdelSize
	 */
	public int getMinIdelSize() {
		return minIdelSize;
	}
	/**
	 * @param minIdelSize the minIdelSize to set
	 */
	public void setMinIdelSize(int minIdelSize) {
		this.minIdelSize = minIdelSize;
	}
	/**
	 * @return the longestPeriod
	 */
	public long getLongestPeriod() {
		return longestPeriod;
	}
	/**
	 * @param longestPeriod the longestPeriod to set
	 */
	public void setLongestPeriod(long longestPeriod) {
		this.longestPeriod = longestPeriod;
	}
	/**
	 * @return the decreaseRate
	 */
	public DecreaseRate getDecreaseRate() {
		return decreaseRate;
	}
	/**
	 * @param decreaseRate the decreaseRate to set
	 */
	public void setDecreaseRate(DecreaseRate decreaseRate) {
		this.decreaseRate = decreaseRate;
	}
	/**
	 * @return the increaseRate
	 */
	public IncreaseRate getIncreaseRate() {
		return increaseRate;
	}
	/**
	 * @param increaseRate the increaseRate to set
	 */
	public void setIncreaseRate(IncreaseRate increaseRate) {
		this.increaseRate = increaseRate;
	}
	/**
	 * @return the checkRate
	 */
	public CheckRate getCheckRate() {
		return checkRate;
	}
	/**
	 * @param checkRate the checkRate to set
	 */
	public void setCheckRate(CheckRate checkRate) {
		this.checkRate = checkRate;
	}
	/**
	 * @return the minCheckPeriod
	 */
	public int getMinCheckPeriod() {
		return minCheckPeriod;
	}
	/**
	 * @param minCheckPeriod the minCheckPeriod to set
	 */
	public void setMinCheckPeriod(int minCheckPeriod) {
		this.minCheckPeriod = minCheckPeriod;
	}
	/**
	 * @return the autoChangeSize
	 */
	public boolean isAutoChangeSize() {
		return autoChangeSize;
	}
	/**
	 * @param autoChangeSize the autoChangeSize to set
	 */
	public void setAutoChangeSize(boolean autoChangeSize) {
		this.autoChangeSize = autoChangeSize;
	}
	
	
}
