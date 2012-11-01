package me.jor.roa.protocol.niosocket;

import me.jor.common.GlobalObject;
import me.jor.nio.runnable.ExecutionAfterReading;
import me.jor.roa.core.AccessData;
import me.jor.roa.core.ResourceAccessHandler;

public class ResourceAccessServerTask implements ExecutionAfterReading<String>{
	private String accessData;
	
	@Override
	public ExecutionAfterReading<String> setAccessData(String accessData) {
		this.accessData=accessData;
		return this;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T> T execute() throws Throwable {
		String[] ad=this.accessData.split("?");
		return (T)ResourceAccessHandler.handle(ad[0], GlobalObject.getJsonMapper().readValue(ad[1], AccessData.class),true);
	}
}
