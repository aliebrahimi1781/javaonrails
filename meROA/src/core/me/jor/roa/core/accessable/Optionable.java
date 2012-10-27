package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;

public interface Optionable extends AccessableTag,Describable{
	public Object options(ResourceAccessContext context);
}
