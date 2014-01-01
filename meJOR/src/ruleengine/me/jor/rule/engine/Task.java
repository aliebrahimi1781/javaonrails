package me.jor.rule.engine;

public interface Task<P,R> {
	public R execute(P p);
}
