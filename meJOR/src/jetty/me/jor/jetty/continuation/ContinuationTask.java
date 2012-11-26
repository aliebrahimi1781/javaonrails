package me.jor.jetty.continuation;

import java.io.IOException;
import java.util.UUID;

import me.jor.util.Log4jUtil;

import org.eclipse.jetty.continuation.Continuation;

import me.jor.util.pubsub.AbstractSubTask;

public class ContinuationTask extends AbstractSubTask{
	
	protected Continuation continuation;
	protected boolean immediateCompletion;
	protected ContinuationTreatment treatment;
	
	
	public ContinuationTask() throws IOException{
		super(UUID.randomUUID());
		immediateCompletion=false;
		treatment=ContinuationTreatment.COMPLETE;
		writeUUID();
	}
	/**could be override*/
	public String generateResult(Object data){
		return data.toString();
	}
	
	protected void write(Object content) throws IOException{
		continuation.getServletResponse().getWriter().print(generateResult(content));
	}
	protected void writeUUID() throws IOException{
		write(getUUID());
	}
	
	@Override
	public void execute(Object data) {
		try {
			write(data);
			treat();
		} catch (IOException e) {
			Log4jUtil.log.error("",e);
		}
	}
	public void complete(){
		this.continuation.complete();
	}
	private void treat(){
		if(immediateCompletion){
			switch(treatment){
			case COMPLETE:
				this.continuation.complete();
				break;
			case RESUME:default:
				this.continuation.resume();
				break;
			}
		}
	}
	
	public void setContinuation(Continuation continuation) {
		this.continuation = continuation;
	}
	public void setImmediateCompletion(boolean immediateCompletion) {
		this.immediateCompletion = immediateCompletion;
	}
	public void setTreatment(ContinuationTreatment treatment) {
		this.treatment = treatment;
	}
}
