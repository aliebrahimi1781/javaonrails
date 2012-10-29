package me.jor.roa.core;

import java.util.HashMap;
import java.util.Map;

import me.jor.roa.common.constant.ROAConstant;
import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.core.accessable.BaseAccess;
import me.jor.roa.core.accessable.Creatable;
import me.jor.roa.core.accessable.Deletable;
import me.jor.roa.core.accessable.Describable;
import me.jor.roa.core.accessable.Result;
import me.jor.roa.core.accessable.Retrivable;
import me.jor.roa.core.accessable.Updatable;
import me.jor.roa.exception.UnsupportedResourceAccessTypeException;
import me.jor.util.Help;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;


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
	
	private Map<String, CRUDAccess<Retrivable>> retrivableMap;
	private CRUDAccess<Retrivable> defaultRetrivable;
	private String defaultRetrivableType;
	
	private Map<String, CRUDAccess<Creatable>> creatableMap;
	private CRUDAccess<Creatable> defaultCreatable;
	private String defaultCreatableType;
	
	private Map<String, CRUDAccess<Updatable>> updatableMap;
	private CRUDAccess<Updatable> defaultUpdatable;
	private String defaultUpdatableType;
	
	private Map<String, CRUDAccess<Deletable>> deletableMap;
	private CRUDAccess<Deletable> defaultDeletable;
	private String defaultDeletableType;
	
	private String resourceDescription;
	
	
	public ResourceAccess(){}
	
	@Override
	public Object access(ResourceAccessContext context){
		context.setBaseAccess(this);
		String accessType=context.getAccessType();
		AccessMethod am=context.getAccessMethod();
		CRUDAccess crudAccess=null;
		String dataType=null;
		boolean data=true;
		try{
			switch(am){
			case C:
				crudAccess=Help.isEmpty(accessType)?this.defaultCreatable:creatableMap.get(accessType);
				break;
			case R:
				crudAccess=Help.isEmpty(accessType)?this.defaultRetrivable:retrivableMap.get(accessType);
				break;
			case U:
				crudAccess=Help.isEmpty(accessType)?this.defaultUpdatable:updatableMap.get(accessType);
				break;
			case D:
				crudAccess=Help.isEmpty(accessType)?this.defaultDeletable:deletableMap.get(accessType);
			default:
				context.setResult(this);
			}
			if(crudAccess!=null){
				context.setCRUDAccess(crudAccess);
			}else{
				context.setCRUDAccess(new CRUDAccess());
			}
			crudAccess.access(context);
		}catch(NullPointerException e){
			Exception ex= new UnsupportedResourceAccessTypeException(am, accessType, e);
			context.setResult(ex);
			data=false;
			log.error("",e);
		}catch(Exception e){
			context.setResult(e);
			data=false;
			log.error("",e);
		}
		return data;
	}
	
	@Override
	public Object access(ResourceAccessContext context, boolean generate) throws Exception{
		context.setInterceptor(interceptor);
		return determinResult(context,(Boolean)context.access(),generate);
	}
	private Object generateResult(ResourceAccessContext context,boolean data){
		String dataType=context.getResultType(data);
		return ((Result) Help.convert(context.getResult(dataType), resultMap.get(dataType), ROAAccess.getResult(dataType))).generate(context);
	}
	private Object determinResult(ResourceAccessContext context,boolean data,boolean generate){
		if(generate){
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
	
	private AccessMethod convert(AccessMethod accessMethod){
		return (AccessMethod)Help.convert(accessMethod, this.defaultMethod);
	}
	
	public String getDefaultDataType(AccessMethod accessMethod, String accessType) {
		accessMethod=convert(accessMethod);
		try{
			switch(accessMethod){
			case C:
				return getCRUDAccessDefaultDataType((Help.isNotEmpty(accessType)?creatableMap.get(accessType):defaultCreatable));
			case R:
				return getCRUDAccessDefaultDataType((Help.isNotEmpty(accessType)?retrivableMap.get(accessType):defaultRetrivable));
			case U:
				return getCRUDAccessDefaultDataType((Help.isNotEmpty(accessType)?updatableMap.get(accessType):defaultUpdatable));
			case D:
				return getCRUDAccessDefaultDataType((Help.isNotEmpty(accessType)?deletableMap.get(accessType):defaultDeletable));
			}
		}catch(NullPointerException e){
			throw new UnsupportedResourceAccessTypeException(accessMethod, accessType, e);
		}
		return getDefaultDataType();
	}
	private String getCRUDAccessDefaultDataType(CRUDAccess access){
		String dt=access.getDefaultDataType();
		if(Help.isEmpty(dt)){
			synchronized(dt){
				if(Help.isEmpty(dt)){
					dt=getDefaultDataType();
					access.setDefaultDataType(dt);
				}
			}
		}
		return dt;
	}
	private String getDefaultDataType(){
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
	public String getDefaultErrorType(AccessMethod accessMethod, String accessType) {
		accessMethod=convert(accessMethod);
		try{
			switch(accessMethod){
			case C:
				return getCRUDAccessDefaultErrorType((Help.isNotEmpty(accessType)?creatableMap.get(accessType):defaultCreatable));
			case R:
				return getCRUDAccessDefaultErrorType((Help.isNotEmpty(accessType)?retrivableMap.get(accessType):defaultRetrivable));
			case U:
				return getCRUDAccessDefaultErrorType((Help.isNotEmpty(accessType)?updatableMap.get(accessType):defaultUpdatable));
			case D:
				return getCRUDAccessDefaultErrorType((Help.isNotEmpty(accessType)?deletableMap.get(accessType):defaultDeletable));
			}
		}catch(NullPointerException e){
			throw new UnsupportedResourceAccessTypeException(accessMethod, accessType, e);
		}
		return getDefaultErrorType();
	}
	private String getCRUDAccessDefaultErrorType(CRUDAccess access){
		String et=access.getDefaultErrorType();
		if(Help.isEmpty(et)){
			synchronized(et){
				if(Help.isEmpty(et)){
					et=getDefaultErrorType();
					access.setDefaultErrorType(et);
				}
			}
		}
		return et;
	}
	private String getDefaultErrorType(){
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

	public Map<String, CRUDAccess<Retrivable>> getRetrivableMap() {
		return retrivableMap;
	}

	public CRUDAccess<Retrivable> getDefaultRetrivable() {
		return defaultRetrivable;
	}

	public Map<String, CRUDAccess<Creatable>> getCreatableMap() {
		return creatableMap;
	}

	public CRUDAccess<Creatable> getDefaultCreatable() {
		return defaultCreatable;
	}

	public Map<String, CRUDAccess<Updatable>> getUpdatableMap() {
		return updatableMap;
	}

	public CRUDAccess<Updatable> getDefaultUpdatable() {
		return defaultUpdatable;
	}

	public Map<String, CRUDAccess<Deletable>> getDeletableMap() {
		return deletableMap;
	}

	public CRUDAccess<Deletable> getDefaultDeletable() {
		return defaultDeletable;
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

	
	void addRetrivable(String type, CRUDAccess<Retrivable> retrivable, boolean isDefault){
		if(retrivableMap==null){
			retrivableMap=new HashMap<String, CRUDAccess<Retrivable>>();
		}
		retrivableMap.put(type, retrivable);
		if(isDefault){
			defaultRetrivable=retrivable;
			defaultRetrivableType=type;
		}
	}
	void addCreatable(String type, CRUDAccess<Creatable> creatable, boolean isDefault){
		if(creatableMap==null){
			creatableMap=new HashMap<String, CRUDAccess<Creatable>>();
		}
		creatableMap.put(type, creatable);
		if(isDefault){
			defaultCreatable=creatable;
			defaultCreatableType=type;
		}
	}
	void addUpdatable(String type, CRUDAccess<Updatable> updatable, boolean isDefault){
		if(updatableMap==null){
			updatableMap=new HashMap<String, CRUDAccess<Updatable>>();
		}
		updatableMap.put(type, updatable);
		if(isDefault){
			defaultUpdatable=updatable;
			defaultUpdatableType=type;
		}
	}
	void addDeleble(String type, CRUDAccess<Deletable> deletable, boolean isDefault){
		if(deletableMap==null){
			deletableMap=new HashMap<String, CRUDAccess<Deletable>>();
		}
		deletableMap.put(type, deletable);
		if(isDefault){
			defaultDeletable=deletable;
			defaultDeletableType=type;
		}
	}
	
	void addResult(String name, Result result){
		if(resultMap==null){
			resultMap=new HashMap<String, Result>();
		}
		resultMap.put(name, result);
	}

	@Override
	public Object getDescription() {
		return this.resourceDescription;
	}

}
