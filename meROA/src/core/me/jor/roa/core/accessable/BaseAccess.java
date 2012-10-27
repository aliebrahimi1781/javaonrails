package me.jor.roa.core.accessable;

import me.jor.roa.core.ResourceAccessContext;

public interface BaseAccess extends Accessable{
	public Object access(ResourceAccessContext context, boolean generate)throws Exception;
	public AccessMethod getDefaultMethod();
	public String getDefaultDataType(AccessMethod accessMethod, String accessType);
	public String getDefaultErrorType(AccessMethod accessMethod, String accessType);
}
