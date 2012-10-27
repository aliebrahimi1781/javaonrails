package me.jor.roa.core;

import java.io.File;
import java.util.jar.JarFile;

import org.springframework.context.ApplicationContext;
/**
 * roa相关配置应包含远程资源服务器相关配置包含服务器ip/port和能提供的资源uri列表
 * 必须检查资源配置文件内有没有为每一个资源定义description
 * @author wujingrun
 *
 */
public interface ROAConfigParserDefinition {
	public void parseROAConfig(JarFile jar, ApplicationContext context);
	 public void parseROAConfig(File classpath, ApplicationContext context);
}
