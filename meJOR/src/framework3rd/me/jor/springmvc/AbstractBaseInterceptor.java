package me.jor.springmvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.jor.common.CommonConstant;
import me.jor.util.Help;
import me.jor.util.IOUtil;
import me.jor.util.Log4jUtil;
import me.jor.util.ip.IPSeeker;

import org.apache.commons.logging.Log;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
/**
 * 为请求计时并记日志
 * 抽象方法：
 * beforeTransformParams(String params)//转化参数为业务对象
 * generateLogText(Map logContent)//将参数转化为符合业务需求的日志文本
 * 
 * <!-- 拦截器 -->  
    <mvc:interceptors>  
        <bean class="xxxxx.Interceptor"></bean><!--拦截所有访问-->
        <!-- 多个拦截器,顺序执行 -->  
        <mvc:interceptor>  
           <mvc:mapping path="/controller/*" /><!-- 如果不配置或/*,将拦截所有的Controller -->  
           <bean class="xxxx.Interceptor"></bean>  <!--拦截所有符合mvc:mapping的url，可以在此处配置多个拦截器，按照在配置文件中出现的顺序执行拦截器-->
        </mvc:interceptor>  
    </mvc:interceptors>  
 * @author wujingrun
 *
 */
public abstract class AbstractBaseInterceptor implements HandlerInterceptor{
	private Log logger=Log4jUtil.getLog(AbstractBaseInterceptor.class);
	private ThreadLocal<Long> startHandleMillis;
	private Log actualLogger;
	private String loggerName;
	private String charset;
	public String getCharset() {
		return Help.isEmpty(charset)?CommonConstant.DEFAULT_CHARSET:charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
			                    Object handler, Exception exception) throws Exception {
		try{
			Map logContent=new HashMap();
			logContent.put("params", request.getAttribute(SpringMVCConstant.REQUEST_PARAMS));
			logContent.put("result", request.getAttribute(SpringMVCConstant.RESULT_FOR_LOG));
			logContent.put("url", request.getServletPath());
			logContent.put("ip", IPSeeker.getIp(request));
			logContent.put("consumed", consumed());
			if(exception==null){
				getLogger().info(generateLogText(logContent));
			}else{
				logContent.put("exception", exception.getMessage());
				getLogger().error(generateLogText(logContent),exception);
			}
		}finally{
			removeStartMillis();
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			               Object handler, ModelAndView mv) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {
		startHandleTime();
		transformParams(request);
		return true;
	}

	private void startHandleTime(){
		startHandleMillis.set(System.currentTimeMillis());
	}
	private long consumed(){
		return System.currentTimeMillis()-startHandleMillis.get();
	}
	private void removeStartMillis(){
		startHandleMillis.remove();
	}
	private void transformParams(HttpServletRequest request) throws Exception{
		populateParams(request,getParams(request));
	}
	private Object getParams(HttpServletRequest request) throws Exception{
		String params=IOUtil.readString(request.getInputStream(), getCharset());
		Log log=getLogger();
		if(log.isDebugEnabled()){
			log.debug(this.getClass().getSimpleName()+".getParams:"+params+";"+request.getServletPath());
		}
		if(Help.isNotEmpty(params)){
			return beforeTransformParams(params);
		}
		return params;
	}
	/**
	 * 实际执行业务逻辑前对请求参数做预处理
	 * @param params
	 * @return
	 */
	protected abstract Object beforeTransformParams(String params) throws Exception;
	private void populateParams(HttpServletRequest request,Object params){
		request.setAttribute(SpringMVCConstant.REQUEST_PARAMS, params);
	}
	private Log getLogger(){
		if(actualLogger==null){
			synchronized(this){
				if(actualLogger==null){
					actualLogger=Help.isEmpty(loggerName)?logger:Log4jUtil.getLog(loggerName);
				}
			}
		}
		return actualLogger;
	}
	/**
	 * 把日志内容构造成符合业务要求的字符串
	 * @param logContent
	 * @return
	 */
	protected abstract String generateLogText(Map logContent) throws Exception;
}
