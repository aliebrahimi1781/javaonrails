package me.jor.roa.core;

import java.util.HashMap;
import java.util.Map;

import me.jor.common.GlobalObject;
import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.Result;
import me.jor.roa.exception.UnsupportedResourceAccessException;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class ROAAccess {
	private static final Log log=Log4jUtil.getLog(ROAAccess.class);
	private static final Map<String, ResourceAccess> config=new HashMap<String, ResourceAccess>();
	private static final Map<String, Result> resultMap=new HashMap<String, Result>();
	private static AccessDataParser accessDataParser;
	private static Map<String, ApplicationContext> applicationContextMap=new HashMap<String, ApplicationContext>();
	
	
	static void addResourceAccess(String url, ResourceAccess resourceAccess){
		config.put(url, resourceAccess);
	}
	static void addResult(String name, String uri, Result result){
		Result existed=resultMap.get(name);
		if(existed==null){
			resultMap.put(name, new ResultMap(uri, result));
		}else{
			((ResultMap)existed).addResult(uri, result);
		}
	}
	static void addApplicationContext(String path,ApplicationContext context){
		ApplicationContext old=applicationContextMap.get(path);
		if(old!=null){
			((AbstractApplicationContext)old).destroy();
		}
		applicationContextMap.put(path,context);
	}
	
	static Object access(String uri, ResourceAccessContext context, boolean generate){
		try{
			return config.get(uri).access(context,generate);
		}catch(NullPointerException e){
			Exception ex=new UnsupportedResourceAccessException(e);
			log.error("",ex);
			context.setResult(ex);
			if(generate){
				Result result=resultMap.get(context.getErrorType());
				try{
					return result!=null?result.generate(context):GlobalObject.getJsonMapper().writeValueAsString(context);
				}catch(Exception e2){
					return null;
				}
			}else{
				return ex;
			}
		}
	}
	
	static <D> D parseAccessData(String accessData){
		return (D)accessDataParser.parse(accessData);
	}
	
	static AccessMethod getDefaultAccessMethod(String uri){
		return config.get(uri).getDefaultMethod();
	}
	static String getDefaultDataType(String uri, AccessMethod accessMethod, String accessType){
		return config.get(uri).getDefaultDataType(accessMethod, accessType);
	}
	static String getDefaultErrorType(String uri, AccessMethod accessMethod, String accessType){
		return config.get(uri).getDefaultErrorType(accessMethod, accessType);
	}
	static void addResult(String name, Result result){
		resultMap.put(name, result);
	}
	static void optimize(){

	}
	static void end(){
		for(ApplicationContext context:applicationContextMap.values()){
			((AbstractApplicationContext)context).close();
		}
	}
	
}
