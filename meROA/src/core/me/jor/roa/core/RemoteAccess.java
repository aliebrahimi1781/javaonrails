package me.jor.roa.core;

import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.Accessable;
import me.jor.roa.core.accessable.BaseAccess;


public class RemoteAccess implements BaseAccess{
	@Override
	public Object access(ResourceAccessContext context, boolean generate){
		return null;
	}
	void addRemoteConfig(String ip,int port, String uri){
		
	}
	void addRemoteConfig(String ip, int port, String[] uri){
		
	}
	@Override
	public AccessMethod getDefaultMethod() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getDefaultDataType(AccessMethod accessMethod, String accessType) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getDefaultErrorType(AccessMethod accessMethod, String accessType) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object access(ResourceAccessContext context) {
		// TODO Auto-generated method stub
		return null;
	}
}
