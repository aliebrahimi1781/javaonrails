package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;

public interface Accessable extends AccessableTag{
	public Object access(ResourceAccessContext context)throws Exception;
}
