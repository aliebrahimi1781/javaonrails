package me.jor.cxf;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import me.jor.util.ip.IPSeeker;

import org.apache.cxf.transport.http.AbstractHTTPDestination;

public abstract class AbstractBaseCXFWS {
	
	@Resource
	private WebServiceContext context;
	/**
	 * 得到发起webservice请求的MessageContext对象
	 * @return
	 */
	public MessageContext getContext(){
		return context.getMessageContext();
	}
	/**
	 * 得到MessageContext对象的值 
	 * @param destination AbstractHTTPDestination内维护的的常量值
	 * @return
	 */
	public Object getContextValue(String destination){
		return getContext().get(destination);
	}
	/**
	 * 得到发起webservice请求的Request对象
	 * @return
	 */
	public HttpServletRequest getRequest(){
		return (HttpServletRequest)getContextValue(AbstractHTTPDestination.HTTP_REQUEST);
	}
	public ServletContext getHttpContext(){
		return (ServletContext)getContextValue(AbstractHTTPDestination.HTTP_CONTEXT);
	}
	/**
	 * 得到发起请求的ip
	 * @return
	 */
	public String getIp(){
		return IPSeeker.getIp(getRequest());
	}
	public String getRealPath(String path){
		return getHttpContext().getRealPath(path);
	}
}
