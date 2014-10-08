package me.jor.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.jor.exception.ConstantLoadingException;
import me.jor.util.Help;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;

/**
 * 在工程类路径的根下查找文件名以constant.properties结尾的文件<br/>
 * 加载其中的properties名值对作为常量定义保存在名为PROPERTIES的私有静态属性内<br/>
 * 可通过getProperties()方法获取<br/>
 * 
 * constant.prefix.properties文件中储存key是constant.prefix的常量，这个常量的值决定加载哪个properties常量文件里的值。
 * 如果constant.prefix=product，加载的就是product.constant.properties
 * 
 * properties.dev.reload.period 定义重新载入常量定义文件的周期，默认是60000ms，最好跟properties.dev.reload.period.timeunit搭配使用，否则可能导致周期过短或过长
 * properties.dev.reload.period.timeunit 定义重新载入周期的时间单位，默认是TimeUnit.MILLISECONDS
 * properties.dev.reloadable 如果这个键的值是"1"，就会重新定时重新加载常量定义文件，如果没有定义或者是其它值就不会
 * */
public final class CommonConstant {
	private static final Log log=Log4jUtil.getLog(CommonConstant.class);
	private static final String CONSTANT_PREFIX_FILE="constant.prefix.properties";
	private static final String CONSTANT_PREFIX="constant.prefix";
	private static boolean loadScheduled;
	/**
	 * 常量属性文件后缀，所有在资源目录中以此结束的文件名都作为常量文件名加载。
	 * 常量key相同的以最后一次出现的为准
	 */
	public static final String CONSTANT_PROPERTIES="constant.properties";
	/**
	 * 以此为名在cookie中保存登录用户id
	 */
	public static final String USER_ID_HEADER="User-Id";
	/**
	 * 以此为名在cookie中保存登录用户名
	 */
	public static final String LOGGED_USERNAME="LOGGED-USERNAME";
	/**
	 * 未找到登录用户名的标志
	 */
	public static final long LOGIN_USERNAME_UNEXISTANCE=-1;
	/**
	 * 未找到登录用户密码的标志
	 */
	public static final long LOGIN_INCORRECT_PASSWORD=-2;
	/**
	 * 默认字符集
	 */
	public static final String DEFAULT_CHARSET="UTF-8";
	public static final String PASSWORD = "password";

	private static Properties PROPERTIES;
	
	public static Properties getPROPERTIES(){
		return PROPERTIES!=null?PROPERTIES:System.getProperties();
	}
	
	/**
	 * 返回值作为向浏览器推送的javascript函数名
	 * properties键名：properties.dev.project.iframefn
	 * @return String
	 */
	public static String getIFRAMEFN(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.iframefn");
	}

