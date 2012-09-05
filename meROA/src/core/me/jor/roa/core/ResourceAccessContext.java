package me.jor.roa.core;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import me.jor.roa.common.constant.ROAConstant;
import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.AccessPurpose;
import me.jor.util.Help;
import me.jor.util.RegexUtil;

/**
 * 每次资源访问都会创建一个新对象，此类的对象包含一次资源访问的所有信息
 *
 */
public class ResourceAccessContext{
	private static final Pattern QUOTA=Pattern.compile("\\\"",Pattern.MULTILINE);
	private static final Pattern BIAS=Pattern.compile("\\\\",Pattern.MULTILINE);
	
	private String uri;
	private Object accessData;
	private Object result;
	private ResourceAccess resourceAccess;
	private CRUDAccess crudAccess;
	
	private String dataType;
	private String errorType;
	private AccessMethod accessMethod;
	
	private ResourceAccessContext(String uri, Object accessData){
		this.uri=uri;
		this.accessData=accessData;
	}
	
	public static ResourceAccessContext newInstance(String uri, Object accessData){
		return new ResourceAccessContext(uri, accessData);
	}
	
	/**
	 * 被ResourceAccessHandler对象调用，作为资源访问的开始。
	 * 也会被资源对象调用，从其它资源中获取相关信息
	 */
	public final Object access() {
		return ROAAccess.access(uri, this, false);
	}

	void setResourceAccess(ResourceAccess resourceAccess) {
		this.resourceAccess=resourceAccess;
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

	private <D> D getAccessData(){
		if(accessData!=null && accessData instanceof String){
			String data=accessData.toString();
			if(data.startsWith("\"")){
				String accessStr=data.substring(1,data.length()-1);
				QUOTA.matcher(accessStr).replaceAll("\"");
				BIAS.matcher(accessStr).replaceAll("\\");
				accessData=(D)accessStr;
			}else if(data.equals("true") || data.equals("false")){
				accessData=(D)new Boolean(Boolean.parseBoolean(data));
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
			}else{
				accessMethod=resourceAccess.getDefaultMethod();
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
				dataType=Help.isEmpty(dt)?crudAccess.getDefaultDataType():dt;
			}
		}
		return dataType;
	}
	public String getErrorType(){
		if(Help.isEmpty(errorType)){
			getAccessData();
			if(accessData instanceof AccessData){
				errorType=((AccessData)accessData).getErrorType();
			}else{
				String et=ROAConstant.getDefaultErrorType();
				errorType=Help.isEmpty(et)?crudAccess.getDefaultErrorType():et;
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

	void setCRUDAccess(CRUDAccess crudAccess) {
		this.crudAccess=crudAccess;
	}


	Object accessCRUD() {
		return crudAccess.accessTag(this);
	}
}
