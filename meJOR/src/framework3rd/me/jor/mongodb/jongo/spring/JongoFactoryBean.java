package me.jor.mongodb.jongo.spring;

import java.io.Closeable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import me.jor.util.Help;

import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.databind.MapperFeature;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

public class JongoFactoryBean implements FactoryBean<Jongo>,Closeable{
	
	private String address;
	private String database;
	private String username;
	private String password;
	private MapperFeature mapperFeature;
	
	public JongoFactoryBean(){}
	public JongoFactoryBean(String address, String database, String username, String password){
		this.address=address;
		this.database=database;
		this.username=username;
		this.password=password;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public MapperFeature getMapperFeature() {
		return mapperFeature;
	}
	public void setMapperFeature(MapperFeature mapperFeature) {
		this.mapperFeature = mapperFeature;
	}

	private List<ServerAddress> createServerAddressList() throws NumberFormatException, UnknownHostException{
		List<ServerAddress> addressList=new ArrayList<ServerAddress>();
		String[] parts=this.address.split(";");
		for(int i=0,l=parts.length;i<l;i++){
			String[] part=parts[i].split(":");
			addressList.add(new ServerAddress(part[0],Integer.parseInt(part[1])));
		}
		return addressList;
	}
	
	private Mongo mongo;
	private Mongo createMongo() throws NumberFormatException, UnknownHostException{
		return this.mongo=new Mongo(createServerAddressList());
	}
	private DB getDB() throws NumberFormatException, UnknownHostException{
		DB db=createMongo().getDB(Help.isEmpty(database)?"admin":database);
		char[] passwordChars=new char[password.length()];
		password.getChars(0, password.length(), passwordChars, 0);
		db.authenticate(username, passwordChars);
		return db;
	}
	private Mapper createJacksonMapper(){
		JacksonMapper.Builder builder=new JacksonMapper.Builder();
		if(mapperFeature!=null){
			builder.enable(mapperFeature);
		}
		return builder.build();
	}
	private Jongo jongo;
	@Override
	public Jongo getObject() throws Exception {
		if(jongo==null){
			synchronized(JongoFactoryBean.class){
				if(jongo==null){
					jongo=new Jongo(getDB(),createJacksonMapper());
				}
			}
		}
		return jongo;
	}
	@Override
	public Class<Jongo> getObjectType() {
		return Jongo.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void close(){
		this.mongo.close();
	}
}
