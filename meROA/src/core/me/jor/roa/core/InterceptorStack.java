package me.jor.roa.core;

import java.util.List;

public class InterceptorStack implements Interceptor{
	
	private Interceptor[] interceptors;
	private ThreadLocal<StackNode> node=new ThreadLocal<StackNode>();
	
	public InterceptorStack(Interceptor[] interceptors){
		this.interceptors=interceptors;
	}
	public InterceptorStack(List<Interceptor> interceptors){
		this(interceptors.toArray(new Interceptor[0]));
	}
	private Interceptor getInterceptor(){
		StackNode node=this.node.get();
		InterceptorStack stack=node.stack;
		Interceptor interceptor=null;
		if(stack!=null){
			interceptor=stack.getInterceptor();
		}
		if(interceptor==null){
			int i=++node.idx;
			interceptor=i<interceptors.length?interceptors[i]:null;
			if(interceptor instanceof InterceptorStack){
				stack=(InterceptorStack)interceptor;
				node.stack=stack;
				interceptor=stack.getInterceptor();
			}else{
				node.stack=null;
			}
		}
		return interceptor;
	}
	/**
	 * 这里是决定调用拦截器还是访问资源逻辑的枢纽
	 * 在拦截器的access方法内部调用context.access()即可实现拦截器的链式调用，如果已经到达拦截器链尾就调用具体的资源
	 * @param context
	 * @return
	 * @throws Exception
	 * @see me.jor.roa.core.accessable.Accessable.access(me.jor.roa.core.ResourceAccessContext)
	 */
	@Override
	public Object access(ResourceAccessContext context) throws Exception {
		Interceptor interceptor=getInterceptor();
		if(interceptor!=null){
			return interceptor.access(context);
		}else{
			this.node.remove();
			return context.access();
		}
	}
	@Override
	public Object getDescription() {
		return interceptors;
	}
	public boolean hasNext(){
		StackNode node=this.node.get();
		if(node==null){
			node=new StackNode();
			this.node.set(node);
		}
		return node.stack!=null || node.idx<interceptors.length;
	}
	
	private class StackNode{
		int idx=-1;
		InterceptorStack stack;
	}
}
