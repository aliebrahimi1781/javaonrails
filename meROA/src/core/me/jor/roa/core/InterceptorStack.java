package me.jor.roa.core;

import java.util.List;

public class InterceptorStack implements Interceptor{
	
	private Interceptor[] interceptors;
	private ThreadLocal<Integer> idx=new ThreadLocal<Integer>();
	
	public InterceptorStack(Interceptor[] interceptors){
		this.interceptors=interceptors;
	}
	public InterceptorStack(List<Interceptor> interceptors){
		this(interceptors.toArray(new Interceptor[0]));
	}
	private int getI(){
		Integer integer=idx.get();
		int i=0;
		if(integer!=null){
			i=integer;
		}
		idx.set(i+1);
		return i;
	}
	private Interceptor getInteceptor(){
		int i=getI();
		return i<interceptors.length?interceptors[i]:null;
	}
	
	@Override
	public Object access(ResourceAccessContext context) throws Exception {
		Interceptor interceptor=getInteceptor();
		if(interceptor!=null){
			return interceptor.access(context);
		}else{
			idx.remove();
			return context.access();
		}
	}
	@Override
	public Object getDescription() {
		return interceptors;
	}
}
