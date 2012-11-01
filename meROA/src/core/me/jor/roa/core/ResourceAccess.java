package me.jor.roa.core;

import java.util.HashMap;
import java.util.Map;

import me.jor.roa.common.constant.ROAConstant;
import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.BaseAccess;
import me.jor.roa.core.accessable.Creatable;
import me.jor.roa.core.accessable.Deletable;
import me.jor.roa.core.accessable.Describable;
import me.jor.roa.core.accessable.ResourceAccessable;
import me.jor.roa.core.accessable.Result;
import me.jor.roa.core.accessable.Retrivable;
import me.jor.roa.core.accessable.Updatable;
import me.jor.util.Help;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;


/**
 * 对应<resource>配置
 * @author wujingrun
 *
 */
public class ResourceAccess implements BaseAccess,Describable{
	private static final Log log=Log4jUtil.getLog(ResourceAccess.class);
	
	private String uri;
	private AccessMethod defaultMethod;//=AccessMethod.R;
	private String defaultDataType;
	private String defaultErrorType;
	private Interceptor interceptor;
	
	private Map<String, Result> resultMap;
	
	private String defaultRetrivableType;
	
	private String defaultCreatableType;
	
	private String defaultUpdatableType;
	
	private String defaultDeletableType;
	
	private String resourceDescription;
	
	private String resourceAccessableBeanName;
	
	private ApplicationContext applicationContext;
	
	private AccessStatus accessStatus=AccessStatus.DETERMIN_RESULT;
	
	
	public ResourceAccess(){}
	
	@Override
	public Object access(ResourceAccessContext context) throws Exception{
		return accessStatus.access(context,this);
	}
	private Object generateResult(ResourceAccessContext context,boolean data) throws Exception{
		String dataType=context.getResultType(data);
		return ((Result) Help.convert(context.getResult(dataType), resultMap.get(dataType), ROAAccess.getResult(dataType))).generate(context);
	}
	private Object determinResult(ResourceAccessContext context) throws Exception{
		boolean data=(Boolean)context.access();//must be invoked here
		if(context.isGenerateResult()){
			return generateResult(context,data);
		}else{
			return context.getResult();
		}
	}
	public AccessMethod getDefaultMethod() {
		return defaultMethod;
	}
	public void setDefaultMethod(AccessMethod defaultMethod) {
		this.defaultMethod = defaultMethod;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	public String getDefaultDataType(){
		if(Help.isEmpty(defaultDataType)){
			synchronized(this){
				if(Help.isEmpty(defaultDataType)){
					defaultDataType=ROAConstant.getDefaultDataType();
				}
			}
		}
		return defaultDataType;
	}
	
	public void setDefaultDataType(String defaultDataType) {
		this.defaultDataType = defaultDataType;
	}
	public void setInterceptor(Interceptor interceptor){
		this.interceptor=interceptor;
	}
	@Override
	public String getDefaultErrorType(){
		if(Help.isEmpty(defaultErrorType)){
			synchronized(this){
				if(Help.isEmpty(defaultErrorType)){
					defaultErrorType=ROAConstant.getDefaultErrorType();
				}
			}
		}
		return defaultErrorType;
	}

	public void setDefaultErrorType(String defaultErrorType) {
		this.defaultErrorType = defaultErrorType;
	}

	public void setResourceDescription(String resourceDescription) {
		this.resourceDescription = resourceDescription;
	}
	
	public Interceptor getInterceptor() {
		return interceptor;
	}

	public Map<String, Result> getResultMap() {
		return resultMap;
	}
	
	@Override
	public Result getResult(String name){
		return resultMap.get(name);
	}


	public String getDefaultRetrivableType() {
		return defaultRetrivableType;
	}

	public void setDefaultRetrivableType(String defaultRetrivableType) {
		this.defaultRetrivableType = defaultRetrivableType;
	}

	public String getDefaultCreatableType() {
		return defaultCreatableType;
	}

	public void setDefaultCreatableType(String defaultCreatableType) {
		this.defaultCreatableType = defaultCreatableType;
	}

	public String getDefaultUpdatableType() {
		return defaultUpdatableType;
	}

	public void setDefaultUpdatableType(String defaultUpdatableType) {
		this.defaultUpdatableType = defaultUpdatableType;
	}

	public String getDefaultDeletableType() {
		return defaultDeletableType;
	}

	public void setDefaultDeletableType(String defaultDeletableType) {
		this.defaultDeletableType = defaultDeletableType;
	}


	void addResult(String name, Result result){
		if(resultMap==null){
			resultMap=new HashMap<String, Result>();
		}
		resultMap.put(name, result);
	}

	public void setResourceAccessableBeanName(String resourceAccessableBeanName) {
		this.resourceAccessableBeanName = resourceAccessableBeanName;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public Object getDescription() {
		return this.resourceDescription;
	}
	
	private enum AccessStatus{
		DETERMIN_RESULT {
			public Object access(ResourceAccessContext context, ResourceAccess resourceAccess)throws Exception {
				resourceAccess.accessStatus=ACCESS_METHOD;
				context.setBaseAccess(resourceAccess);
				context.setInterceptor(resourceAccess.interceptor);
				return resourceAccess.determinResult(context);
			}
		},ACCESS_METHOD {
			public Object access(ResourceAccessContext context, ResourceAccess resourceAccess)throws Exception {
				resourceAccess.accessStatus=null;
				ResourceAccessable rable=(ResourceAccessable)Help.merge(resourceAccess.applicationContext.getBean(resourceAccess.resourceAccessableBeanName),context.getAccessData());
				AccessMethod am=context.getAccessMethod();
				boolean data=true;
				try{
					switch(am){
					case C:
						if(rable instanceof Creatable){
							context.setResult(((Creatable)rable).create(context));
						}
						break;
					case R:
						if(rable instanceof Retrivable){
							context.setResult(((Retrivable)rable).retrive(context));
						}
						break;
					case U:
						if(rable instanceof Updatable){
							context.setResult(((Updatable)rable).update(context));
						}
						break;
					case D:
						if(rable instanceof Deletable){
							context.setResult(((Deletable)rable).delete(context));
						}
						break;
					case O:
						context.setResult(this);
						break;
					}
				}catch(Exception e){
					context.setResult(e);
					data=false;
					log.error("",e);
				}
				return data;
			}
		};
		public abstract Object access(ResourceAccessContext context, ResourceAccess resourceAccess)throws Exception;
	}
}
