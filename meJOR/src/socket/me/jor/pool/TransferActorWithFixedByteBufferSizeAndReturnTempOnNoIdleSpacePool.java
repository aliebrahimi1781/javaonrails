package me.jor.pool;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import me.jor.exception.PoolException;

class TransferActorWithFixedByteBufferSizeAndReturnTempOnNoIdleSpacePool extends TransferActorWithFixedByteBufferSizePool{

	/**
	 * 这是为超出池大小时缓存临时ByteBuffer对象而创建。
	 * 初始化应用时应当努力保证池大小尽可能的合适，需要使用此对象的机会应当保证尽量的少；
	 * 因此清除软引用池内的空引用只需要占用当前线程即可。
	 */
	private SoftReferencePool<TransferActor> tempPool=new SoftReferencePool<TransferActor>("me.jor.pool.FixedByteBufferSizeAndReturnTempOnEmpty");
	
	TransferActorWithFixedByteBufferSizeAndReturnTempOnNoIdleSpacePool(int fixedBufferSize) {
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
		}else{
			actor=tempPool.get();
			if(actor==null){
				actor=new TransferActor(ByteBuffer.allocate(super.getFixedBufferSize()));
				tempPool.put(actor);
			}
		}
		return actor;
	}

}
