package me.jor.jetty.continuation;

import java.util.concurrent.Callable;

import org.eclipse.jetty.continuation.Continuation;

public abstract class ContinuationCallable implements Callable {
	protected Continuation continuation;

	/**
	 * @return the continuation
	 */
	public Continuation getContinuation() {
		return continuation;
	}

	/**
	 * @param continuation the continuation to set
	 */
	public void setContinuation(Continuation continuation) {
		this.continuation = continuation;
	}
	
}
