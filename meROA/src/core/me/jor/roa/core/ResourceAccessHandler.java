package me.jor.roa.core;



/**
 * 被具体的协议访问（比如javaee的Filter/Servlet实现）
 *
 */
public class ResourceAccessHandler {
	public static Object handle(String uri, Object accessData, boolean generateResult) throws Exception{
		return ResourceAccessContext.newInstance(uri, accessData, generateResult).access();
	}
}
