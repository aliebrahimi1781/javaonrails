package me.jor.pool;

import java.nio.ByteBuffer;

import me.jor.nio.socket.constant.ActionOnPoolNoIdleSpace;
import me.jor.nio.socket.constant.NIOSocketConstant;

public abstract class TransferActorPool{
	protected static final ByteBuffer GLOBAL_BUFFER;
	private static final TransferActorPool POOL;
	static{
		/*
		 * private static long BYTE_BUFFER_POOL_SIZE;
	private static boolean DIRECT_BYTE_BUFFER;
	private static long FIXED_BYTE_BUFFER_SIZE;
		 */
		GLOBAL_BUFFER=createGlobalByteBuffer();
		POOL=createPool();
	}
	private static ByteBuffer createGlobalByteBuffer(){
		int size=NIOSocketConstant.getBYTE_BUFFER_POOL_SIZE();
		if(NIOSocketConstant.getDIRECT_BYTE_BUFFER()){
			return ByteBuffer.allocateDirect(size);
		}else{
			return ByteBuffer.allocate(size);
		}
	}
	private static TransferActorPool createPool(){
		int bufferSize=NIOSocketConstant.getFIXED_BYTE_BUFFER_SIZE();
		if(bufferSize>0){
			return createFixedByteBufferSizePool(bufferSize,NIOSocketConstant.getACTION_ON_POOL_EMPTY());
		}else{
			return createActiveAllocatingByteBufferSizePool(NIOSocketConstant.getACTION_ON_POOL_EMPTY());
		}
	}
	
	private static TransferActorPool createActiveAllocatingByteBufferSizePool(ActionOnPoolNoIdleSpace action) {
		switch(action){
		case WAIT:
			return new TransferActorWithActiveAllocationByteBufferSizeAndWaitOnNoIdleSpacePool();
		case RETURN_NULL:
			return new TransferActorWithActiveAllocationByteBufferSizeAndReturnNullOnNoIdleSpacePool();
		case RETURN_TEMP:
			return new TransferActorWithActiveAllocationByteBufferSizeAndReturnTempOnNoIdleSpacePool();
		default:
			return null;//永远也不会执行
		}
	}
	private static TransferActorPool createFixedByteBufferSizePool(int bufferSize, ActionOnPoolNoIdleSpace action) {
		switch(action){
		case WAIT:
			return new TransferActorWithFixedByteBufferSizeAndWaitOnNoIdleSpacePool(bufferSize);
		case RETURN_NULL:
			return new TransferActorWithFixedByteBufferSizeAndReturnNullOnNoIdleSpacePool(bufferSize);
		case RETURN_TEMP:
			return new TransferActorWithFixedByteBufferSizeAndReturnTempOnNoIdleSpacePool(bufferSize);
		default:
			return null;//永远也不会执行
		}
	}
	public static TransferActorPool singleInstance(){
		return POOL;
	}

	/**
	 * 参数在池创建的ByteBuffer大小固定时会被忽略。<br/>
	 * 向Socket写文件数据，或向socket写来自其它socket的字节时可用此方式
	 */
	public abstract TransferActor getTransferActor(int size);

	/**
	 * 对于每个从此池的实例获取的ByteBuffe的对象，应在每次用完时执行这个方法，否则将无法再从池内获取新的ByteBuffer。
	 * 如果ByteBuffer对象来自池，此对象会被回收；如果参数是池对象创建的临时ByteBuffer对象或是由其它任意方式创建的，
	 * 则此方法应当什么也不做
	 */
	public abstract void recycle(TransferActor actor);
}
