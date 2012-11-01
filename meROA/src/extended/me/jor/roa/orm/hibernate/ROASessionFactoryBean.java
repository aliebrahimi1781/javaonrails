package me.jor.roa.orm.hibernate;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import me.jor.roa.core.ResourceAccessHandler;
import me.jor.roa.exception.ResourceAccessClientException;
import me.jor.util.Help;

import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

public class ROASessionFactoryBean extends LocalSessionFactoryBean{

	private Map<String,String> param;
	private String dataSourceName;
	private String resource;
	@Override
	public void afterPropertiesSet(){
		try {
			super.setDataSource(retriveDataSource());
			super.afterPropertiesSet();
		} catch (Exception e) {
			throw new ResourceAccessClientException(e);
		}
	}
	/**
	 * does nothing, just override super implementation
	 * DataSource instance will be retrived from DataSourceResource
	 * @exception
	 * @param dataSource
	 * @see org.springframework.orm.hibernate3.AbstractSessionFactoryBean#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource){}
	
	private DataSource retriveDataSource() throws Exception{
		if(Help.isNotEmpty(dataSourceName)){
			if(param==null){
				param=new HashMap<String,String>();
				param.put("name", dataSourceName);
			}
		}
		return (DataSource)ResourceAccessHandler.handle(resource, param,false);
	}
	public Map<String, String> getParam() {
		return param;
	}
	public void setParam(Map<String, String> param) {
		this.param = param;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
}
