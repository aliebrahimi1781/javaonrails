package me.jor.util;

import java.lang.ref.SoftReference;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.jor.util.policy.ClearByExecutorPolicy;
import me.jor.util.policy.ClearPolicy;

/**
 * 以SoftReference<String>做key的缓存类。此类的方法会创建String对象的软引用，用来保存缓存的值
 * 内部使用HashMap保存缓存内容
 * 实现SoftReference<String>比较器
 * null比非null大
 * null值相等
 * 忽略key字符串大小写
 * 内部维护着一个Cache<Cache<?>>对象；每次都使用Cache.getCache(String)获取缓存对象。
 * 随时会清除空的软引用
 * 虽然在比较时考虑到null值比较，但是缓存对象本身不接收null作key和value
 * <br/>
 * 此类内部维持一个缓存对象的实例：private static final SoftReference<Cache<Cache<?>>> cachecache=new SoftReference<Cache<Cache<?>>(new Cache<Cache<?>>())，
 * 用于缓存创建的所有缓存对象。<br/>
 * 缓存清除策略是全局的，所有缓存对象使用相同的缓存清除策略，一旦修改清除策略，所有缓存对象都会受到影响
 * */
public class Cache<K,E> {
	/**
	 * 软引用对象
	 */
	private static final StringSoftReference nullRef=new StringSoftReference(null);
	private static final String CACHECACHE_NAME="me.jor.util.Cache-cachecache".toLowerCase();
	/**
	 * 缓存对象
	 */
	private static SoftReference<Cache<String,Cache<?,?>>> cachecache=new SoftReference<Cache<String,Cache<?,?>>>(new Cache<String,Cache<?,?>>(CACHECACHE_NAME));
	private static <T> Cache<String,Cache<?,?>> getCachecache(){
		Cache<String,Cache<?,?>> cc=cachecache.get();
		if(cc==null){
			synchronized(Cache.class){
				cc=cachecache.get();
				if(cc==null){
					cc=new Cache<String,Cache<?,?>>(CACHECACHE_NAME);
					cachecache=new SoftReference<Cache<String,Cache<?,?>>>(cc);
				}
			}
		}
		return cc;
	}
	/**
	 * 重复锁对象
	 */
	private final ReadWriteLock lock=new ReentrantReadWriteLock();
	private static class HashAndEqualsSoftReference<G> extends SoftReference<G>{

		public HashAndEqualsSoftReference(G g) {
			super(g);
		}
		public int hashCode(){
			G g=super.get();
			return g!=null?g.hashCode():0;
		}
		public boolean equals(Object o){
			if(o!=null && o instanceof SoftReference){
				G g1=super.get();
				Object g2=((SoftReference)o).get();
				return g1!=null && g2!=null?g1.equals(g2):(g1==null && g2==null);
			}else{
				return false;
			}
		}
	}
	private static class StringSoftReference extends HashAndEqualsSoftReference<String> implements Comparable<SoftReference<String>>{
		public StringSoftReference(String key){
			super(key);
		}

		@Override
		public int compareTo(SoftReference<String> o) {
			return keycomparator.compare(this, o);
		}
	}
	private static final Comparator<SoftReference<String>> keycomparator=new Comparator<SoftReference<String>>(){
		@Override
		public int compare(SoftReference<String> s1, SoftReference<String> s2) {
			if(s1!=null && s2!=null){
				String k1=s1.get(), k2=s2.get();
				if(k1!=null && k2!=null){
					return k1.compareToIgnoreCase(k2);
				}else if(k1!=null && k2==null){
					return 1;
				}else if(k1==null && k2!=null){
					return -1;
				}else{
					return 0;
				}
			}else if(s1!=null && s2==null){
				return 1;
			}else if(s1==null && s2!=null){
				return -1;
			}else{
				return 0;
			}
		}
	};
	/**
	 * 缓存map
	 */
	private final Map<SoftReference<K>,SoftReference<E>> cache=new HashMap<SoftReference<K>,SoftReference<E>>();
	private static ClearPolicy<Cache<?,?>> clearPolicy;
	private String name;
	private Cache(String name){
		this.name=name;
	}
	/**
	 * 清除空的软引用对象
	 *  void
	 * @throws 
	 * @exception
	 */
	private void clearEmptyReference(){
		clearPolicy.clear(this);
	}
	
