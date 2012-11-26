package me.jor.roa.protocol.servlet;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import me.jor.common.GlobalObject;
import me.jor.roa.core.AccessData;
import me.jor.roa.core.accessable.AccessMethod;
import me.jor.roa.util.ResourceAccessUtil;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

public class ServletResourceAccessUtil {
	public static void access(ServletRequest request, ServletResponse response,AccessMethod method) throws JsonParseException, JsonMappingException, IOException, Exception{
		HttpServletRequest hsr=((HttpServletRequest)request);
		AccessData data=GlobalObject.getJsonMapper().readValue(hsr.getParameter("param"), AccessData.class);
		String uri=hsr.getRequestURI();
		response.getWriter().print(ResourceAccessUtil.access(uri, data, method,true).toString());
	}
}
