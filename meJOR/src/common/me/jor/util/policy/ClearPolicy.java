package me.jor.util.policy;

/**
 * 清除池或缓存中的空引用策略
 */
public interface ClearPolicy<E> {
	public void clear(E e);
}
