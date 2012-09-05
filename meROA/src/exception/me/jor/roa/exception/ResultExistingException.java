package me.jor.roa.exception;

import me.jor.roa.core.accessable.Result;

public class ResultExistingException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7097338766301390113L;
	private String uri;
	private Result newOne;
	private Result existing;
	public ResultExistingException(String uri, Result newOne, Result existing) {
		super();
		this.uri = uri;
		this.newOne = newOne;
		this.existing = existing;
	}
	
	@Override
	public String getMessage(){
		String msg=uri.equals("")?"common Result object has existed. ":("Result specified for "+uri+" has existed. ");
		msg+="existing one: "+existing.getClass().getName();
		msg+=" and new one: "+newOne.getClass().getName();
		return msg;
	}

}
