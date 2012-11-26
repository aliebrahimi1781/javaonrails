package me.jor.jetty.continuation;

import org.eclipse.jetty.continuation.Continuation;

public abstract class ContinuationRunnable implements Runnable {
	protected Continuation continuation;
	public void setContinuation(Continuation continuation){
		this.continuation=continuation;
	}
	public Continuation getContinuation(){
		return this.continuation;
	}
}