	/**
	 * 决定log4j配置是否可在运行期重加载
	 * properties键名：properties.dev.project.log4j.reloadable
	 */
	public static String getLOG4J_CONF_RELOADABLE(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.log4j.reloadable");
	}
	/**
	 * 决定log4j日志的文件路径
	 * properties键名：properties.dev.project.log4j.path
	 */
	public static String getLOG4J_PATH(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.log4j.path");
	}
	/**
	 * 决定重加载log4j配置的周期
	 * properties键名：properties.dev.project.log4j.reload.period
	 * @return String
	 */
	public static String getLOG4J_RELOAD_PERIOD(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.log4j.reload.period");
	}
	/**
	 * 确定svn协议
	 * properties键名：properties.dev.project.svn.protocol
	 * @return String
	 */
	public static String getSVN_PROTOCOL(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.svn.protocol");
	}
	/**
	 * 确定svn用户名
	 * properties键名：properties.dev.project.svn.auth.user
	 * @return String
	 */
	public static String getSVN_AUTH_USER(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.svn.auth.user");
	}
	/**
	 * 确定svn密码
	 * properties键名：properties.dev.project.svn.auth.pass
	 * @return String
	 */
	public static String getSVN_AUTH_PASS(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.svn.auth.pass");
	}
	/**
	 * 如果应用需要一个统一的线程池完成系统级任务可指定此线程池
	 * properties键名：properties.dev.project.threadpool.size
	 * @return int 系统线程池大小
	 * @see
	 */
	public static int getTHREAD_POOL_SIZE(){
		String size=CommonConstant.getPROPERTIES().getProperty("properties.dev.project.threadpool.size");
		int count=Help.isNotEmpty(size)?Integer.parseInt(size):0;
		return count>0?count*Runtime.getRuntime().availableProcessors():1;
	}
	public static int getIntConstant(String key, int defaultVal){
		String val=CommonConstant.getPROPERTIES().getProperty(key);
		if(Help.isEmpty(val)){
			return defaultVal;
		}else{
			return Integer.parseInt(val);
		}
	}
	public static long getLongConstant(String key, long defaultVal){
		String val=CommonConstant.getPROPERTIES().getProperty(key);
		if(Help.isEmpty(val)){
			return defaultVal;
		}else{
			return Long.parseLong(val);
		}
	}
	public static boolean getBooleanConstant(String key, boolean defaultVal){
		String val=CommonConstant.getPROPERTIES().getProperty(key);
		if(Help.isEmpty(val)){
			return defaultVal;
		}else{
			return Boolean.parseBoolean(val);
		}
	}
	public static String getStringConstant(String key, String defaultVal){
		String val=CommonConstant.getPROPERTIES().getProperty(key);
		if(Help.isEmpty(val)){
			return defaultVal;
		}else{
			return val;
		}
	}
	public static <E extends Enum> E getEnumConstant(String key, Class enumType, Enum<?> defaultVal){
		String val=CommonConstant.getPROPERTIES().getProperty(key);
		if(Help.isEmpty(val)){
			return (E)defaultVal;
		}else{
			return (E)Enum.valueOf(enumType, val);
		}
	}
	public static void loadConstant(Class constantClass) throws IllegalArgumentException, IllegalAccessException{
		clearBufferedValue(constantClass);
		loadConstant();
	}
	public static void clearBufferedValue(Class constantClass) throws IllegalArgumentException, IllegalAccessException{
		Field[] fs=constantClass.getDeclaredFields();
		for(int i=0,l=fs.length;i<l;i++){
			Field f=fs[i];
			Class ft=f.getType();
			if((f.getModifiers()&Modifier.FINAL)>0){
				continue;
			}
			if(ft.isPrimitive()){
				if(ft.equals(Boolean.TYPE)){
					f.setBoolean(null, false);
				}else if(ft.equals(Byte.TYPE)){
					f.set(null,(byte)0);
				}else if(ft.equals(Short.TYPE)){
					f.set(null,(short)0);
				}else{
					f.set(null, 0);
				}
			}else{
				f.set(null,null);
			}
		}
	}
	private static Properties loadConstantPrefixFile() throws IOException, URISyntaxException{
		Properties props=System.getProperties();
		try{
			props.putAll(Help.loadProperties(new File(CommonConstant.class.getResource("/"+CONSTANT_PREFIX_FILE).toURI())));
		}catch(Exception e){
		}finally{
			return props;
		}
	}
	private static String getConstantPrefix() throws IOException, URISyntaxException{
		return Help.convert(loadConstantPrefixFile().getProperty(CONSTANT_PREFIX),""); 
	}
	public static void loadConstant(){
		Properties properties=new Properties();
		try {
			String prefix=getConstantPrefix();
			String constantFileName=CONSTANT_PROPERTIES;
			if(Help.isNotEmpty(prefix)){
				constantFileName=prefix+'.'+constantFileName;
			}
			for(File props:new File(CommonConstant.class.getResource("/").toURI()).listFiles()){
				if(props.isFile() && props.getName().endsWith(constantFileName)){
					properties.putAll(Help.loadProperties(props, DEFAULT_CHARSET));
				}
			}
			PROPERTIES=properties;
		}catch(Exception e) {
			throw new ConstantLoadingException(e);
		}
		if(!loadScheduled){
			scheduledLoad();
		}
	}
	private static void scheduledLoad(){
		String periodDef=PROPERTIES.getProperty("properties.dev.reload.period");
		String timeUnit=PROPERTIES.getProperty("properties.dev.reload.period.timeunit");
		long delay=Help.isEmpty(periodDef)?60000:Long.parseLong(periodDef);
		if("1".equals(PROPERTIES.getProperty("properties.dev.reloadable"))){
			loadScheduled=true;
			final ScheduledExecutorService ses=Executors.newScheduledThreadPool(1);
			ses.scheduleWithFixedDelay(new Runnable(){
				public void run(){
					try{
						loadConstant();
						ses.shutdown();
					}catch(ConstantLoadingException e){
						log.error("",e);
					}
				}
			},delay,delay,Help.isEmpty(timeUnit)?TimeUnit.MILLISECONDS:TimeUnit.valueOf(timeUnit));
		}
	}
	public static void setPROPERTIES(Properties props){
		PROPERTIES=props;
	}
	static{
		loadConstant();
	}
}