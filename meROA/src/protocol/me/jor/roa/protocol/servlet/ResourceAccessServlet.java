package me.jor.roa.protocol.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.jor.roa.core.accessable.AccessMethod;

public class ResourceAccessServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4972127173676273972L;

	@Override
	protected void doDelete(HttpServletRequest request,HttpServletResponse response)throws ServletException{
		doService(request,response,AccessMethod.D);
	}
	@Override
	protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException{
		doService(request,response,AccessMethod.R);
	}
	@Override
	protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException{
		doService(request,response,AccessMethod.C);
	}
	@Override
	protected void doPut(HttpServletRequest request,HttpServletResponse response)throws ServletException{
		doService(request,response,AccessMethod.U);
	}
	@Override
	protected void doOptions(HttpServletRequest request,HttpServletResponse response)throws ServletException{
		doService(request,response,AccessMethod.O);
	}
	
	private void doService(HttpServletRequest request,HttpServletResponse response, AccessMethod method)throws ServletException{
		try {
			ServletResourceAccessUtil.access(request, response, method);
		}catch(ServletException e){
			throw e;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
