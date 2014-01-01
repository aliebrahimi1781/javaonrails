package me.jor.redis;

import java.lang.invoke.MethodHandle;

public class PipelineParams {
	private MethodHandle cmd;
	private String key;
	private Object param;
	public PipelineParams(MethodHandle cmd,String key, Object param) {
		super();
		this.key = key;
		this.param = param;
	}
	public MethodHandle getCmd(){
		return cmd;
	}
	public String getKey() {
		return key;
	}
	public Object getParam() {
		return param;
	}
	
}
