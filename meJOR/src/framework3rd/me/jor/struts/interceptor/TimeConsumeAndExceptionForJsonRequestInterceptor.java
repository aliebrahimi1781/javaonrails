package me.jor.struts.interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.jor.common.Decider;
import me.jor.common.GlobalObject;
import me.jor.struts.action.AbstractBaseAction;
import me.jor.util.Help;
import me.jor.util.IOUtil;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;
import org.apache.struts2.ServletActionContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 计算每次请求的耗时，并把耗时和请求参数、action类名、url记录到info日志
 */
public class TimeConsumeAndExceptionForJsonRequestInterceptor extends AbstractInterceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 300759252325913128L;
	private static final Log logger=Log4jUtil.getLog(TimeConsumeAndExceptionForJsonRequestInterceptor.class);
	private String defaultCharset;
	private char logSplitor='|';
	private boolean debug;
	private Decider logDecider;
	public String getDefaultCharset() {
		return defaultCharset;
	}
	public void setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
	}
	public char getLogSplitor() {
		return logSplitor;
	}
	public void setLogSplitor(char logSplitor) {
		this.logSplitor = logSplitor;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public Decider getLogDecider() {
		return logDecider;
	}
	public void setLogDecider(Decider logDecider) {
		this.logDecider = logDecider;
	}
	private String getParams(AbstractBaseAction action) throws IOException{
		String params=IOUtil.readString(action.getRequest().getInputStream(), defaultCharset);
		if(Help.isNotEmpty(params)){
			params=beforeTransformJson(params);
			action.getRequest().setAttribute("params",params);
		}
		return params;
	}
	private boolean decideLog(String url){
		return logDecider==null || logDecider.decide(url);
	}
	private void log(AbstractBaseAction action,Map paramValues,Object resultContent,long start) throws JsonProcessingException{
		String url=ServletActionContext.getRequest().getServletPath();
		Map log=new HashMap();
		log.put("ip",action.getIp());
		log.put("url", url);
		if(decideLog(url)){
			log.put("params", paramValues);
			log.put("result", resultContent);
		}
		log.put("consumed", System.currentTimeMillis()-start);
		logger.info(GlobalObject.getJsonMapper().writeValueAsString(log));
	}
	private void log(AbstractBaseAction action,Object resultContent,Throwable t) throws JsonProcessingException{
		String url=ServletActionContext.getRequest().getServletPath();
		Map log=new HashMap();
		log.put("ip",action.getIp());
		log.put("url", url);
		log.put("result", resultContent);
		StringBuilder logMsg=new StringBuilder(action.getClass().getName()).append(logSplitor);
		Object params=action.getRequest().getAttribute("params");
		if(Help.isNotEmpty(params)){
			logMsg.append(params instanceof String?params:GlobalObject.getJsonMapper().writeValueAsString(params)).append(logSplitor);
		}
		log.put("throwable", t.getClass().getName());
		logger.error(logMsg.append(GlobalObject.getJsonMapper().writeValueAsString(log)),t);
	}
	@Override
	public String intercept(ActionInvocation ai) throws Exception {
		AbstractBaseAction action=(AbstractBaseAction)ai.getAction();
		String params=getParams(action);
		Object resultContent=null;
		try{
			long start=System.currentTimeMillis();
			Map paramValues=null;
			if(Help.isNotEmpty(params)){
				paramValues=GlobalObject.getJsonMapper().readValue(params, Map.class);
				if(debug){
					action.getRequest().setAttribute("params", paramValues);
				}
				Help.populate(action,paramValues,false);
			}
			String result=ai.invoke();
			resultContent=action.getRequest().getAttribute("result");
			if(Help.isEmpty(resultContent)){
				resultContent=action.getResult();
			}
			log(action,paramValues,resultContent,start);
			return result;
		}catch(Throwable t){
			log(action, resultContent, t);
			if(Help.isNotEmpty(resultContent)){
				return ActionSupport.SUCCESS;
			}else{
				return ActionSupport.ERROR;
			}
		}
	}
	public String beforeTransformJson(String params){
		return params;
	}
}