package me.jor.nio.runnable;

import java.nio.channels.ByteChannel;

public interface TransferRunnable extends Runnable{
	public TransferRunnable setByteChannel(ByteChannel channel);
}
