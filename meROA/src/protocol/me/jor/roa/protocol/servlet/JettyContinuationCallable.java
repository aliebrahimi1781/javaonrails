package me.jor.roa.protocol.servlet;

import javax.servlet.http.HttpServletRequest;

import me.jor.common.GlobalObject;
import me.jor.jetty.filter.continuation.ContinuationCallable;
import me.jor.roa.core.AccessData;
import me.jor.roa.core.ResourceAccessHandler;

public class JettyContinuationCallable extends ContinuationCallable{

	@Override
	public Object call() throws Exception {
		HttpServletRequest hsr=((HttpServletRequest)super.continuation.getAttribute("request"));
		return ResourceAccessHandler.handle(hsr.getRequestURI(), 
				GlobalObject.getJsonMapper().readValue(hsr.getParameter("param"), AccessData.class),true);
	}

}
