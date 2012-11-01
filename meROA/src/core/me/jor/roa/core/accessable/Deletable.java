package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;


public interface Deletable extends ResourceAccessable,Describable{
	public Object delete(ResourceAccessContext context)throws Exception;
}
