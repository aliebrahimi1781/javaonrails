package me.jor.roa.exception;

public class AccessDataParseException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -424648276776662267L;

	public AccessDataParseException() {}

	public AccessDataParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessDataParseException(String message) {
		super(message);
	}

	public AccessDataParseException(Throwable cause) {
		super(cause);
	}

}
