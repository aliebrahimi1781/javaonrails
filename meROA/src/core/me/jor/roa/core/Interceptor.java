package me.jor.roa.core;

import me.jor.roa.core.accessable.AccessableTag;

public interface Interceptor extends AccessableTag{
	public Object intercept(ResourceAccessContext context);
}
