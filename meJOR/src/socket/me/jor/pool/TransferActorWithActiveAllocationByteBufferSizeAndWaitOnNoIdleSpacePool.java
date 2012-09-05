package me.jor.pool;

public class TransferActorWithActiveAllocationByteBufferSizeAndWaitOnNoIdleSpacePool extends TransferActorWithActiveAllocationByteBufferSizePool{

	@Override
	protected TransferActor createActorOnNoIdleSpace(int size) {
		synchronized(this){
			try {
				this.wait();
			} catch (InterruptedException e) {}
			return new TransferActor(null);
		}
	}

}
