package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;


public interface Updatable extends ResourceAccessable,Describable{
	public Object update(ResourceAccessContext context)throws Exception;
}
