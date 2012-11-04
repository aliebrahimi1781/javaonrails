package me.jor.roa.protocol.servlet;

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
import me.jor.util.Log4jUtil;

public class ResourceAccessFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			    throws IOException, ServletException {
		try {
			HttpServletRequest hsr=((HttpServletRequest)request);
			response.getWriter().print(ResourceAccessHandler.handle(hsr.getRequestURI(), 
					GlobalObject.getJsonMapper().readValue(hsr.getParameter("param"), AccessData.class),true).toString());
			chain.doFilter(request, response);
		}catch(IOException e){
			throw e;
		}catch(ServletException e){
			throw e;
		} catch (Exception e) {
			Log4jUtil.getLog(ResourceAccessFilter.class).error("",e);
		}
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
