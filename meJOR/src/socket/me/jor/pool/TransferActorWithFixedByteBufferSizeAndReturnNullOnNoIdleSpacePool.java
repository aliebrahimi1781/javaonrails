package me.jor.pool;

import java.util.concurrent.ArrayBlockingQueue;

import me.jor.exception.PoolException;

class TransferActorWithFixedByteBufferSizeAndReturnNullOnNoIdleSpacePool extends TransferActorWithFixedByteBufferSizePool{

	TransferActorWithFixedByteBufferSizeAndReturnNullOnNoIdleSpacePool(int fixedBufferSize) {
		super(fixedBufferSize);
	}

	@Override
	public TransferActor getTransferActor(int size) {
		ArrayBlockingQueue<TransferActor> idle=super.idle;
		TransferActor actor=null;
		try {
			actor=idle.take();
		} catch (InterruptedException e) {
			throw new PoolException(e);
		}
		if(actor!=null){
			super.busy.add(actor);
		}
		return actor;
	}
}
