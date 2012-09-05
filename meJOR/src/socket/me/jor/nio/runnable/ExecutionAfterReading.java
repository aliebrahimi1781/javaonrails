package me.jor.nio.runnable;

import me.jor.common.Task;

public interface ExecutionAfterReading<E> extends Task{
	public ExecutionAfterReading<E> setAccessData(E e);
}
