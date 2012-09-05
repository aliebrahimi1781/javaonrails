package me.jor.roa.protocol.niosocket;

import me.jor.common.GlobalObject;
import me.jor.nio.Starter;
import me.jor.nio.runnable.ExecutionAfterReading;
import me.jor.roa.core.AccessData;
import me.jor.roa.exception.ResourceAccessClientException;


public class ResourceAccessClient{
	public static void access(String uri, AccessData accessData, Class<?> dstType, ExecutionAfterReading<?> task){
		try {
			Starter.transfer(task , 
				new StringBuilder(uri).append('?').append(GlobalObject.getJsonMapper().writeValueAsString(accessData)).toString(), dstType);
		} catch (Exception e) {
			throw new ResourceAccessClientException(e);
		}
	}
}
