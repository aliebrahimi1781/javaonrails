package me.jor.roa.core.accessable;


public interface BaseAccess extends Accessable{
	public AccessMethod getDefaultMethod();
	public String getDefaultDataType();
	public String getDefaultErrorType();
	public Result getResult(String name);
}
