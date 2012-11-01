package me.jor.roa.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.jor.common.GlobalObject;
import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.BaseAccess;
import me.jor.roa.core.accessable.Result;
import me.jor.roa.exception.UnsupportedResourceAccessException;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;

public class ROAAccess {
	private static final Log log=Log4jUtil.getLog(ROAAccess.class);
	private static final Map<String, BaseAccess> config=new ConcurrentHashMap<String, BaseAccess>();
	/**
	 * 全局Result
	 */
	private static final Map<String, Result> resultMap=new HashMap<String, Result>();
	private static AccessDataParser accessDataParser;
	private static final RemoteAccess remoteAccess=new RemoteAccess();
	
	
	static void addResourceAccess(String url, BaseAccess resourceAccess){
		config.put(url, resourceAccess);
	}
	static void addResult(String name, String uri, Result result){
		Result existed=resultMap.get(name);
		if(existed==null){
			resultMap.put(name, new ResultMap(uri, result));
		}else if(existed instanceof ResultMap){
			((ResultMap)existed).addResult(uri, result);
		}else{
			ResultMap map=new ResultMap("",existed);
			map.addResult(uri, result);
			resultMap.put(name, map);
		}
	}
	static void addResult(String name, Result result){
		resultMap.put(name, result);
	}
	static Result getResult(String name){
		return resultMap.get(name);
	}
	
	static Object access(ResourceAccessContext context) throws Exception{
		try{
			return getBaseAccess(context.getUri()).access(context);
		}catch(NullPointerException e){
			Exception ex=new UnsupportedResourceAccessException(e);
			log.error("",ex);
			context.setResult(ex);
			if(context.isGenerateResult()){
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
	
	private static BaseAccess getBaseAccess(String uri) throws InterruptedException, IOException{
		BaseAccess res=config.get(uri);
		if(res==null){
			ROAConfigParser.parser.parse(uri.substring(0,uri.lastIndexOf('/'))/*此处为取得模块名*/);//此方法的实现已做了同步处理不必再同步
			res=config.get(uri);
			if(res==null){
				synchronized(config){
					res=config.get(uri);
					if(res==null){
						res=remoteAccess;
						config.put(uri, remoteAccess);
					}
				}
			}
		}
		return res;
	}
	
	static <D> D parseAccessData(String accessData){
		return (D)accessDataParser.parse(accessData);
	}
	
	static AccessMethod getDefaultAccessMethod(String uri) throws InterruptedException, IOException{
		return getBaseAccess(uri).getDefaultMethod();
	}
	static String getDefaultDataType(String uri, AccessMethod accessMethod, String accessType) throws InterruptedException, IOException{
		return getBaseAccess(uri).getDefaultDataType();
	}
	static String getDefaultErrorType(String uri, AccessMethod accessMethod, String accessType) throws InterruptedException, IOException{
		return getBaseAccess(uri).getDefaultErrorType();
	}
	
    void addRemoteConfig(String ip,int port, String uri){
		remoteAccess.addRemoteConfig(ip, port, uri);
	}
	void addRemoteConfig(String ip, int port, String[] uri){
		remoteAccess.addRemoteConfig(ip,port,uri);
	}
	static void end(){
		for(BaseAccess access:config.values()){
			if(access instanceof ResourceAccess){
				try{
					ApplicationContext context=(ApplicationContext)((ResourceAccess) access).getApplicationContext();
					context.getClass().getMethod("close").invoke(context);
				}catch(Exception e){}
			}
		}
	}
	
}
