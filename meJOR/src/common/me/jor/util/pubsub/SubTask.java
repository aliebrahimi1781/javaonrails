package me.jor.util.pubsub;

public interface SubTask {
	public void execute(Object data);
	public void complete();
}
