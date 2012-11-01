package me.jor.roa.exception;

import me.jor.roa.core.accessable.AccessMethod;

public class UnsupportedResourceAccessMethodException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9005746066644842614L;

	public UnsupportedResourceAccessMethodException() {
		super();
	}

	public UnsupportedResourceAccessMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedResourceAccessMethodException(String message) {
		super(message);
	}

	public UnsupportedResourceAccessMethodException(Throwable cause) {
		super(cause);
	}
	public UnsupportedResourceAccessMethodException(String uri, AccessMethod accessMethod){
		this(new StringBuilder("the resource named ")
		      .append(uri).append(" does not support access method ")
		      .append(stringfiAccessMethod(accessMethod)).toString());
	}
	private static String stringfiAccessMethod(AccessMethod accessMethod){
		switch(accessMethod){
		case C:return "CREATABLE";
		case R:return "RETRIVABLE";
		case U:return "UPDATABLE";
		case D:return "DELETABLE";
		default:return "";
		}
	}
}
