package me.jor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;

import me.jor.common.CommonConstant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * 用此类获取的Log对象都会事先判断isXxxxEnabled然后再执行相应的日志记录方法
 * 不必再显式调用isXXXEnabled
 **/
public class Log4jUtil {
	private static final String KEY=Log4jUtil.class.getName();
	public static final Log log = new LogWrapper(LogFactory.getLog(Log4jUtil.class));
	private static Map<String,Log> logMap;
	
	
	static{
		initLogMap();
	}
	
	private static String LOG4JPATH;
	private static boolean watch=false;
	private static String log4jPath(){
		String path=LOG4JPATH;
		if(!path.startsWith("/")){
			path="/"+LOG4JPATH;
		}
		return path;
	}
	
	private static void initLogMap(){
		if(logMap==null){
			synchronized(Log4jUtil.class){
				if(logMap==null){
					logMap=new HashMap<String,Log>();
				}
			}
		}
	}
	public static Log getLog(String key){
		Log log=logMap.get(KEY+key);
		if(log==null){
			Lock lock=LockCache.getReentrantLock(key);
			boolean locked=false;
			try{
				lock.lock();
				locked=true;
				log=logMap.get(key);
				if(log==null){
					log=new LogWrapper(LogFactory.getLog(key));
					logMap.put(key, log);
				}
			}finally{
				if(locked){
					lock.unlock();
					locked=false;
				}
			}
		}
		return log;
	}
	public static Log getLog(Class cls){
		String key=KEY+cls.getName();
		Log log=logMap.get(key);
		if(log==null){
			synchronized(cls){
				log=logMap.get(key);
				if(log==null){
					log=new LogWrapper(LogFactory.getLog(cls));
					logMap.put(key, log);
				}
			}
		}
		return log;
	}
	
	private static Properties loadLog4jConfig() throws UnsupportedEncodingException, IOException{
		return Help.loadProperties(CommonConstant.class.getResourceAsStream(log4jPath()),CommonConstant.DEFAULT_CHARSET);
	}
	private static void storeLog4j(Properties log4jconf) throws UnsupportedEncodingException, FileNotFoundException, IOException, URISyntaxException{
		Help.storeProperties(log4jconf, new OutputStreamWriter(new FileOutputStream(new File(CommonConstant.class.getResource(log4jPath()).toURI())),CommonConstant.DEFAULT_CHARSET));
	}
	private static void configure(Properties log4jconf) throws UnsupportedEncodingException, FileNotFoundException, IOException, URISyntaxException{
		synchronized(Log4jUtil.class){
			if(watch){
				storeLog4j(log4jconf);
			}else{
				PropertyConfigurator.configure(log4jconf);
			}
		}
	}
	/**
	 * 
	 * @param key 键
	 * @param value 值
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws URISyntaxException void
	 * @throws 
	 * @exception
	 */
	public synchronized static void updateLog4jConfig(String key, String value) throws UnsupportedEncodingException, IOException, URISyntaxException{
		Properties confprops=loadLog4jConfig();
		if(Help.isEmpty(value)){
			confprops.remove(key);
		}else{
			confprops.setProperty(key, value);
		}
		configure(confprops);
	}
	/**
	 * props:新的配置值，
	 * overwrite:true:用props覆盖原配置，没有在props中出现的值继续使用原配置；false：用props完全代替原配置，没有在props中出现的值被丢弃
	 * @param props 新的配置值，
	 * @param overwrite true:用props覆盖原配置，没有在props中出现的值继续使用原配置；false：用props完全代替原配置，没有在props中出现的值被丢弃
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws URISyntaxException void
	 * @throws 
	 * @exception
	 */
	@SuppressWarnings("rawtypes")
	public synchronized static void updateLog4jConfig(Properties props,boolean overwrite) throws UnsupportedEncodingException, IOException, URISyntaxException{
		Properties confprops=null;
		if(overwrite){
			confprops=loadLog4jConfig();
			for(Map.Entry entry:props.entrySet()){
				confprops.setProperty(entry.getKey().toString(), entry.getValue().toString());
			}
		}else{
			confprops=props;
		}
		configure(confprops);
	}
	
	public static class LogWrapper implements Log{
		private Log log;
		public LogWrapper(Log log){
			this.log=log;
		}
		
		@Override
		public void debug(Object o) {
			if(isDebugEnabled()){
				log.debug(o);
			}
		}

		@Override
		public void debug(Object o, Throwable t) {
			if(isDebugEnabled()){
				log.debug(o,t);
			}
		}

		@Override
		public void error(Object o) {
			if(isErrorEnabled()){
				log.error(o);
			}
		}

		@Override
		public void error(Object o, Throwable t) {
			if(isErrorEnabled()){
				log.error(o,t);
			}
		}

		@Override
		public void fatal(Object o) {
			if(isFatalEnabled()){
				log.fatal(o);
			}
		}

		@Override
		public void fatal(Object o, Throwable t) {
			if(isFatalEnabled()){
				log.fatal(o,t);
			}
		}

		@Override
		public void info(Object o) {
			if(isInfoEnabled()){
				log.info(o);
			}
		}

		@Override
		public void info(Object o, Throwable t) {
			if(isInfoEnabled()){
				log.info(o,t);
			}
		}

		@Override
		public boolean isDebugEnabled() {
			return log.isDebugEnabled();
		}

		@Override
		public boolean isErrorEnabled() {
			return log.isErrorEnabled();
		}

		@Override
		public boolean isFatalEnabled() {
			return log.isFatalEnabled();
		}

		@Override
		public boolean isInfoEnabled() {
			return log.isInfoEnabled();
		}

		@Override
		public boolean isTraceEnabled() {
			return log.isTraceEnabled();
		}

		@Override
		public boolean isWarnEnabled() {
			return log.isWarnEnabled();
		}

		@Override
		public void trace(Object o) {
			if(isTraceEnabled()){
				log.trace(o);
			}
		}

		@Override
		public void trace(Object o, Throwable t) {
			if(isTraceEnabled()){
				log.trace(o,t);
			}
		}

		@Override
		public void warn(Object o) {
			if(isWarnEnabled()){
				log.warn(o);
			}
		}

		@Override
		public void warn(Object o, Throwable t) {
			if(isWarnEnabled()){
				log.warn(o,t);
			}
		}
		
	}
	
	static{
		String log4jreloadable=CommonConstant.getLOG4J_CONF_RELOADABLE();
		if("true".equals(log4jreloadable) && !watch){
			synchronized(Log4jUtil.class){
				if(!watch){
					LOG4JPATH=CommonConstant.getLOG4J_PATH();
					String log4jperiod=CommonConstant.getLOG4J_RELOAD_PERIOD();
					try {
						PropertyConfigurator.configureAndWatch(new File(Log4jUtil.class.getResource(LOG4JPATH).toURI()).toString(),Help.isEmpty(log4jperiod)?60:Integer.parseInt(log4jperiod));
						watch=true;
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		}
	}
}
