package me.jor.util.pubsub;

import java.util.UUID;

public abstract class AbstractSubTask implements SubTask{
	private UUID uuid;
	private long timeout;
	private long subTime=System.currentTimeMillis();
	
	public AbstractSubTask(){}
	public AbstractSubTask(UUID uuid, long timeout){
		this.uuid=uuid;
		this.timeout=timeout;
	}
	public AbstractSubTask(UUID uuid){
		this.uuid=uuid;
	}
	public AbstractSubTask(long timeout){
		this.timeout=timeout;
	}
	
	
	
	@Override
	public boolean equals(Object o){
		return uuid.equals(o);
	}
	@Override
	public int hashCode(){
		return uuid.hashCode();
	}
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	public void setUUID(String uuid){
		this.uuid=UUID.fromString(uuid);
	}
	public UUID getUUID() {
		return uuid;
	}
	public long getSubTime() {
		return subTime;
	}
	public void setSubTime(long subTime) {
		this.subTime = subTime;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public boolean isTimeout(){
		return timeout>0 && (System.currentTimeMillis()-subTime)>=timeout;
	}
}
