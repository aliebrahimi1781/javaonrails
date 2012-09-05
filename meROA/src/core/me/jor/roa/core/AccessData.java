package me.jor.roa.core;

import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.AccessPurpose;

/**
 * 访问参数信息
 * data;//实际的请求参数，可以是数字、字符串、布尔值、map、list、数组，或其它任意的对象
 * method;//C R U D
 * accessType;//同一个AccessMethod需要不同的访问逻辑时
 * dataType;//返回的数据类型
 * errorType;//发生错误时返回的数据类型
 * purpose;//访问目的：用于页面的显示、运行、渲染或保存到本地
 */
public class AccessData {
	private Object data;//实际的请求参数，可以是数字、字符串、布尔值、map、list、数组
	private AccessMethod method;//C R U D
	private String accessType;//同一个AccessMethod需要不同的访问逻辑时
	private String dataType;//返回的数据类型
	private String errorType;//发生错误时返回的数据类型
	private AccessPurpose purpose;//访问目的：用于页面的显示、运行、渲染或保存到本地
	
	private String logtoken;//登录标记，uuid；不是必须的
	
	public AccessData() {}

	public AccessData(Object data, AccessMethod method, String accessType,
			String dataType, String errorType, AccessPurpose purpose) {
		this.data = data;
		this.method = method;
		this.accessType = accessType;
		this.dataType = dataType;
		this.errorType=errorType;
		this.purpose = purpose;
	}
	public AccessData(Object data, AccessMethod method, String accessType,
			String dataType, String errorType, AccessPurpose purpose, String logtoken) {
		this(data,method,accessType,dataType,errorType,purpose);
		this.logtoken=logtoken;
	}
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public AccessMethod getMethod() {
		return method;
	}
	public void setMethod(AccessMethod method) {
		this.method = method;
	}

	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public AccessPurpose getPurpose() {
		return purpose;
	}
	public void setPurpose(AccessPurpose purpose) {
		this.purpose = purpose;
	}

	public String getLogtoken() {
		return logtoken;
	}
	public void setLogtoken(String logtoken) {
		this.logtoken = logtoken;
	}

	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
//	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
//		AccessData bad=new AccessData();
//		bad.setMethod(AccessMethod.C);
//		String json=GlobalObject.getJsonMapper().writeValueAsString(bad);
//		System.out.println(((AccessData)GlobalObject.getJsonMapper().readValue(json,AccessData.class)).method);
//	}
}
