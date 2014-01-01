package me.jor.mongodb.jongo.spring;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.FactoryBean;

import com.mongodb.WriteConcern;

public class MongoCollectionFactoryBean implements FactoryBean<MongoCollection>{

	private String collection;
	private Jongo jongo;
	private WriteConcern writeConcern;
	private MongoCollection mongoCollection;
	public MongoCollectionFactoryBean(){}
	public MongoCollectionFactoryBean(Jongo jongo, String collection) {
		this.jongo=jongo;
		this.collection=collection;
	}
	public MongoCollectionFactoryBean(Jongo jongo, String collection, String writeConcern){
		this.jongo=jongo;
		this.collection=collection;
		this.writeConcern=WriteConcern.valueOf(writeConcern);
	}
	public MongoCollectionFactoryBean(Jongo jongo, String collection, int wtimeout, boolean fsync, boolean j){
		this.jongo=jongo;
		this.collection=collection;
		this.writeConcern=WriteConcern.majorityWriteConcern(wtimeout, fsync, j);
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public Jongo getJongo() {
		return jongo;
	}
	public void setJongo(Jongo jongo) {
		this.jongo = jongo;
	}
	public WriteConcern getWriteConcern() {
		return writeConcern;
	}
	public void setWriteConcern(WriteConcern writeConcern) {
		this.writeConcern = writeConcern;
	}

	
	@Override
	public MongoCollection getObject() throws Exception {
		if(mongoCollection==null){
			synchronized(this){
				if(mongoCollection==null){
					MongoCollection mongoCollection=jongo.getCollection(collection);
					this.mongoCollection=writeConcern==null?mongoCollection:mongoCollection.withConcern(writeConcern);
				}
			}
		}
		return mongoCollection;
	}
	@Override
	public Class<MongoCollection> getObjectType() {
		return MongoCollection.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
}