	private K pretreatKey(K key){
		return (K)(key instanceof String?((String)key).toLowerCase():key);
	}
	/**
	 * 向缓存添加新值，并返回相同key的原值
	 * 此操作会覆盖原值
	 * @param key 键
	 * @param value 值
	 * @return E 返回值类型
	 * @throws 
	 * @exception
	 */
	public E put(K key, E value){
		return put(new HashAndEqualsSoftReference(pretreatKey(key)), new HashAndEqualsSoftReference<E>(value));
	}
	private E put(SoftReference<K> srk, SoftReference<E> srv){
		try{
			lock.writeLock().lock();
			SoftReference<E> sre=cache.put(srk,srv);
			return sre==null?null:sre.get();
		}finally{
			clearEmptyReference();
			lock.writeLock().unlock();
		}
	}
	/**
	 * 向缓存添加新值
	 * 如果缓存中当前没有与指定键关联的值就使指定值与key关联并返回value
	 * 如果缓存中已有值与指定键关联就返回旧值，忽略value
	 * @param key 键
	 * @param value 值
	 * @return E 返回值类型
	 * @throws 
	 * @exception
	 */
	public E putIfAbsent(K key, E value){
		return putIfAbsent(new HashAndEqualsSoftReference(pretreatKey(key)),new HashAndEqualsSoftReference<E>(value));
	}
	/**
	 * 如果srv或srk包含一个空引用，则尽量不会执行插入，并返回原值,不过这一行为由于受到gc的影响，而不能控制gc时机，因此此行为不能得到保证。
	 * 依旧有可能会插入了空引用
	 * @param srk
	 * @param srv
	 * @return E
	 * @throws 
	 * @exception
	 */
	private E putIfAbsent(SoftReference<K> srk, SoftReference<E> srv){
		try{
			lock.writeLock().lock();
			SoftReference<E> sre=cache.get(srk);
			E e=sre==null?null:sre.get();
			E e2=srv.get();
			K k=srk.get();
			if(e==null && k!=null && e2!=null){
				cache.put(srk,srv);
				return e2;
			}
			return e;
		}finally{
			clearEmptyReference();
			lock.writeLock().unlock();
		}
	}
	/**
	 * 当缓存中已存在指定key时用value替换旧值，并返回旧值，否则返回null，且不执行替换
	 * 如果存在Cache.nullRef就清除空引用
	 * @param key 键
	 * @param value 值
	 * @return E 返回类型
	 * @throws 
	 * @exception
	 */
	public E replace(K key, E value){
		try{
			lock.writeLock().lock();
			clearEmptyReference();
			SoftReference<K> srk=new HashAndEqualsSoftReference(pretreatKey(key));
			SoftReference<E> sre=cache.get(srk);
			E e=sre==null?null:sre.get();
			if(e!=null){
				cache.put(srk,new HashAndEqualsSoftReference<E>(value));
				return e;
			}else{
				return null;
			}
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * 当缓存中已存在 指定key且cache.get(new SoftReference<String>(key)).equals(oldVal)==true时，才用newVal替换旧值
	 * 如果存在Cache.nullRef就清除空引用
	 * @param key 键 
	 * @param oldVal 旧值
	 * @param newVal 新值
	 * @return boolean 是否替换成功true:成功,false:失败
	 * @throws 
	 * @exception
	 */
	public boolean replace(K key, E oldVal, E newVal){
		try{
			lock.writeLock().lock();
			clearEmptyReference();
			SoftReference<K> srk=new HashAndEqualsSoftReference(pretreatKey(key));
			SoftReference<E> sre=cache.get(srk);
			E e=sre==null?null:sre.get();
			if(oldVal.equals(e)){
				cache.put(srk,new HashAndEqualsSoftReference<E>(newVal));
				return true;
			}else{
				return false;
			}
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * 从缓存移除key
	 * @param key 键名称
	 * @return E 返回类型
	 * @throws 
	 * @exception
	 */
	public E remove(K key){
		try{
			lock.writeLock().lock();
			clearEmptyReference();
			SoftReference<E> sre=cache.remove(new HashAndEqualsSoftReference(pretreatKey(key)));
			return sre==null?null:sre.get();
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * 当缓存中包含指定key，且cache.get(new SoftReference<String>(key)).equals(oldVal)==true时，才移除key
	 * @param key 需移除的key
	 * @param val 对应的值
	 * @return boolean 移除是否成功
	 * @throws 
	 * @exception
	 */
	public boolean remove(K key, E val){
		try{
			lock.writeLock().lock();
			clearEmptyReference();
			SoftReference<K> sr=new HashAndEqualsSoftReference(pretreatKey(key));
			SoftReference<E> sre=cache.get(sr);
			E e=sre==null?null:sre.get();
			if(val.equals(e)){
				cache.remove(sr);
				return true;
			}
			return false;
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * 返回与指定key关联的值
	 * @param key 指定key
	 * @return E 关联值
	 * @throws 
	 * @exception
	 */
	public E get(K key){
		try{
			lock.readLock().lock();
			clearEmptyReference();
			SoftReference<E> sre=cache.get(new HashAndEqualsSoftReference(pretreatKey(key)));
			return sre==null?null:sre.get();
		}finally{
			lock.readLock().unlock();
		}
	}
	/**
	 * 判断缓存内是否含有指定key，返回值只表示此方法返回前的状态，有可能方法返回时，缓存状态立即发生了变化 
	 * @param key 需判断的key值
	 * @return boolean 是否包含
	 * @throws 
	 * @exception
	 */
	public boolean contains(K key){
		try{
			lock.readLock().lock();
			clearEmptyReference();
			return cache.containsKey(new HashAndEqualsSoftReference(pretreatKey(key)));
		}finally{
			lock.readLock().unlock();
		}
	}
	public void clear(){
		try{
			lock.writeLock().lock();
			cache.clear();
		}finally{
			lock.writeLock().unlock();
		}
	}
	/**
	 * 返回缓存大小
	 * @return int 缓存容量
	 * @throws 
	 * @exception
	 */
	public int size(){
		try{
			lock.readLock().lock();
			clearEmptyReference();
			return cache.size();
		}finally{
			lock.readLock().unlock();
		}
	}
	/**
	 * 传入缓存名，返回一个缓存对象。
	 * 如果与此缓存名关联的缓存对象已经存在就返回这个对象。
	 * 如果没有与此缓存名关联的缓存对象，就创建一个新的缓存对象并将此添加到缓存池中。
	 * 如果一个名字关联的缓存对象长时间不用，在内存紧张时可能会被gc。下次再试图获取此名字关联的缓存对象时，将会创建一个新的缓存对象。<br/>
	 * 采用默认的Cache.ClearInCurrentThreadPolicy清除空引用
	 * @param <T>
	 * @param cachename
	 * @return Cache<T>
	 * @throws 
	 * @exception
	 */
	@SuppressWarnings("unchecked")
	public static <K,T> Cache<K,T> getCache(String cachename){
		cachename=cachename.toLowerCase();
		Cache<String,Cache<?,?>> cc=getCachecache();
		Cache<K,T> c=(Cache<K,T>)cc.get(cachename);
		if(c==null){
			synchronized(cc){
				c=(Cache<K,T>)cc.get(cachename);
				if(c==null){
					c=new Cache<K,T>(cachename);
					cc.put(cachename, c);
				}
			}
		}
		return c;
	}
	public static abstract class AbstractCommonCacheClearPolicy<K,E> implements ClearPolicy<Cache<K,E>>{
		private Set<String> nameSet=new HashSet<String>();
		private boolean putIfAbsent(String name){
			synchronized(nameSet){
				if(!nameSet.contains(name)){
					nameSet.add(name);
					return true;
				}else{
					return false;
				}
			}
		}
		private void remove(String name){
			synchronized(nameSet){
				nameSet.remove(name);
			}
		}
		@Override
		public void clear(Cache<K,E> cache) {
			String name=cache.name;
			if(putIfAbsent(name)){
				innerClear(cache);
				remove(name);
			}
		}
		protected abstract void innerClear(Cache<K,E> cache);
	}
	/**
	 * 清除空引用的实际算法
	 * @param <E> 缓存对象内保存的对象范型类型
	 */
	public static class ClearInCurrentThreadPolicy<K,E> extends AbstractCommonCacheClearPolicy<K,E>{
		@Override
		protected void innerClear(Cache<K,E> cache){
			Map<SoftReference<K>, SoftReference<E>> map=cache.cache;
			while(remove(map, cache)!=null);
		}
		private Object remove(Map<SoftReference<K>,SoftReference<E>> map, Cache cache){
			synchronized(cache){
				return map.remove(nullRef);
			}
		}
	}
	/**
	 * 发现空引用时清除所有缓存对象
	 * @param <E> 缓存的对象类型
	 */
	public static class ClearAllOnNullFoundPolicy<K,E> extends AbstractCommonCacheClearPolicy<K,E>{
		protected void innerClear(Cache<K,E> cache){
			Map<SoftReference<K>,SoftReference<E>> map=cache.cache;
			if(map.containsKey(nullRef) || map.containsValue(nullRef)){
				clear(map,cache);
			}
		}
		private void clear(Map<SoftReference<K>,SoftReference<E>> map, Cache cache){
			synchronized(cache){
				map.clear();
			}
		}
	}
	/**
	 * 变更清除Cache内部缓存对象(cachecache)的空引用策略
	 * @param clearPolicy 清除内部缓存对象的空引用策略
	 */
	public static void changeClearPolicy(ClearPolicy<Cache<?,?>> clearPolicy){
		Cache.clearPolicy=clearPolicy;
	}
	/**
	 * 使用线程池清除Cache类内部缓存对象(cachecache)的策略
	 * @param clearExecutor 执行清除空引用策略的线程池
	 */
	public static void changeClearPolicy(Executor clearExecutor){
		changeClearPolicy(new ClearByExecutorPolicy<Cache<?,?>>(clearExecutor, new ClearInCurrentThreadPolicy()));
	}
	static{
		changeClearPolicy(new ClearInCurrentThreadPolicy());
	}
}
