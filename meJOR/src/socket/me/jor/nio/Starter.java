package me.jor.nio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;

import me.jor.nio.client.SocketChannelGetter;
import me.jor.nio.runnable.ExecutionAfterReading;
import me.jor.nio.runnable.TransferRunnable;
import me.jor.pool.SelectorPool;
import me.jor.pool.TransferActorPool;

public class Starter {
	public static void transfer(byte... data) throws IOException{
		SelectorPool.register(SocketChannelGetter.getChannel(), SelectionKey.OP_WRITE,
				TransferActorPool.singleInstance().getTransferActor(data.length+8).startTransfer(data));
	}
	public static void transfer(Object data) throws ClosedChannelException, IOException{
		SelectorPool.register(SocketChannelGetter.getChannel(), SelectionKey.OP_WRITE,
				TransferActorPool.singleInstance().getTransferActor(1024).startTransfer(data));
	}
	public static void transfer(File data) throws ClosedChannelException, IOException{
		SelectorPool.register(SocketChannelGetter.getChannel(), SelectionKey.OP_WRITE,
				TransferActorPool.singleInstance().getTransferActor(1024).startTransfer(data));
	}
	public static void transfer(InputStream data) throws ClosedChannelException, IOException{
		SelectorPool.register(SocketChannelGetter.getChannel(), SelectionKey.OP_WRITE,
				TransferActorPool.singleInstance().getTransferActor(1024).startTransfer(data));
	}
	public static void transfer(ReadableByteChannel channel) throws ClosedChannelException, IOException{
		SelectorPool.register(SocketChannelGetter.getChannel(), SelectionKey.OP_WRITE,
				TransferActorPool.singleInstance().getTransferActor(1024).startTransfer(channel));
	}
	public static void transfer(WritableByteChannel channel) throws ClosedChannelException, IOException{
		SelectorPool.register(SocketChannelGetter.getChannel(), SelectionKey.OP_READ,
				TransferActorPool.singleInstance().getTransferActor(1024).startTransfer(channel));
	}
	public static void transfer(TransferRunnable runnable) throws IOException{
		runnable.setByteChannel((ByteChannel)SocketChannelGetter.getChannel()).run();
	}
	public static void transfer(ExecutionAfterReading task, Object accessData, Class dstType) throws ClosedChannelException, IOException{
		SelectorPool.register(SocketChannelGetter.getChannel(), SelectionKey.OP_WRITE,
				TransferActorPool.singleInstance().getTransferActor(1024).startTransfer(task, accessData, dstType, false));
	}
	public static void transfer(SelectableChannel channel, ExecutionAfterReading task, Class dstType) throws IOException{
		SelectorPool.register(channel, SelectionKey.OP_READ,
				TransferActorPool.singleInstance().getTransferActor(1024).startTransfer(task, dstType, true));
	}
}
