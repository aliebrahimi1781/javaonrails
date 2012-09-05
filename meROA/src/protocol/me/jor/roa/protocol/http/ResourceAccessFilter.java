package me.jor.roa.protocol.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import me.jor.common.GlobalObject;
import me.jor.roa.core.AccessData;
import me.jor.roa.core.ResourceAccessHandler;

public class ResourceAccessFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			    throws IOException, ServletException {
		HttpServletRequest hsr=((HttpServletRequest)request);
		response.getWriter().print(ResourceAccessHandler.handle(hsr.getRequestURI(), 
				GlobalObject.getJsonMapper().readValue(hsr.getParameter("param"), AccessData.class)).toString());
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
