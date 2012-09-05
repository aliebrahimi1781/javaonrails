package me.jor.roa.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.jor.classloader.ClassPathClassLoader;
import me.jor.classloader.JarFileClassLoader;
import me.jor.roa.exception.ApplicationContextCreationException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

public class ApplicationContextConfigParser {
	private static final String applicationContextClass="org.springframework.context.support.GenericXmlApplicationContext";
	private static ApplicationContext createApplicationContext(ClassLoader classloader, List<Resource> resources){
		try{
			GenericXmlApplicationContext appContext=(GenericXmlApplicationContext)classloader.loadClass(applicationContextClass).newInstance();
			appContext.load(resources.toArray(new Resource[0]));
			appContext.refresh();
			return appContext;
		}catch(Exception e){
			throw new ApplicationContextCreationException(e);
		}
		
	}
	static ApplicationContext createJarFileApplicationContext(ClassLoader parent, JarFile jar) throws IOException{
		List<Resource> list=new ArrayList<Resource>();
		for(Enumeration<JarEntry> entries=jar.entries();entries.hasMoreElements();){
			JarEntry entry=entries.nextElement();
			String name=entry.getName();
			if(name.startsWith("applicationContext-") && name.endsWith(".xml")){
				list.add(new InputStreamResource(jar.getInputStream(entry)));
			}
		}
		return createApplicationContext(new JarFileClassLoader(jar, parent, applicationContextClass,false), list);
	}
	static ApplicationContext createClassPathApplicationContext(ClassLoader parent){
		try{
			final List<Resource> list=new ArrayList<Resource>();
			File classpath=new File(ApplicationContextConfigParser.class.getResource("/").toURI());
			File[] files=classpath.listFiles();
			for(int i=0,l=files.length;i<l;i++){
				File file=files[i];
				String name=file.getName();
				if(file.isFile() && name.startsWith("applicationContext-") && name.endsWith(".xml")){
					try {
						list.add(new InputStreamResource(new FileInputStream(file)));
					} catch (FileNotFoundException e) {}
				}
			}
			return createApplicationContext(new ClassPathClassLoader(classpath ,parent, applicationContextClass,false), list);
		}catch(URISyntaxException e){
			throw new ApplicationContextCreationException(e);
		}
	}
}
