package me.jor.jetty.continuation;

import java.io.IOException;

import javax.servlet.FilterConfig;

public class IframeContinuationTask extends ContinuationTask{
	
	private String functionName;
	
	public IframeContinuationTask() throws IOException {
		super();
	}
	private String getFunctionName(){
		if(functionName==null){
			synchronized(this){
				if(functionName==null){
					functionName=new StringBuilder("<script>parent.")
								.append(((FilterConfig)continuation.getAttribute("$filterConfig")).getInitParameter("functionName"))
								.append('(').toString();
				}
			}
		}
		return functionName;
	}
	@Override
	protected void write(Object content) throws IOException{
		super.write(getFunctionName());
		super.write(generateResult(content));
		super.write(");" +
				"var node=document.getElementsByTagName('script');" +
				"node=node[node.length-1];" +
				"node.parentNode.removeChild(node);" +
				"</script>");
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
}
