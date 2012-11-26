package me.jor.jetty.continuation.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import me.jor.exception.JettyContinuationFilterException;
import me.jor.jetty.continuation.ContinuationTask;
import me.jor.jetty.continuation.ContinuationTreatment;
import me.jor.util.Help;
import me.jor.util.Log4jUtil;
import me.jor.util.pubsub.PubSub;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.ContinuationSupport;

public class ContinuationFilter implements Filter{

	private static final Pattern UUID_SPLITOR=Pattern.compile(";uuid:");
	
	private PubSub pubsub=PubSub.getPubSub();
	private ContinuationTreatment treatment;
	private List<ContinuationListener> listener;
	private long timeout;
	private boolean immediateCompletion;
	private Class<ContinuationTask> task;
	private FilterConfig config;

	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Continuation continuation=ContinuationSupport.getContinuation(request);
		continuation.setAttribute("chain", chain);
		continuation.setAttribute("request", request);
		for(int i=0,l=listener.size();i<l;i++){
			continuation.addContinuationListener(listener.get(i));
		}
		String[] uri=UUID_SPLITOR.split(((HttpServletRequest)request).getServletPath());
		
		String uuid=uri.length==2?uri[1]:null;
		int paraStart=uuid.indexOf('?');
		if(paraStart>0){
			uuid=uuid.substring(0,paraStart);
		}
		try {
			ContinuationTask task = this.task.newInstance();
			task.setContinuation(continuation);
			task.setTreatment(treatment);
			task.setImmediateCompletion(immediateCompletion);
			task.setTimeout(timeout);
			if(Help.isNotEmpty(uuid)){
				task.setUUID(uuid);
			}
			continuation.setAttribute("$filterConfig", config);
			pubsub.sub(uri[0], task);
		} catch (Exception e) {
			throw new JettyContinuationFilterException(e);
		}
		
		continuation.setTimeout(timeout);
		continuation.suspend(response);
	}

	@Override
	public void init(final FilterConfig cfg) throws ServletException {
		try {
			task=(Class<ContinuationTask>)Class.forName(cfg.getInitParameter("task"));
			timeout=Help.convert(cfg.getInitParameter("timeout"), 0);
			treatment=Help.convert(cfg.getInitParameter("treatment"),ContinuationTreatment.COMPLETE);
			String immediate=cfg.getInitParameter("immediateCompletion");
			immediateCompletion=Help.isNotEmpty(immediate)?Boolean.parseBoolean(immediate):false;
			listener=new ArrayList<ContinuationListener>();
			setCotinuationListener(Help.convert(cfg.getInitParameter("listener"), ""));
			config=cfg;
		} catch (Exception e) {
			Log4jUtil.log.error("error occured on initializing instance of ContinuationFilter ",e);
		}
	}

	public void setCotinuationListener(String listenerName) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		if(Help.isEmpty(listenerName)){
			this.listener.add(new ContinuationListener(){
				@Override
				public void onComplete(Continuation continuation) {
					switch(ContinuationFilter.this.treatment){
					case COMPLETE:
						ServletResponse response=continuation.getServletResponse();
						if(response!=null){
							try {
								response.getWriter().print(continuation.getAttribute("result").toString());
								chain(continuation);
							} catch (IOException e) {
								Log4jUtil.log.error("error occured on invoking ServletResponse.getWriter()",e);
							}
						}
						break;
					}
				}

				@Override
				public void onTimeout(Continuation continuation) {
					chain(continuation);
				}
				private void chain(Continuation continuation){
					try {
						((FilterChain)continuation.getAttribute("chain")).doFilter((ServletRequest)continuation.getAttribute("request"), continuation.getServletResponse());
					} catch (Exception e) {
						Log4jUtil.log.error("error occured on FilterChain.doFilter()",e);
					}
				}
			});
		}else{
			String[] listeners=listenerName.split(",");
			for(int i=0,l=listeners.length;i<l;i++){
				this.listener.add((ContinuationListener)Class.forName(listeners[i]).newInstance());
			}
		}
	}
}
