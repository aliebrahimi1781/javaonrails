package me.jor.roa.util;

import me.jor.roa.core.AccessData;
import me.jor.roa.core.ResourceAccessHandler;
import me.jor.roa.core.accessable.AccessMethod;

public class ResourceAccessUtil {
	public static Object access(String uri, AccessData data, AccessMethod method, boolean generateResult) throws Exception{
		int midx=uri.lastIndexOf('!');
		if(method!=null && data.getMethod()==null){
			data.setMethod(method);
		}else if(midx>0){
			data.setMethod(AccessMethod.valueOf(uri.substring(midx+1,midx+2)));
			uri=uri.substring(0,midx);
		}
		return ResourceAccessHandler.handle(uri, data, generateResult);
	}
}
