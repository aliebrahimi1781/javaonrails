package me.jor.roa.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import me.jor.roa.common.constant.ROAConstant;
import me.jor.roa.core.impl.ROAConfParserDef;
import me.jor.util.Log4jUtil;
import me.jor.util.concurrent.ExecutionAwait;

import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;

public class ROAConfigParser implements ROAConfigParserDefinition{
	 private static final Log log=Log4jUtil.getLog(ROAAccess.class);
	 private static final String RESOURCE_PATH="res";
	 private static final File RES=ResourceAccessContext.getRealPathFile(RESOURCE_PATH);
	 private static final Map<String,Long> LAST_MODIFIED_MAP=new HashMap<String,Long>();
	 private static volatile ExecutionAwait await;
	 
	 public static final ROAConfigParser parser=new ROAConfigParser();
	 
	 static{
		 if(ROAConstant.loadResOnStartup()){
			 try {
				parser.parse(ROAConfigParser.class.getClassLoader());
			} catch (Exception e) {
				log.error("",e);
			}
		 }
	 }
	 
	 private void parse(ClassLoader parent) throws IOException, InterruptedException{
		 for(File file:RES.listFiles()){
			 parse(file,parent);
		 }
	 }
	 public void parse(String module) throws InterruptedException, IOException{
		 File file=new File(RES,module);
		 if(!file.exists()){
			 file=new File(RES,module+".jar");
		 }
		 if(file.exists()){
			 parse(file,ROAConfigParser.class.getClassLoader());
		 }
	 }
	private static ExecutionAwait getExecutionAwait(){
		if(await==null || await.needNewInstance()){
			 synchronized(ROAConfigParser.class){
				 if(await==null || await.needNewInstance()){
					 await=new ExecutionAwait(true);
				 }
			 }
		}
		 return await;
	 }

	 private void parse(File file, ClassLoader parent) throws InterruptedException, IOException{
		 getExecutionAwait();
		 try{
			 if(!await.await()){
				 String name=file.getName();
				 Long prevLast=LAST_MODIFIED_MAP.get(name);
				 long last=file.lastModified();
				 if((prevLast==null || prevLast<last)){
					 if(file.isFile()){
						 if(name.endsWith(".jar")){
							 parseJar(file, parent);
						 }
					 }else{
						 parseClasspath(file,parent);
					 }
					 LAST_MODIFIED_MAP.put(name, last);
				 }
			 }
		 }finally{
			 await.signal();
		 }
	 }
	 
	 private void parseJar(File file, ClassLoader parent) throws IOException{
		 JarFile jar=new JarFile(file);
		 ApplicationContext context=ApplicationContextConfigParser.createJarFileApplicationContext(parent, jar);
		 parseROAConfig(jar, context);
	 }
	 private void parseClasspath(File classpath,ClassLoader parent){
		 ApplicationContext context=ApplicationContextConfigParser.createClassPathApplicationContext(parent);
    	 parseROAConfig(classpath,context);
	 }
	 private ROAConfigParserDefinition getParserDef(ApplicationContext context){
		 ROAConfigParserDefinition parserDef=(ROAConfigParserDefinition)context.getBean(ROAConstant.ROA_CONF_PARSER_DEF);
		 if(parserDef==null){
			 parserDef=new ROAConfParserDef();
		 }
		 return parserDef;
	 }
	 @Override
	 public void parseROAConfig(JarFile jar, ApplicationContext context){
		 getParserDef(context).parseROAConfig(jar,context);
		 //应在实现parseROAConfig时将context作为ResourceAccess对象保存
	 }
	 @Override
	 public void parseROAConfig(File classpath, ApplicationContext context){
		 getParserDef(context).parseROAConfig(classpath, context);
		//应在实现parseROAConfig时将context作为ResourceAccess对象保存
	 }
}
