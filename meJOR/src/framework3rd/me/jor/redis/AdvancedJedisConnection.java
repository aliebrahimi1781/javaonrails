package me.jor.redis;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.util.Slowlog;

/**
 * 实现所有命令及pipeline
 * @author wujingrun
 *
 */
public class AdvancedJedisConnection implements RedisConnection{

	@Override
	public Boolean exists(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long persist(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String type(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expire(String key, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long ttl(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getbit(String key, long offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setrange(String key, long offset, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSet(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setnx(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setex(String key, int seconds, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decrBy(String key, long integer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long incrBy(String key, long integer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long incr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long append(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String substr(String key, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hdel(String key, String... field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hlen(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> hkeys(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> hvals(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long rpush(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpush(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long llen(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ltrim(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lindex(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lset(String key, long index, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lrem(String key, long count, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sadd(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> smembers(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long srem(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String spop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long scard(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean sismember(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String srandmember(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long strlen(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zadd(String key, double score, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zadd(String key, Map<Double, String> scoreMembers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrem(String key, String... member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrevrank(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcard(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double zscore(String key, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcount(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcount(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
			double min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min,
			double max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min,
			int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max,
			String min) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min,
			String max, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max,
			double min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max,
			String min, int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot,
			String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpushx(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long rpushx(String key, String... string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(String arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(String arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String echo(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long move(String key, int dbIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitcount(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitcount(String key, long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> sscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Tuple> zscan(String key, int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long del(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(String... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(String... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> keys(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> mget(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mset(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long msetnx(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rename(String oldkey, String newkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sdiff(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sinter(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sort(String key, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sunion(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String watch(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String unwatch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zinterstore(String dstkey, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zunionstore(String dstkey, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long publish(String channel, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String randomKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> scan(int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> configGet(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configSet(String parameter, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slowlogReset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long slowlogLen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Slowlog> slowlogGet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Slowlog> slowlogGet(long entries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long objectRefcount(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String objectEncoding(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long objectIdletime(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean scriptExists(String sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Boolean> scriptExists(String... sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String scriptLoad(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String quit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String flushDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long dbSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String select(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String flushAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String auth(String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bgsave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bgrewriteaof() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lastsave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String shutdown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String info(String section) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slaveof(String host, int port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slaveofNoOne() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String debug(DebugParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configResetStat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long waitReplicas(int replicas, long timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShardedJedis getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnResource(ShardedJedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnBrokenResource(ShardedJedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <E> E execute(MethodHandle cmd, String key, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long inc(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long dec(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Long del(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long incBy(String key, long value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long decBy(String key, long value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String generateKey(Object... parts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getBoolean(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getLong(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getInteger(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hgetLong(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer hgetInteger(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal hgetBigDecimal(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lindexLong(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer lindexInteger(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal lindexBigDecimal(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> E get(String key, Class<E> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getMap(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long set(String key, Map<String, String> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String set(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String set(String key, Object value, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String set(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long set(String key, String field, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setex(String key, int seconds, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setnx(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hsetnx(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long hsetnx(String key, String field, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hset(String key, String field, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sadd(String key, Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compare(String key, Comparable toCompare) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compare(String key, String field, Comparable toCompare) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compare(String key, long index, Comparable toCompare) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int hcompare(String key, String field, Comparable toCompare) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compare(String key, Class<Comparable> cls, Comparable toCompare) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int del(List keyParts, String... prefix) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int del(Object[] keyParts, String... prefix) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Long srem(String key, Object... members) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hmset(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getMap(String key, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> E hmget(String key, Class<E> cls, String... fields)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hmset(String key, Map<String, Object> hash, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hmset(String key, Object hash, String... fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hsetnx(String key, Object value, String... fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hsetnx(String key, Map<String, Object> value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hsetnx(String key, Map<String, Object> value, String... fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> lgetAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpush(String key, Object... value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpushx(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long rpushx(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpush(String key, Collection value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String zindex(String key, long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int zindexInteger(String key, long index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zindexLong(String key, long index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String zfirst(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
