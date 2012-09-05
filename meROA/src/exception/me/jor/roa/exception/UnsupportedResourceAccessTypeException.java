package me.jor.roa.exception;

import me.jor.roa.core.accessable.AccessMethod;

public class UnsupportedResourceAccessTypeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7146206290443051405L;
	private AccessMethod am;
	private String accessType;

	public UnsupportedResourceAccessTypeException(AccessMethod am, Throwable cause) {
		super(cause);
		this.am=am;
	}
	public UnsupportedResourceAccessTypeException(AccessMethod am, String accessType, Throwable cause){
		this(am, cause);
		this.accessType=accessType;
	}
	public String toString(){
		return new StringBuilder("AccessMethod:").append(am).append(" AccessType:").append(accessType).append(super.toString()).toString();
	}
	public String getMessage(){
		return new StringBuilder("AccessMethod:").append(am).append(" AccessType:").append(accessType).toString();
	}
}
