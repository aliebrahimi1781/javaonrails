package me.jor.pool;

import java.nio.ByteBuffer;

public class TransferActorWithActiveAllocationByteBufferSizeAndReturnTempOnNoIdleSpacePool extends TransferActorWithActiveAllocationByteBufferSizePool{

	@Override
	protected TransferActor createActorOnNoIdleSpace(int size) {
		return new TransferActor(ByteBuffer.allocate(size));
	}

}
