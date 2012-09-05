package me.jor.roa.exception;

public class NoSuchEnumValueException extends RuntimeException {
	
	private int value;
	
	public NoSuchEnumValueException(int value) {
		this.value=value;
	}

	public NoSuchEnumValueException(String message, int value) {
		super(message);
		this.value=value;
	}
	
	public String toString(){
		StringBuilder s=new StringBuilder();
		String msg=super.getMessage();
		if(msg!=null){
			s.append(msg).append('-');
		}
		return s.append(value).toString();
	}
}
