package me.jor.pool;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.jor.common.GlobalObject;
import me.jor.nio.socket.constant.NIOSocketConstant;

public class SocketChannelPool {
	private Queue<ChannelInPool> idle;
	private int timeout;
	

	public SocketChannelPool(){
		this(NIOSocketConstant.getSOCKETCHANNEL_TIMEOUT());
	}
	public SocketChannelPool(int timeout, boolean autoRecycle){
		idle=new ConcurrentLinkedQueue<ChannelInPool>();
		this.timeout=timeout;
		if(autoRecycle){
			GlobalObject.getExecutorService().execute(new Runnable(){
				@Override
				public void run() {
					SocketChannelPool pool=SocketChannelPool.this;
					int wait=pool.timeout<60000?60000:pool.timeout;
					for(;;){
						try{
							synchronized(pool){
								pool.wait(wait);
								recycle();
							}
						}catch(InterruptedException e){}
					}
				}
			});
		}
	}
	public SocketChannelPool(int timeout){
		this(timeout,true);
	}
	public ChannelInPool getChannel(){
		ChannelInPool inpool;
		do{
			inpool=idle.poll();
			synchronized(inpool){
				inpool.resetStartTime();
			}
		}while(!inpool.isOpen());
		return inpool;
	}
	public void recycle(ChannelInPool inpool) throws IOException{
		if(!inpool.destroy(timeout) && inpool.isOpen()){
			inpool.resetStartTime();
			idle.offer(inpool);
		}
	}
	private boolean hasIdle(){
		return !idle.isEmpty();
	}
	
	public void recycle(){
		if(hasIdle()){
			Iterator<ChannelInPool> itr=idle.iterator();
			while(itr.hasNext()){
				try{
					if(itr.next().destroy(timeout)){
						itr.remove();
					}
				}catch(IOException e){
					itr.remove();
				}
			}
		}
	}
}
