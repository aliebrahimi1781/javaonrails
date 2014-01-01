import redis.clients.jedis.Jedis;


public class JedisTest {
	public static void main(String[] args) {
		Jedis  redis = new Jedis ("192.168.1.51",6379);//连接redis
		System.out.println(redis.ping());
	}
}
