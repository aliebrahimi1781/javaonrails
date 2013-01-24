package me.jor.struts.interceptor;

import java.util.Arrays;
import java.util.Map;

import me.jor.common.CommonConstant;
import me.jor.struts.action.AbstractBaseAction;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 计算每次请求的耗时，并把耗时和请求参数、action类名、url记录到info日志
 *
 */
public class TimeConsumeAndExceptionInterceptor extends AbstractInterceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8351194491902862422L;
	@Override
	public String intercept(ActionInvocation ai) throws Exception {
		Map<String, Object> params=(Map<String, Object>)ai.getInvocationContext().getParameters();
		StringBuilder requestString= new StringBuilder();
		for(Map.Entry<String, Object> entry:params.entrySet()){
			String paramName=entry.getKey();
			if(!paramName.equals(CommonConstant.PASSWORD)){
				requestString.append(paramName).append('=').append(Arrays.toString((Object[])entry.getValue())).append('&');
			}
		}
		int last=requestString.length()-1;
		if(last>0 && requestString.lastIndexOf("&")==last){
			requestString.deleteCharAt(last);
		}
		AbstractBaseAction action=(AbstractBaseAction)ai.getAction();
		StringBuilder log=new StringBuilder(action.getClass().getName())
							 .append(";url:").append(ServletActionContext.getRequest().getServletPath())
							 .append(";parameters:").append(requestString.toString().replaceAll("\\s+", ""))
							 .append(";userid:").append(action.getCookieValue(CommonConstant.USER_ID_HEADER));
		Log logger=Log4jUtil.log;
		try{
			long start=System.currentTimeMillis();
			String result=ai.invoke();
			logger.info(log.append(";consume:").append((System.currentTimeMillis()-start)));
			return result;
		}catch(Throwable t){
			logger.error(log.append(";throwable:").append(t.toString()),t);
			return ActionSupport.ERROR;
		}
	}
}