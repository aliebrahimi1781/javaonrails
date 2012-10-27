package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;


public interface Result extends Describable{
	public Object generate(ResourceAccessContext context);
}
