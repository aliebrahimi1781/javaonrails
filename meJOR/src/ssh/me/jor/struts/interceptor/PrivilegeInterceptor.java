package me.jor.struts.interceptor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.jor.common.CommonConstant;
import me.jor.service.PrivilegeService;
import me.jor.struts.action.AbstractBaseAction;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class PrivilegeInterceptor extends AbstractInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3903198385131130331L;

	private PrivilegeService privilegeService;
	
	@SuppressWarnings("unchecked")
	@Override
	public String intercept(ActionInvocation ai) throws Exception {
		String useridStr=ServletActionContext.getRequest().getHeader(CommonConstant.USER_ID_HEADER);
		ActionMapping am=ServletActionContext.getActionMapping();
		String namespace=am.getNamespace();
		String name=am.getName();
		if(useridStr==null){
			useridStr=((AbstractBaseAction)ai.getAction()).getCookieValue(CommonConstant.USER_ID_HEADER);
		}
		if(useridStr==null){
			if("/".equals(namespace) && "login".equals(name) || "logout".equals(name)){
				return ai.invoke();
			}else{
				return AbstractBaseAction.LOGIN;
			}
		}else if("/".equals(namespace) && "logout".equals(name)){
			return ai.invoke();
		}
		int userid=Integer.valueOf(useridStr);
		Map<String,Object> application=ActionContext.getContext().getApplication();
		String url=namespace+'/'+name;
		Set<String> granted=(Set<String>)application.get(useridStr);
		if(granted!=null){
			if(granted.contains(url)){
				return ai.invoke();
			}
		}else{
			granted=new HashSet<String>();
			application.put(userid+"", granted);
		}
		if(privilegeService.hasGranted(userid, namespace, name)){
			granted.add(url);
			return ai.invoke();
		}else{
			return AbstractBaseAction.NOT_GRANTED;
		}
	}

	public PrivilegeService getPrivilegeService() {
		return privilegeService;
	}
	public void setPrivilegeService(PrivilegeService privilegeService) {
		this.privilegeService = privilegeService;
	}
}
