package me.jor.roa.core.impl;

import java.io.File;
import java.util.jar.JarFile;

import org.springframework.context.ApplicationContext;

import me.jor.roa.core.ROAConfigParserDefinition;

//roa相关配置应包含远程资源服务器相关配置包含服务器ip/port和能提供的资源uri列表
public class ROAConfParserDef implements ROAConfigParserDefinition {

	@Override
	public void parseROAConfig(JarFile jar, ApplicationContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseROAConfig(File classpath, ApplicationContext context) {
		// TODO Auto-generated method stub

	}

}
