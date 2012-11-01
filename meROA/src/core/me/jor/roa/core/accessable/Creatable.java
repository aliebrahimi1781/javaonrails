package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;


public interface Creatable extends ResourceAccessable,Describable{
	public Object create(ResourceAccessContext context)throws Exception;
}
