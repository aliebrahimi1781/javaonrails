package me.jor.pool;


class TransferActorWithActiveAllocationByteBufferSizeAndReturnNullOnNoIdleSpacePool extends TransferActorWithActiveAllocationByteBufferSizePool{

	@Override
	protected TransferActor createActorOnNoIdleSpace(int size) {
		return null;
	}
}
