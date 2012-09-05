package me.jor.pool;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract class TransferActorWithFixedByteBufferSizePool extends TransferActorPool{

	private int fixedBufferSize;
	
	protected final Lock lock=new ReentrantLock();
	
	protected ArrayBlockingQueue<TransferActor> idle;
	protected Set<TransferActor> busy;
	
	TransferActorWithFixedByteBufferSizePool(int fixedBufferSize){
		this.fixedBufferSize=fixedBufferSize;
		int capacity=TransferActorPool.GLOBAL_BUFFER.capacity();
		capacity=capacity%fixedBufferSize==0?capacity/fixedBufferSize:capacity/fixedBufferSize+1;
		idle=new ArrayBlockingQueue<TransferActor>(capacity);
		busy=new ConcurrentSkipListSet<TransferActor>();
		init();
	}
	
	private void init(){
		ByteBuffer global=TransferActorPool.GLOBAL_BUFFER;
		for(int position=0,limit=fixedBufferSize,capacity=global.capacity();
				position<capacity;
				position=limit,limit+=fixedBufferSize){
			global.position(position);
			global.limit(limit<capacity?limit:capacity);
			idle.offer(new TransferActor(global.slice()));
		}
	}

	@Override
	public void recycle(TransferActor actor) {
		actor.reinstate();
		if(busy.remove(actor)){
			try {
				idle.put(actor);
			} catch (InterruptedException e) {
				//只有因队列满发生等待时中断才会抛出异常，但是队列大小与受池维护的actor数量一致，
				//因此永远不会抛出异常
			}
		}
	}

	protected final int getFixedBufferSize(){
		return this.fixedBufferSize;
	}
}
