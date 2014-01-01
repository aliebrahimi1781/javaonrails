package me.jor.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.util.Hashing;

public class ShardedJedisPool extends ShardedPool<JedisShardInfo,Jedis> {
    public ShardedJedisPool(List<JedisShardInfo> shards,final GenericKeyedObjectPoolConfig poolConfig) {
    	this(shards, Hashing.MURMUR_HASH,poolConfig);
    }

    public ShardedJedisPool(List<JedisShardInfo> shards, Hashing algo,final GenericKeyedObjectPoolConfig poolConfig) {
    	this(shards, algo, null,poolConfig);
    }

    public ShardedJedisPool(List<JedisShardInfo> shards, Pattern keyTagPattern,final GenericKeyedObjectPoolConfig poolConfig) {
    	this(shards, Hashing.MURMUR_HASH, keyTagPattern,poolConfig);
    }

    public ShardedJedisPool(List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern,final GenericKeyedObjectPoolConfig poolConfig) {
    	super(shards,algo,keyTagPattern,poolConfig, new ShardedJedisFactory());
    }

    public PipelinedShardedJedisPool pipelined(){
    	return this.new PipelinedShardedJedisPool();
    }
    private class PipelineJedisPair{
    	public Pipeline pipeline;
    	public Jedis jedis;
    	public PipelineJedisPair(Pipeline pipeline,Jedis jedis){
    		this.pipeline=pipeline;
    		this.jedis=jedis;
    	}
    }
    public class PipelinedShardedJedisPool{
    	private Map<String,JedisShardInfo> shardMap=new HashMap<>();
    	private Map<JedisShardInfo,PipelineJedisPair> resourceMap=new HashMap<>();
    	private JedisShardInfo getShardInfo(String key){
    		JedisShardInfo jsi=shardMap.get(key);
    		if(jsi==null){
    			jsi=ShardedJedisPool.this.getShardInfo(key);
    			shardMap.put(key, jsi);
    		}
    		return jsi;
    	}
    	public Pipeline getResource(String key){
    		JedisShardInfo jsi=getShardInfo(key);
    		PipelineJedisPair pair=this.resourceMap.get(jsi);
    		if(pair==null){
    			Jedis jedis=ShardedJedisPool.this.getResource(jsi);
    			shardMap.put(key, jsi);
    			pair=new PipelineJedisPair(jedis.pipelined(),jedis);
    			resourceMap.put(jsi, pair);
    		}
    		return pair.pipeline;
    	}
    	public void sync(){
    		for(Map.Entry<JedisShardInfo, PipelineJedisPair> entry:resourceMap.entrySet()){
    			try{
	    			PipelineJedisPair pair=entry.getValue();
	    			pair.pipeline.sync();
	    			JedisShardInfo jsi=entry.getKey();
	    			ShardedJedisPool.this.returnResource(jsi,pair.jedis);
    			}catch(Exception e){}
    		}
    	}
    	public void syncWhileError(){
    		for(Map.Entry<JedisShardInfo, PipelineJedisPair> entry:resourceMap.entrySet()){
    			try{
	    			PipelineJedisPair pair=entry.getValue();
	    			pair.pipeline.sync();
	    			JedisShardInfo jsi=entry.getKey();
	    			ShardedJedisPool.this.returnBrokenResource(jsi,pair.jedis);
    			}catch(Exception e){}
    		}
    	}
    	public void sync(Pipeline... pipelines){
    		for(Map.Entry<JedisShardInfo, PipelineJedisPair> entry:resourceMap.entrySet()){
    			try{
	    			PipelineJedisPair pair=entry.getValue();
	    			for(int i=0,l=pipelines.length;i<l;i++){
	    				if(pair.pipeline==pipelines[i]){
	    					pair.pipeline.sync();
	    	    			JedisShardInfo jsi=entry.getKey();
	    	    			ShardedJedisPool.this.returnResource(jsi,pair.jedis);
	    				}
	    			}
    			}catch(Exception e){}
    		}
    	}
    	public void syncWhileError(Pipeline... pipelines){
    		for(Map.Entry<JedisShardInfo, PipelineJedisPair> entry:resourceMap.entrySet()){
    			try{
	    			PipelineJedisPair pair=entry.getValue();
	    			for(int i=0,l=pipelines.length;i<l;i++){
	    				if(pair.pipeline==pipelines[i]){
	    					pair.pipeline.sync();
	    	    			JedisShardInfo jsi=entry.getKey();
	    	    			ShardedJedisPool.this.returnBrokenResource(jsi,pair.jedis);
	    				}
	    			}
    			}catch(Exception e){}
    		}
    	}
    }
    
    /**
     * PoolableObjectFactory custom impl.
     */
    private static class ShardedJedisFactory implements KeyedPooledObjectFactory<JedisShardInfo,Jedis>{
		@Override
		public void activateObject(JedisShardInfo key, PooledObject<Jedis> pooledShardedJedis)throws Exception {}
		@Override
		public void destroyObject(JedisShardInfo key, PooledObject<Jedis> pooledShardedJedis)throws Exception {
			final Jedis jedis = pooledShardedJedis.getObject();
		    try {
			    try {
			    	jedis.quit();
			    } catch (Exception e) {}
			    jedis.disconnect();
			} catch (Exception e) {}
		}
		@Override
		public PooledObject<Jedis> makeObject(JedisShardInfo key)throws Exception {
		    return new DefaultPooledObject<Jedis>(key.createResource());
		}
		@Override
		public void passivateObject(JedisShardInfo key,PooledObject<Jedis> pooledShardedJedis) throws Exception {}
		@Override
		public boolean validateObject(JedisShardInfo key,PooledObject<Jedis> pooledShardedJedis) {
			try {
				Jedis jedis = pooledShardedJedis.getObject();
				return jedis.ping().equals("PONG");
			} catch (Exception ex) {
				return false;
			}
		}
    }
}