package me.jor.roa.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

import org.springframework.context.ApplicationContext;

public class ROAConfigParser {
	 private static final String RESOURCE_PATH="res";
	 
	 static void parse(ClassLoader parent) throws IOException{
		 parseJars(parent);
		 parseClasspath(parent);
		 ROAAccess.optimize();
	 }
	 
	 private static void parseJars(ClassLoader parent) throws IOException{
		 for(File file:ResourceAccessContext.getRealPathFile(RESOURCE_PATH).listFiles()){
			 if(file.isFile() && file.getName().endsWith(".jar")){
				 parseJars(file, parent);
			 }
		 }
	 }
	 private static void parseJars(File file, ClassLoader parent) throws IOException{
		 JarFile jar=new JarFile(file);
		 ApplicationContext context=ApplicationContextConfigParser.createJarFileApplicationContext(parent, jar);
		 ROAAccess.addApplicationContext(file.getName(),context);
		 parseROAConfig(jar, context);
	 }
	 private static void parseClasspath(ClassLoader parent){
		 ApplicationContext context=ApplicationContextConfigParser.createClassPathApplicationContext(parent);
		 ROAAccess.addApplicationContext("",context);
		 try {
			parseROAConfig(new File(parent.getResource("").toURI()),context);
		} catch (URISyntaxException e) {}
	 }
	 private static void parseROAConfig(JarFile jar, ApplicationContext context){
		 
	 }
	 private static void parseROAConfig(File classpath, ApplicationContext context){
		 
	 }
}
