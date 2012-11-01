package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;


public interface Retrivable extends ResourceAccessable,Describable{
	public Object retrive(ResourceAccessContext context)throws Exception;
}
