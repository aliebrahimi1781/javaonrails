package me.jor.roa.core;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import me.jor.roa.common.constant.ROAConstant;
import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.AccessPurpose;
import me.jor.roa.core.accessable.Accessable;
import me.jor.roa.core.accessable.BaseAccess;
import me.jor.roa.core.accessable.Result;
import me.jor.util.Help;
import me.jor.util.RegexUtil;

/**
 * 每次资源访问都会创建一个新对象，此类的对象包含一次资源访问的所有信息
 *
 */
public class ResourceAccessContext{
	private static final Pattern BIAS=Pattern.compile("\\\\",Pattern.MULTILINE);
	
	private String uri;
	private Object accessData;
	private Object result;
	private InterceptorStack interceptor;
	private BaseAccess baseAccess;
	
	private String dataType;
	private String errorType;
	private AccessMethod accessMethod;
	private AccessStatus currentStatus=AccessStatus.START;
	private boolean generateResult;
	
	private ResourceAccessContext(String uri, Object accessData, boolean generateResult){
		this.uri=uri;
		this.accessData=accessData;
		this.generateResult=generateResult;
	}
	
	public static ResourceAccessContext newInstance(String uri, Object accessData, boolean generateResult){
		return new ResourceAccessContext(uri, accessData, generateResult);
	}
	
	/**
	 * 被ResourceAccessHandler对象调用，作为资源访问的开始。
	 * 也会被资源对象调用，从其它资源中获取相关信息，调用方式为ResourceAccessContext.newInstance(uri,accessData).access();
	 * 它是各资源之间交互的枢纽，以及每次资源访问时控制访问过程的中枢
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public final Object access() throws Exception {
		return this.currentStatus.access(this);
	}

	void setBaseAccess(BaseAccess baseAccess) {
		this.baseAccess=baseAccess;
	}
	
	public String getUri(){
		return uri;
	}
	public <R> R getResult(){
		return (R)result;
	}
	public <R> void setResult(R result){
		this.result=result;
	}

	<D> D getAccessData(){
		if(accessData!=null && accessData instanceof String){
			String data=accessData.toString();
			if(data.startsWith("\"")){
				accessData=BIAS.matcher(data.substring(1,data.length()-1)).replaceAll("\\");
			}else if(data.equals("true") || data.equals("false")){
				accessData=new Boolean(Boolean.parseBoolean(data));
			}else if(RegexUtil.isDigit(data)){
				if(data.indexOf(".")>=0){
					accessData=new BigDecimal(data);
				}else{
					BigInteger bi=new BigInteger(data);
					if(bi.compareTo(ROAConstant.MIN_INT)>=0 && bi.compareTo(ROAConstant.MAX_INT)<=0){
						accessData=bi.intValue();
					}else if(bi.compareTo(ROAConstant.MIN_LONG)>=0 && bi.compareTo(ROAConstant.MAX_LONG)>=0){
						accessData=bi.longValue();
					}else{
						accessData=bi;
					}
				}
			}else{
				accessData=ROAAccess.parseAccessData(data);
			}
		}
		return (D)accessData;
	}
	
	/*
	 *  * data;//实际的请求参数，可以是数字、字符串、布尔值、map、list、数组，或其它任意的对象
 * method;//C R U D
 * accessType;//同一个AccessMethod需要不同的访问逻辑时
 * dataType;//返回的数据类型
 * purpose;//访问目的：用于页面的显示、运行、渲染或保存到本地
	 */
	public <D> D getAccessParam(){
		getAccessData();
		if(accessData instanceof AccessData){
			return (D)((AccessData)accessData).getData();
		}else{
			return (D)accessData;
		}
	}
	public AccessMethod getAccessMethod(){
		if(accessMethod==null){
			getAccessData();
			if(accessData instanceof AccessData){
				accessMethod=(AccessMethod)((AccessData)accessData).getMethod();
			}else if(baseAccess!=null){
				accessMethod=baseAccess.getDefaultMethod();
			}
		}
		return accessMethod;
	}
	public String getAccessType(){
		getAccessData();
		if(accessData instanceof AccessData){
			return ((AccessData)accessData).getAccessType();
		}else{
			return null;
		}
	}
	public String getDataType(){
		if(Help.isEmpty(dataType)){
			getAccessData();
			if(accessData instanceof AccessData){
				dataType=((AccessData)accessData).getDataType();
			}else{
				String dt=ROAConstant.getDefaultDataType();
				dataType=Help.isEmpty(dt)?baseAccess.getDefaultDataType():dt;
			}
		}
		return dataType;
	}
	public String getResultType(boolean data){
		return data?getDataType():getErrorType();
	}
	public String getErrorType(){
		if(Help.isEmpty(errorType)){
			getAccessData();
			if(accessData instanceof AccessData){
				errorType=((AccessData)accessData).getErrorType();
			}else{
				String et=ROAConstant.getDefaultErrorType();
				errorType=Help.isEmpty(et)?baseAccess.getDefaultErrorType():et;
			}
		}
		return errorType;
	}
	
	public AccessPurpose getAccessPurpose(){
		getAccessData();
		if(accessData instanceof AccessData){
			return (AccessPurpose)((AccessData)accessData).getPurpose();
		}else{
			return AccessPurpose.PAGE;
		}
	}
	
	public static String getRealPath(String path){
		File file=getRealPathFile(path);
		if(file==null){
			return null;
		}else{
			return file.getAbsolutePath();
		}
	}
	public static File getRealPathFile(String path){
		try{
			File file=new File(new File(ResourceAccessContext.class.getResource("/").toURI()).getParent(),path);
			if(file.exists()){
				return file;
			}else{
				return null;
			}
		}catch(URISyntaxException e){
			return null;
		}
	}
	
	public Result getResult(String dataType) {
		return baseAccess.getResult(dataType);
	}
	void setInterceptor(Interceptor interceptor){
		if(interceptor instanceof InterceptorStack){
			this.interceptor=(InterceptorStack)interceptor;
		}else{
			this.interceptor=new InterceptorStack(new Interceptor[]{interceptor});
		}
	}
	
	public boolean isGenerateResult() {
		return generateResult;
	}


	private enum AccessStatus implements Accessable{
		START{
			@Override
			public Object access(ResourceAccessContext context) throws Exception{
				super.next(context,INTERCEPTOR);
				return ROAAccess.access(context);
			}
		},INTERCEPTOR{
			@Override
			public Object access(ResourceAccessContext context) throws Exception{
				return super.accessInterceptor(context,RESOURCE);
			}
		},RESOURCE{
			@Override
			public Object access(ResourceAccessContext context) throws Exception{
				super.next(context,null);
				return context.baseAccess.access(context);
			}
		};
		public abstract Object access(ResourceAccessContext context) throws Exception;
		private void next(ResourceAccessContext context, AccessStatus status){
			context.currentStatus=status;
		}
		private Object accessInterceptor(ResourceAccessContext context, AccessStatus status) throws Exception{
			InterceptorStack interceptor=context.interceptor;
			if(!interceptor.hasNext()){
				this.next(context, status);
			}
			return interceptor!=null?interceptor.access(context):null;
		}
	}
}
