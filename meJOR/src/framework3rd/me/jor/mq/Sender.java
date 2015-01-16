package me.jor.mq;

public interface Sender {
	public <M> void ayncSend(M message);
	public <M,A> A syncSend(M message);
	public <M> void anycSend(M message,Runnable runnable);
}
