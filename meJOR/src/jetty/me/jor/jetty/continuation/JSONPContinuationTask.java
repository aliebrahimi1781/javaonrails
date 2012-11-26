package me.jor.jetty.continuation;

import java.io.IOException;

public class JSONPContinuationTask extends ContinuationTask{

	private ThreadLocal<String> functionName;
	
	public JSONPContinuationTask() throws IOException {
		this.functionName=new ThreadLocal<String>();
	}
	
	public String generateResult(Object data){
		return new StringBuilder(functionName.get()).append('(').append(data.toString()).append(");").toString();
	}
	
	public void write(String functionName,Object content) throws IOException{
		this.functionName.set(functionName);
		super.write(content);
	}
}
