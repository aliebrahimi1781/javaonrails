package me.jor.pool;

import java.nio.ByteBuffer;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

abstract class TransferActorWithActiveAllocationByteBufferSizePool extends TransferActorPool{

	protected Queue<MovableTransferActor> actorQueue=new PriorityQueue<MovableTransferActor>();
	@Override
	public void recycle(TransferActor actor) {
		actor.reinstate();
	}
	
	@Override
	public TransferActor getTransferActor(int size) {
		synchronized(this){
			ByteBuffer global=TransferActorPool.GLOBAL_BUFFER;
			TransferActor actor=null;
			for(;;){
				int position=global.position();
				int capacity=global.capacity();
				if(capacity-position>=size){
					gc();
					this.notify();
				}
				position=global.position();
				if(capacity-position>=size){
					actor=createActorOnNoIdleSpace(size);
					if(actor!=null && actor.buffer==null){
						continue;
					}
				}else{
					global.limit(position+size);
					actor=new MovableTransferActor(position,global.slice());
					global.limit(capacity);
					actorQueue.add((MovableTransferActor)actor);
				}
				return actor;
			}
		}
	}
	protected abstract TransferActor createActorOnNoIdleSpace(int size);
	
	/**
	 * 在发现剩余池空间不足以分配新ByteBuffer时，执行gc()
	 */
	protected void gc(){
		Queue<MovableTransferActor> queue=new PriorityBlockingQueue<MovableTransferActor>();
		ByteBuffer global=TransferActorPool.GLOBAL_BUFFER;
		int globalCapacity=global.capacity();
		int p=0;
		global.position(p);
		global.limit(globalCapacity);
		for(;!actorQueue.isEmpty();){
			MovableTransferActor actor=actorQueue.poll();
			int position=actor.position();
			int capacity=actor.capacity();
			if(p<position){
				global.limit(capacity);
				try{
					actor.move(p, global.slice());
				}catch(NullPointerException e){
					continue;
				}
			}
			if(!actor.isGabage()){
				p+=capacity;
				global.limit(globalCapacity);
				global.position(p);
				queue.offer(actor);
			}
		}
		actorQueue=queue;
	}
}
