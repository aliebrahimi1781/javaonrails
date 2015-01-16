package me.jor.mq;

public interface MQ {
	public void getSender(String queue,String messageType,Exchange exchange);
	public void getReceiver(String queue,String messagePattern,Exchange exchange);
}
