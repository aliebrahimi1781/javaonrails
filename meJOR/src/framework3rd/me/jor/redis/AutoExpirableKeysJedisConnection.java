package me.jor.redis;

/**
 * 通过pipeline执行expire命令。超时时间作为每个命令方法的参数传入
 * @author wujingrun
 *
 */
public class AutoExpirableKeysJedisConnection extends AdvancedJedisConnection implements AutoExpirableKeysRedisConnection{

}
