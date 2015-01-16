package me.jor.mq;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Receiver {
	public String receive();
	public <M> M receiveJson(Class<M> messageClass);
	public <M,R> Future<R> asyncReceive(Callable<M> callback);
}
