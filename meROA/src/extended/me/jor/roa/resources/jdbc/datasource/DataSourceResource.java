package me.jor.roa.resources.jdbc.datasource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;

import javax.sql.DataSource;

import me.jor.roa.core.ResourceAccessContext;
import me.jor.roa.core.accessable.Retrivable;
import me.jor.util.Help;
import me.jor.util.LockCache;
/**
 * access data is like below
 * {
 *    "name":"xxxxx",
 *    "url":"jdbc:db2://197.0.3.44:60004/MIBSSE",
 *    "username":"username",
 *    "password":"password"
 * } or
 * {
 *    "name":"xxxxx"//the value is a datasource name
 * } or a simple string as a datasource name
 * @author Administrator
 *
 */
public class DataSourceResource implements Retrivable{

	private Properties props;
	private Class dataSourceClass;
	private Map<String, DataSource> dataSourceMap;
	/**
	 * key是DataSource对象的名字，依据名字获取相应的DataSource对象
	 * 值是一个如下格式的map
	 * {
	 *    "name":"xxxx",//to get datasource as this key. if it was omit, the will be generated like "url?username:password"
	 *    "url":"jdbc:db2://197.0.3.44:60004/MIBSSE",
	 *    "username":"username",
	 *    "password":"password"
	 * }
	 */
	private Map<String,Map<String,String>> urlMap;
	
	public DataSourceResource(){
		dataSourceMap=new HashMap();
	}
	
	@Override
	public Object retrive(ResourceAccessContext context) throws Exception {
		Object param=context.getAccessParam();
		DataSource dataSource=null;
		if(param instanceof String){
			dataSource=dataSourceMap.get((String)param);
		}else{
			Map<String, String> map=(Map<String,String>)param;
			if(map.size()==1){
				dataSource=dataSourceMap.get(map.get("name"));
			}else{
				dataSource=get(map);
			}
		}
		return dataSource;
	}
	private DataSource get(Map<String,String> param) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		String name=param.get("map");
		String url=param.get("url");
		String username=param.get("username");
		String password=param.get("password");
		if(Help.isEmpty(name)){
			name=new StringBuilder(url).append('?').append(username).append(':').append(password).toString();
		}
		DataSource dataSource=dataSourceMap.get(name);
		if(dataSource==null){
			Lock lock=LockCache.getReentrantLock(name);
			try{
				lock.lock();
				dataSource=dataSourceMap.get(name);
				if(dataSource==null){
					dataSource=create(url,username,password);
					dataSourceMap.put(name, dataSource);
				}
			}finally{
				lock.unlock();
			}
		}
		return dataSource;
	}

	public void init() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		for(Map.Entry<String, Map<String,String>> entry:urlMap.entrySet()){
			Map<String,String> v=entry.getValue();
			dataSourceMap.put(entry.getKey(), create(v.get("url"),v.get("username"),v.get("password")));
		}
		urlMap=null;
	}
	
	public DataSource create(String url,String username, String password) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		DataSource dataSource=(DataSource)dataSourceClass.newInstance();
		Method[] ms=dataSourceClass.getMethods();
		for(int i=0,l=ms.length;i<l;i++){
			Method m=ms[i];
			String mn=m.getName();
			if(mn.startsWith("set")){
				String pn=mn.replaceFirst("^set", "");
				String lowerpn=pn.toLowerCase();
				if(lowerpn.indexOf("url")>=0){
					m.invoke(dataSource, url);
				}else if(lowerpn.indexOf("user")>=0){
					m.invoke(dataSource, username);
				}else if(lowerpn.indexOf("pass")>=0){
					m.invoke(dataSource, password);
				}else{
					String v=props.getProperty(pn);
					if(Help.isNotEmpty(v)){
						m.invoke(dataSource, Help.parse(m.getParameterTypes()[0], v));
					}
				}
			}
		}
		return dataSource;
	}
	
	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public Class getDataSourceClass() {
		return dataSourceClass;
	}

	public void setDataSourceClass(Class dataSourceClass) {
		this.dataSourceClass = dataSourceClass;
	}

	@Override
	public Object getDescription() {
		return dataSourceMap.keySet();
	}

}
