package me.jor.pool;

import java.util.concurrent.ArrayBlockingQueue;

import me.jor.exception.PoolException;

class TransferActorWithFixedByteBufferSizeAndWaitOnNoIdleSpacePool extends TransferActorWithFixedByteBufferSizePool{

	TransferActorWithFixedByteBufferSizeAndWaitOnNoIdleSpacePool(int fixedBufferSize) {
		super(fixedBufferSize);
	}

	@Override
	public TransferActor getTransferActor(int size) {
		ArrayBlockingQueue<TransferActor> idle=super.idle;
		TransferActor actor=null;
		while(actor==null){
			try {
				actor=idle.take();
			} catch (InterruptedException e) {
				throw new PoolException(e);
			}
		}
		super.busy.add(actor);
		return actor;
	}

}
