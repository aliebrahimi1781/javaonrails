package me.jor.redis;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.jor.common.GlobalObject;
import me.jor.exception.RedisException;
import me.jor.util.Help;
import me.jor.util.RegexUtil;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JedisConnection implements RedisConnection{
	private boolean toThrowOnError;
	private ShardedJedisPool jedisPool;

	public boolean isToThrowOnError() {
		return toThrowOnError;
	}

	public void setToThrowOnError(boolean toThrowOnError) {
		this.toThrowOnError = toThrowOnError;
	}

	public ShardedJedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(ShardedJedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public ShardedJedis getResource(){
		return jedisPool.getResource();
	}
	
	@Override
	public void returnResource(ShardedJedis jedis){
		jedisPool.returnResource(jedis);
	}
	
	@Override
	public void returnBrokenResource(ShardedJedis jedis){
		jedisPool.returnBrokenResource(jedis);
	}
	
	@Override
	public <E> E execute(MethodHandle cmd, String key, Object... args){
		ShardedJedis jedis=null;
		boolean released=false;
		try{
			jedis=getResource();
			Object[] params=new Object[2+args.length];
			params[0]=jedis;
			params[1]=key;
			System.arraycopy(args, 0, params, 2, args.length);
			return (E)cmd.invokeWithArguments(params);
		}catch(Throwable e){
			//==========start
			released=true;
			returnBrokenResource(jedis);
			//==========end   如果出了异常或长时间阻塞就把这部分修改如下
//			jedis.disconnect();//调用jedisPool.returnBrokenResource(jedis)发生阻塞，替换成这一行
			if(this.toThrowOnError){
				throw new RedisException(e);
			}else{
				return null;
			}
		}finally{
			if(jedis!=null && !released){
				returnResource(jedis);
			}
		}
	}
	
	@Override
	public String generateKey(Object... parts) {
		return Help.concat(parts, ":");
	}
	
	@Override
	public String get(String key){
		return execute(JedisMethodHandles.get,key);
	}

	@Override
	public <E> E get(String key, Class<E> cls) {
		try {
			Map m=getMap(key);
			if(m.size()==0){
				return null;
			}
			Object r=cls.newInstance();
			for(Map.Entry entry:(Set<Map.Entry>)m.entrySet()){
				String fn=(String)entry.getKey();
				Field f=null;
				try{
					f=cls.getDeclaredField(fn);
					f.setAccessible(true);
					if(Help.isFinalOrStaticField(f)){
						continue;
					}
					String v=(String)entry.getValue();
					if(f.getType()==Date.class){
						f.set(r,new Date(Long.parseLong(v)));
					}else{
						Help.populate(r, fn, v);
					}
				}catch(Exception e){}
			}
			return (E)r;
		} catch (Exception e) {
			throw new RedisException(e);
		}
	}
	@Override
	public String getSet(String key){
		return execute(JedisMethodHandles.getSet,key);
	}
	@Override
	public Map<String, Object> getMap(String key) {
		return execute(JedisMethodHandles.hgetAll,key);
	}

	@Override
	public Map<String, String> getMap(String key, String... fields){
		List<String> list=hmget(key,fields);
		Map<String,String> m=null;
		if(list!=null){
			m=new HashMap<>();
			for(int i=0,l=list.size();i<l;i++){
				String v=list.get(i);
				if(Help.isNotEmpty(v)){
					m.put(fields[i], v);
				}
			}
		}
		return m;
	}

	@Override
	public String getString(String key) {
		return get(key);
	}
	@Override
	public Boolean getBoolean(String key){
		String v=get(key);
		if(Help.isEmpty(v)){
			return null;
		}else if("false".equalsIgnoreCase(v) || RegexUtil.matches(v, "^0?(\\.0*)?$")){
			return false;
		}else{
			return true;
		}
	}
	@Override
	public Long getLong(String key) {
		String v=get(key);
		return Help.isEmpty(v)?null:Long.parseLong(v);
	}

	@Override
	public Integer getInteger(String key) {
		String v=get(key);
		return Help.isEmpty(v)?null:Integer.parseInt(v);
	}
	@Override
	public BigDecimal getBigDecimal(String key){
		String v=get(key);
		return Help.isEmpty(v)?null:new BigDecimal(v);
	}
	@Override
	public Long hgetLong(String key,String field){
		String v=hget(key,field);
		return Help.isEmpty(v)?null:Long.parseLong(v);
	}
	@Override
	public Integer hgetInteger(String key,String field){
		String v=hget(key,field);
		return Help.isEmpty(v)?null:Integer.parseInt(v);
	}
	@Override
	public BigDecimal hgetBigDecimal(String key,String field){
		String v=hget(key,field);
		return Help.isEmpty(v)?null:new BigDecimal(v);
	}
	@Override
	public Long lindexLong(String key,long index){
		String v=lindex(key,index);
		return Help.isEmpty(v)?null:Long.parseLong(v);
	}
	@Override
	public Integer lindexInteger(String key,long index){
		String v=lindex(key,index);
		return Help.isEmpty(v)?null:Integer.parseInt(v);
	}
	@Override
	public BigDecimal lindexBigDecimal(String key,long index){
		String v=lindex(key,index);
		return Help.isEmpty(v)?null:new BigDecimal(v);
	}

	@Override
	public long inc(String key) {
		Long v=incr(key);
		return v==null?0:v;
	}

	@Override
	public long dec(String key) {
		Long v=decr(key);
		return v==null?0:v;
	}

	@Override
	public long incBy(String key, long value) {
		Long v=incrBy(key,value);
		return v==null?0:v;
	}

	@Override
	public long decBy(String key, long value) {
		Long v=decrBy(key,value);
		return v==null?0:v;
	}
	
	@Override
	public String set(String key, String value) {
		return execute(JedisMethodHandles.set,key,value);
	}

	@Override
	public Long set(String key, Map<String, String> value) {
		long r=0L;
		for(Map.Entry<String,String> e:value.entrySet()){
			Object v=e.getValue();
			if(v!=null){
				r&=this.set(key, e.getKey(),value2String(v));
			}
		}
		return r;
	}
	
	@Override
	public Long set(String key, String field, Object value){
		return hset(key,field,value!=null?value.toString():null);
	}

	@Override
	public String set(String key, Object value) {
		if(value instanceof String || value instanceof Number || value instanceof Boolean){
			return set(key,value.toString());
		}else if(value instanceof Map){
			Map map=(Map)value;
			Map mv=new HashMap();
			for(Map.Entry entry:(Set<Map.Entry>)map.entrySet()){
				Object v=entry.getValue();
				if(v!=null){
					mv.put(entry.getKey(), value2String(v));
				}
			}
			return String.valueOf(set(key,(Map)mv));
		}else{
			Class c=value.getClass();
			Field[] fs=c.getDeclaredFields();
			Map m=new HashMap();
			for(int i=0,l=fs.length;i<l;i++){
				Field f=fs[i];
				f.setAccessible(true);
				if(Help.isFinalOrStaticField(f)){
					continue;
				}
				try {
					Object v=f.get(value);
					if(v!=null){
						m.put(f.getName(), value2String(v));
					}
				} catch (Exception e) {}
			}
			return String.valueOf(set(key,m));
		}
	}
	@Override
	public String set(String key, Object value, String... fields) {
		if(Help.isEmpty(fields)){
			return "0";
		}
		if(value instanceof Map){
			Map map=(Map)value;
			Map mv=new HashMap();
			for(int i=0,l=fields.length;i<l;i++){
				String fn=fields[i];
				Object v=map.get(fn);
				if(v!=null){
					mv.put(fn, value2String(v));
				}
			}
			return Long.toString(set(key,mv));
		}
		Class c=value.getClass();
		Field[] fs=c.getDeclaredFields();
		Map m=new HashMap();
		if(fields!=null){
			for(int i=0,l=fields.length;i<l;i++){
				String fn=fields[i];
				try{
					Field f=c.getDeclaredField(fn);
					f.setAccessible(true);
					if(Help.isFinalOrStaticField(f)){
						continue;
					}
					Object v=f.get(value);
					if(v!=null){
						m.put(f.getName(), value2String(v));
					}
				}catch(Exception e){}
			}
		}
		return Long.toString(set(key,m));
	}

	@Override
	public Long append(String key, String append) {
		return execute(JedisMethodHandles.append,key,append);
	}

	@Override
	public Long decr(String key) {
		return execute(JedisMethodHandles.decr,key);
	}

	@Override
	public Long decrBy(String key, long value) {
		return execute(JedisMethodHandles.decrBy,key,value);
	}

	@Override
	public Boolean exists(String key) {
		return execute(JedisMethodHandles.exists,key);
	}

	@Override
	public Long expire(String key, int expire) {
		return execute(JedisMethodHandles.expire,key,expire);
	}
	@Override
	public Long expire(String key, long expire){
		return expire(key,(int)expire);
	}

	@Override
	public Long expireAt(String key, long expireAt) {
		return execute(JedisMethodHandles.expireAt,key,expireAt);
	}

	@Override
	public String getSet(String key, String value) {
		return execute(JedisMethodHandles.getSet,key,value);
	}

	@Override
	public Boolean getbit(String key, long offset) {
		return execute(JedisMethodHandles.getbit,key,offset);
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		return execute(JedisMethodHandles.getrange,key,startOffset,endOffset);
	}

	@Override
	public Long hdel(String key, String... fields) {
		return execute(JedisMethodHandles.hdel,key,fields);
	}

	@Override
	public Boolean hexists(String key, String field) {
		return execute(JedisMethodHandles.hexists,key,field);
	}

	@Override
	public String hget(String key, String field) {
		return execute(JedisMethodHandles.hget,key,field);
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		return execute(JedisMethodHandles.hgetAll,key);
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		return execute(JedisMethodHandles.hincrBy,key,field,value);
	}

	@Override
	public Set<String> hkeys(String key) {
		return execute(JedisMethodHandles.hkeys,key);
	}

	@Override
	public Long hlen(String key) {
		return execute(JedisMethodHandles.hlen,key);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return execute(JedisMethodHandles.hmget,key,fields);
	}
	@Override
	public <E> E hmget(String key, Class<E> cls,String... fields) throws Exception{
		List<String> list=hmget(key,fields);
		if(list==null){
			return null;
		}
		Object r=cls.newInstance();
		for(int i=0,l=list.size();i<l;i++){
			String fn=fields[i];
			Field f=null;
			try{
				f=cls.getDeclaredField(fn);
				f.setAccessible(true);
				String v=list.get(i);
				if(Help.isFinalOrStaticField(f) || v==null){
					continue;
				}
				if(f.getType()==Date.class){
					f.set(r,new Date(Long.parseLong(v)));
				}else{
					Help.populate(r, fn, v);
				}
			}catch(Exception e){}
		}
		return (E)r;
	}
	private String hmset0(String key, Map<String,String> hash){
		return execute(JedisMethodHandles.hmset,key,hash);
	}
	@Override
	public String hmset(String key, Map<String,Object> hash, String... fields){
		Map m=new HashMap();
		if(Help.isNotEmpty(fields)){
			for(int i=0,l=fields.length;i<l;i++){
				Object v=hash.get(fields[i]);
				if(v!=null){
					m.put(fields[i],value2String(v));
				}
			}
		}
		return hmset0(key,m);
	}
	@Override
	public String hmset(String key, Object hash, String... fields){
		if(hash instanceof Map){
			return hmset(key,(Map)hash,fields);
		}
		if(Help.isEmpty(fields)){
			return "0";
		}
		Map m=new HashMap();
		if(Help.isNotEmpty(fields)){
			Class c=hash.getClass();
			for(int i=0,l=fields.length;i<l;i++){
				try{
					Field f=c.getDeclaredField(fields[i]);
					f.setAccessible(true);
					Object v=f.get(hash);
					if(v!=null){
						m.put(fields[i],value2String(v));
					}
				}catch(Exception e){}
			}
		}
		return hmset0(key,m);
	}
	
	@Override
	public String hmset(String key, Map<String, String> hash) {
		Map mv=new HashMap();
		for(Map.Entry e:hash.entrySet()){
			Object v=e.getValue();
			if(v!=null){
				mv.put(e.getKey(), value2String(v));
			}
		}
		return hmset0(key,mv);
	}
	@Override
	public String hmset(String key, Object value){
		if(value instanceof Map){
			return hmset(key,(Map)value);
		}else{
			Class c=value.getClass();
			Field[] fs=c.getDeclaredFields();
			Map m=new HashMap();
			for(int i=0,l=fs.length;i<l;i++){
				Field f=fs[i];
				f.setAccessible(true);
				if(Help.isFinalOrStaticField(f)){
					continue;
				}
				try {
					Object v=f.get(value);
					if(v!=null){
						m.put(f.getName(), value2String(v));
					}
				} catch (Exception e) {}
			}
			return hmset0(key,m);
		}
	}

	@Override
	public Long hset(String key, String field, String value) {
		return execute(JedisMethodHandles.hset,key,field,value);
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		return execute(JedisMethodHandles.hsetnx,key,field,value);
	}

	@Override
	public List<String> hvals(String key) {
		return execute(JedisMethodHandles.hvals,key);
	}

	@Override
	public Long incr(String key) {
		return execute(JedisMethodHandles.incr,key);
	}

	@Override
	public Long incrBy(String key, long value) {
		return execute(JedisMethodHandles.incrBy,key,value);
	}

	@Override
	public String lindex(String key, long index) {
		return execute(JedisMethodHandles.lindex,key,index);
	}

	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
		return execute(JedisMethodHandles.lindex,key,where,pivot,value);
	}

	@Override
	public Long llen(String key) {
		return execute(JedisMethodHandles.llen,key);
	}

	@Override
	public String lpop(String key) {
		return execute(JedisMethodHandles.lpop,key);
	}

	@Override
	public Long lpush(String key, String... values) {
		return execute(JedisMethodHandles.lpush,key,values);
	}
	@Override
	public Long lpush(String key,Object... value){
		String[] vs=new String[value.length];
		for(int i=0,l=value.length;i<l;i++){
			vs[i]=value[i].toString();
		}
		return lpush(key,vs);
	}
	@Override
	public Long lpush(String key,Collection value){
		return lpush(key,value.toArray());
	}

	@Override
	public Long lpushx(String key, String value) {
		return execute(JedisMethodHandles.lpushx,key,value);
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		return execute(JedisMethodHandles.lrange,key,start,end);
	}
	@Override
	public List<String> lgetAll(String key){
		return lrange(key,0,this.llen(key));
	}

	@Override
	public Long lrem(String key, long count, String value) {
		return execute(JedisMethodHandles.lrem,key,count,value);
	}

	@Override
	public String lset(String key, long index, String value) {
		return execute(JedisMethodHandles.lset,key,index,value);
	}

	@Override
	public String ltrim(String key, long start, long end) {
		return execute(JedisMethodHandles.ltrim,key,start,end);
	}

	@Override
	public String rpop(String key) {
		return execute(JedisMethodHandles.rpop,key);
	}

	@Override
	public Long rpush(String key, String... values) {
		return execute(JedisMethodHandles.rpush,key,values);
	}

	@Override
	public Long rpushx(String key, String value) {
		return execute(JedisMethodHandles.rpushx,key,value);
	}

	@Override
	public Long sadd(String key, String... members) {
		return execute(JedisMethodHandles.sadd,key,members);
	}
	
	@Override
	public Long sadd(String key, Object... members){
		if(members==null){
			sadd(key,null);
		}
		String[] ms=new String[members.length];
		for(int i=0,l=members.length;i<l;i++){
			ms[i]=members[i].toString();
		}
		return sadd(key,ms);
	}

	@Override
	public Long scard(String key) {
		return execute(JedisMethodHandles.scard,key);
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		return execute(JedisMethodHandles.setbit,key,offset,value);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return execute(JedisMethodHandles.setex,key,seconds,value);
	}

	@Override
	public Long setnx(String key, String value) {
		return execute(JedisMethodHandles.setnx,key,value);
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		return execute(JedisMethodHandles.setrange,key,offset,value);
	}

	@Override
	public Boolean sismember(String key, String member) {
		return execute(JedisMethodHandles.sismember,key,member);
	}

	@Override
	public Set<String> smembers(String key) {
		return execute(JedisMethodHandles.smembers,key);
	}

	@Override
	public List<String> sort(String key) {
		return execute(JedisMethodHandles.sort,key);
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParams) {
		return execute(JedisMethodHandles.sortWITH_PARAMS,key,sortingParams);
	}

	@Override
	public String spop(String key) {
		return execute(JedisMethodHandles.spop,key);
	}

	@Override
	public String srandmember(String key) {
		return execute(JedisMethodHandles.srandmember,key);
	}

	@Override
	public Long srem(String key, String... members) {
		return execute(JedisMethodHandles.srem,key,members);
	}

	@Override
	public String substr(String key, int start, int end) {
		return execute(JedisMethodHandles.substr,key,start,end);
	}

	@Override
	public Long ttl(String key) {
		return execute(JedisMethodHandles.ttl,key);
	}

	@Override
	public String type(String key) {
		return execute(JedisMethodHandles.type,key);
	}

//	@Override
//	public Long zadd(String key, Map<String, Double> scoreMembers) {
//		return execute(JedisMethodHandles.zaddMULTI,key);
//	}
	@Override
	public Long zadd(String key, Map<Double, String> members) {
		return execute(JedisMethodHandles.zaddMULTI,key);
	}
	@Override
	public Long zadd(String key, double score, String member) {
		return execute(JedisMethodHandles.zadd,key,score,member);
	}

	@Override
	public Long zcard(String key) {
		return execute(JedisMethodHandles.zcard,key);
	}

	@Override
	public Long zcount(String key, double min, double max) {
		return execute(JedisMethodHandles.zcountDOUBLE,key,min,max);
	}

	@Override
	public Long zcount(String key, String min, String max) {
		return execute(JedisMethodHandles.zcountSTRING,key,min,max);
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		return execute(JedisMethodHandles.zincrby,key,score,member);
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		return execute(JedisMethodHandles.zrange,key,start,end);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		return execute(JedisMethodHandles.zrangeByScoreDOUBLE_MINMAX,key,min,max);
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		return execute(JedisMethodHandles.zrangeByScoreSTRING_MINMAX,key,min,max);
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		return execute(JedisMethodHandles.zrangeByScoreDOUBLE_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		return execute(JedisMethodHandles.zrangeByScoreSTRING_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min,double max) {
		return execute(JedisMethodHandles.zrangeByScoreWithScoresDOUBLE_MINMAX,key,min,max);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		return execute(JedisMethodHandles.zrangeByScoreWithScoresSTRING_MINMAX,key,min,max);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		return execute(JedisMethodHandles.zrangeByScoreWithScoresDOUBLE_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		return execute(JedisMethodHandles.zrangeByScoreWithScoresSTRING_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		return execute(JedisMethodHandles.zrangeWithScores,key,start,end);
	}

	@Override
	public Long zrank(String key, String member) {
		return execute(JedisMethodHandles.zrank,key,member);
	}

	@Override
	public Long zrem(String key, String... members) {
		return execute(JedisMethodHandles.zrem,key,members);
	}

	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		return execute(JedisMethodHandles.zremrangeByRank,key,start,end);
	}

	@Override
	public Long zremrangeByScore(String key, double min, double max) {
		return execute(JedisMethodHandles.zremrangeByScoreDOUBLE_MINMAX,key,min,max);
	}

	@Override
	public Long zremrangeByScore(String key, String min, String max) {
		return execute(JedisMethodHandles.zremrangeByScoreSTRING_MINMAX,key,min,max);
	}

	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		return execute(JedisMethodHandles.zrevrange,key,start,end);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double min, double max) {
		return execute(JedisMethodHandles.zrevrangeByScoreDOUBLE_MINMAX,key,min,max);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String min, String max) {
		return execute(JedisMethodHandles.zrevrangeByScoreSTRING_MINMAX,key,min,max);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double min, double max, int offset, int count) {
		return execute(JedisMethodHandles.zrevrangeByScoreDOUBLE_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String min, String max, int offset, int count) {
		return execute(JedisMethodHandles.zrevrangeByScoreSTRING_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double min, double max) {
		return execute(JedisMethodHandles.zrevrangeByScoreWithScoresDOUBLE_MINMAX,key,min,max);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String min, String max) {
		return execute(JedisMethodHandles.zrevrangeByScoreWithScoresSTRING_MINMAX,key,min,max);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		return execute(JedisMethodHandles.zrevrangeByScoreWithScoresDOUBLE_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		return execute(JedisMethodHandles.zrevrangeByScoreWithScoresSTRING_MINMAX_OFFSET_COUNT,key,min,max,offset,count);
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		return execute(JedisMethodHandles.zrevrangeWithScores,key,start,end);
	}

	@Override
	public Long zrevrank(String key, String member) {
		return execute(JedisMethodHandles.zrevrank,key,member);
	}

	@Override
	public Double zscore(String key, String member) {
		return execute(JedisMethodHandles.zscore,key,member);
	}

	@Override
	public String setex(String key,int seconds, Object value) {
		if(value instanceof String || value instanceof Number || value instanceof Boolean){
			return setex(key,seconds,value.toString());
		}else if(value instanceof Map){
			Map map=(Map)value;
			for(Map.Entry entry:(Set<Map.Entry>)map.entrySet()){
				entry.setValue(entry.getValue().toString());
			}
			String code=hmset(key,(Map)value);
			expire(key, seconds);
			return code;
		}else{
			Class c=value.getClass();
			Field[] fs=c.getDeclaredFields();
			Map m=new HashMap();
			for(int i=0,l=fs.length;i<l;i++){
				Field f=fs[i];
				f.setAccessible(true);
				if(Help.isFinalOrStaticField(f)){
					continue;
				}
				try {
					m.put(f.getName(), value2String(f.get(value)));
				} catch (Exception e) {}
			}
			String code=hmset(key,m);
			expire(key,seconds);
			return code;
		}
	}

	@Override
	public Long setnx(String key, Object value) {
		if(value instanceof String || value instanceof Number || value instanceof Boolean){
			return setnx(key,value.toString());
		}else if(value instanceof Map){
			Map map=(Map)value;
			Long code=0L;
			for(Map.Entry entry:(Set<Map.Entry>)map.entrySet()){
				code=hsetnx(key,entry.getKey().toString(),entry.getValue().toString());
			}
			return code;
		}else{
			Class c=value.getClass();
			Field[] fs=c.getDeclaredFields();
			Long code=0L;
			for(int i=0,l=fs.length;i<l;i++){
				Field f=fs[i];
				f.setAccessible(true);
				if(Help.isFinalOrStaticField(f)){
					continue;
				}
				try {
					code=hsetnx(key,f.getName(),value2String(f.get(value)));
				} catch (Exception e) {}
			}
			return code;
		}
	}
	private int compareTo(String v,Comparable toCompare){
		if(v==null && toCompare==null){
			return 0;
		}else if(v!=null && toCompare==null){
			return 1;
		}else if(v==null && toCompare!=null){
			return -1;
		}
		if(toCompare instanceof String){
			return v.compareTo((String)toCompare);
		}else if(toCompare instanceof Long){
			return Long.valueOf(v).compareTo((Long)toCompare);
		}else if(toCompare instanceof Integer){
			return Integer.valueOf(v).compareTo((Integer)toCompare);
		}else if(toCompare instanceof Short){
			return Short.valueOf(v).compareTo((Short)toCompare);
		}else if(toCompare instanceof Byte){
			return Byte.valueOf(v).compareTo((Byte)toCompare);
		}else if(toCompare instanceof Boolean){
			return Boolean.valueOf(v).compareTo((Boolean)toCompare);
		}else if(toCompare instanceof Character){
			return v.charAt(0)-((Character)toCompare).charValue();
		}else if(toCompare instanceof BigDecimal){
			return new BigDecimal(v).compareTo((BigDecimal)toCompare);
		}else if(toCompare instanceof BigInteger){
			return new BigInteger(v).compareTo((BigInteger)toCompare);
		}else if(toCompare instanceof Double){
			return new Double(v).compareTo((Double)toCompare);
		}else if(toCompare instanceof Float){
			return new Float(v).compareTo((Float)toCompare);
		}else{
			return v.compareTo(toCompare.toString());
		}
	}
	@Override
	public int compare(String key, String field, Comparable toCompare){
		return compareTo(hget(key,field),toCompare);
	}
	@Override
	public int compare(String key, long index, Comparable toCompare){
		return compareTo(lindex(key,index),toCompare);
	}
	@Override
	public int compare(String key, Comparable toCompare) {
		return compareTo(get(key),toCompare);
	}

	@Override
	public int hcompare(String key, String field, Comparable toCompare) {
		return compareTo(hget(key,field),toCompare);
	}

	@Override
	public int compare(String key, Class<Comparable> cls, Comparable toCompare) {
		Comparable v=get(key,cls);
		if(v==null && toCompare==null){
			return 0;
		}else if(v!=null && toCompare==null){
			return 1;
		}else if(v==null && toCompare!=null){
			return -1;
		}else{
			return get(key,cls).compareTo(toCompare);
		}
	}

	@Override
	public Long del(String key){
		return this.execute(JedisMethodHandles.del, key);
	}
	@Override
	public Long del(String... key) {
		int c=0;
		for(int i=0,l=key.length;i<l;i++){
			c+=del(key[i]).intValue();
		}
		return (long)c;
	}
	@Override
	public int del(List keyParts, String... prefix){
		String pre=this.generateKey(prefix);
		int c=0;
		for(int i=0,l=keyParts.size();i<l;i++){
			c+=del(this.generateKey(pre,keyParts.get(i))).intValue();
		}
		return c;
	}
	@Override
	public int del(Object[] keyParts, String... prefix){
		String pre=this.generateKey(prefix);
		int c=0;
		for(int i=0,l=keyParts.length;i<l;i++){
			c+=del(this.generateKey(pre,keyParts[i])).intValue();
		}
		return c;
	}

	@Override
	public void hsetnx(String key, Object value) {
		Class c=value.getClass();
		Field[] fs=c.getDeclaredFields();
		for(int i=0,l=fs.length;i<l;i++){
			Field f=fs[i];
			f.setAccessible(true);
			if(Help.isFinalOrStaticField(f)){
				continue;
			}
			try {
				hsetnx(key,f.getName(),value2String(f.get(value)));
			} catch (Exception e) {}
		}
	}
	@Override
	public void hsetnx(String key, Map<String,Object> value){
		for(Map.Entry<String,Object> e:value.entrySet()){
			hsetnx(e.getKey(),value2String(e.getValue()));
		}
	}
	@Override
	public void hsetnx(String key,Map<String,Object> value, String... fields){
		for(int i=0,l=fields.length;i<l;i++){
			hsetnx(key,fields[i],value2String(value.get(fields[i])));
		}
	}
	@Override
	public void hsetnx(String key, Object value, String... fields) {
		if(Help.isNotEmpty(fields)){
			Class c=value.getClass();
			for(int i=0,l=fields.length;i<l;i++){
				try {
					Field f=c.getDeclaredField(fields[i]);
					f.setAccessible(true);
					if(Help.isFinalOrStaticField(f)){
						continue;
					}
					hsetnx(key,f.getName(),value2String(f.get(value)));
				} catch (Exception e) {}
			}
		}
	}

	@Override
	public Long hsetnx(String key, String field, Object value) {
		return this.hsetnx(key, field, value2String(value));
	}

	@Override
	public Long hset(String key, String field, Object value) {
		return this.hset(key, field, value2String(value));
	}
	@Override
	public Long srem(String key, Object... members) {
		String[] ms=new String[members.length];
		for(int i=0,l=members.length;i<l;i++){
			ms[i]=members[i].toString();
		}
		return srem(key,ms);
	}
	private String value2String(Object v){
		if(v==null){
			return null;
		}
		return v instanceof Date?Long.toString(((Date)v).getTime()):v.toString();
	}
	

	@Override
	public String zindex(String key, long index) {
		Set<String> s=zrange(key,index,index);
		return s!=null?null:s.iterator().next();
	}

	@Override
	public int zindexInteger(String key, long index) {
		String v=zindex(key,index);
		return v!=null?Integer.parseInt(v):null;
	}

	@Override
	public long zindexLong(String key, long index) {
		String v=zindex(key,index);
		return v!=null?Long.parseLong(v):null;
	}

	@Override
	public String zfirst(String key) {
		return zindex(key,0);
	}

	@Override
	public Map<String, Object> getJsonMap(String key) throws JsonParseException, JsonMappingException, IOException {
		return getObjectFromJson(key,Map.class);
	}

	@Override
	public Map<String, Object> getJsonMap(String key, String... fields)throws JsonParseException, JsonMappingException, IOException {
		Map map=getJsonMap(key);
		if(map==null){
			return null;
		}else if(Help.isEmpty(fields)){
			return new HashMap<String,Object>();
		}else{
			Map<String,Object> result=new HashMap<>();
			for(int i=0,l=fields.length;i<l;i++){
				String k=fields[i];
				result.put(k, map.get(k));
			}
			return result;
		}
	}

	@Override
	public <E> E getObjectFromJson(String key, Class<E> cls) throws JsonParseException, JsonMappingException, IOException {
		String value=get(key);
		return value!=null?null:GlobalObject.getJsonMapper().readValue(value, cls);
	}

	@Override
	public String setJsonFromObject(String key, Object value) throws JsonProcessingException {
		if(value==null){
			String nullString=null;
			return set(key,nullString);
		}else{
			return set(key,GlobalObject.getJsonMapper().writeValueAsString(value));
		}
	}

//	@Override
//	public Long persist(String key) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Boolean setbit(String key, long offset, String value) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Long strlen(String key) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Long lpushx(String key, String... string) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Long rpushx(String key, String... string) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> blpop(String arg) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> brpop(String arg) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String echo(String string) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Long move(String key, int dbIndex) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Long bitcount(String key) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Long bitcount(String key, long start, long end) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ScanResult<String> sscan(String key, int cursor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ScanResult<Tuple> zscan(String key, int cursor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ScanResult<String> sscan(String key, String cursor) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ScanResult<Tuple> zscan(String key, String cursor) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
