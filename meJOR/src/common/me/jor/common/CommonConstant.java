package me.jor.common;

import java.io.File;
import java.util.Properties;

import me.jor.exception.ConstantLoadingException;
import me.jor.util.Help;

/**
 * 在工程类路径的根下查找文件名以constant.properties结尾的文件<br/>
 * 加载其中的properties名值对作为常量定义保存在名为PROPERTIES的私有静态属性内<br/>
 * 可通过getProperties()方法获取<br/>
 * */
public final class CommonConstant {
	/**
	 * 常量属性文件后缀，所有在资源目录中以此结束的文件名都作为常量文件名加载。
	 * 常量key相同的以最后一次出现的为准
	 */
	public static final String CONSTANT_PROPERTIES="constant.properties";
	/**
	 * 以此为名在cookie中保存登录用户id
	 */
	public static final String USER_ID_HEADER="USER-ID-HEADER";
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

	private static final Properties PROPERTIES;
	
	public static Properties getPROPERTIES(){
		return PROPERTIES!=null?PROPERTIES:System.getProperties();
	}
	
	/**
	 * 返回值作为向浏览器推送的javascript函数名
	 * @return String
	 */
	public static String getIFRAMEFN(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.iframefn");
	}

	/**
	 * 决定log4j配置是否可在运行期重加载
	 */
	public static String getLOG4J_CONF_RELOADABLE(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.log4j.reloadable");
	}
	/**
	 * 决定log4j日志的文件路径
	 */
	public static String getLOG4J_PATH(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.log4j.path");
	}
	/**
	 * 决定重加载log4j配置的周期
	 * @return String
	 */
	public static String getLOG4J_RELOAD_PERIOD(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.log4j.reload.period");
	}
	/**
	 * 确定svn协议
	 * @return String
	 */
	public static String getSVN_PROTOCOL(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.svn.protocol");
	}
	/**
	 * 确定svn用户名
	 * @return String
	 */
	public static String getSVN_AUTH_USER(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.svn.auth.user");
	}
	/**
	 * 确定svn密码
	 * @return String
	 */
	public static String getSVN_AUTH_PASS(){
		return CommonConstant.getPROPERTIES().getProperty("properties.dev.project.svn.auth.pass");
	}
	/**
	 * 如果应用需要一个统一的线程池完成系统级任务可指定此线程池
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
	static{
		try {
			PROPERTIES=new Properties();
			for(File props:new File(CommonConstant.class.getResource("/").toURI()).listFiles()){
				if(props.isFile() && props.getName().endsWith(CONSTANT_PROPERTIES)){
					PROPERTIES.putAll(Help.loadProperties(props, DEFAULT_CHARSET));
				}
			}
		}catch(Exception e) {
			throw new ConstantLoadingException(e);
		}
	}
	
//	public static void main(String[] args) {
//		Properties p=System.getProperties();
//		System.out.println(Runtime.getRuntime().availableProcessors());
//		Map m=System.getenv();
//		for(Object e:m.entrySet()){
//			Map.Entry me=(Map.Entry)e;
//			System.out.println(me.getKey()+"  "+me.getValue());
//		}
//		for(Map.Entry e:p.entrySet()){
//			System.out.println(e.getKey()+"  "+e.getValue());
//		}
//		System.out.println(getTHREAD_POOL_SIZE());
//	}
}