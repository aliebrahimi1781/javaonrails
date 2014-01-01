package me.jor.alibaba.memcached;
import java.util.Date;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;
import me.jor.util.Help;


public class AliMemcached {
	private static ICacheManager<IMemcachedCache> manager;//可以直接设定为静态的单例

	public static void setUpBeforeClass() throws Exception
	{
		manager = CacheUtil.getCacheManager(IMemcachedCache.class,
			MemcachedCacheManager.class.getName());//manager初始化，可以通过配置来替换CacheManager实现
		manager.setConfigFile("memcached-client.xml");//设置Cache Client配置文件
		manager.setResponseStatInterval(5*1000);//设置Cache响应统计间隔时间，不设置则不进行统计
		manager.start();//Manager启动
	}

	public static void tearDownAfterClass() throws Exception
	{
		manager.stop();//manager结束
	}

	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		manager.getCache("test").put("data", new TestObject(new Date(),1));
		System.out.println(Help.toString(manager.getCache("test").get("data")));
		tearDownAfterClass();
	}
}
class TestObject{
	private Date d;
	private int i;
	public TestObject(Date d,int i){
		this.d=d;
		this.i=i;
	}
}