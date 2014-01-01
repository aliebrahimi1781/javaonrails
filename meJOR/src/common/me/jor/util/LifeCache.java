package me.jor.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LifeCache extends ConcurrentHashMap<Object,Object>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3625112969942243347L;
	private static final Map<String, LifeCache> sessionMap=new ConcurrentHashMap<String,LifeCache>();

	static{
		Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable(){
			public void run(){
				try{
					Iterator<Map.Entry<String,LifeCache>> itr=sessionMap.entrySet().iterator();
					while(itr.hasNext()){
						Map.Entry<String, LifeCache> entry=itr.next();
						LifeCache lc=entry.getValue();
						if(lc.life<=System.currentTimeMillis()-lc.birth){
							itr.remove();
						}
					}
				}catch(Exception e){}
			}
		},60*1000,60*1000,TimeUnit.MILLISECONDS);
	}
	
	private long life;
	private long birth;
	private LifeCache(){}
	
	public static LifeCache getSession(String name){
		LifeCache s=sessionMap.get(name);
		if(s==null){
			s=new LifeCache();
			sessionMap.put(name,s);
		}
		s.birth=System.currentTimeMillis();
		return s;
	}
	public static LifeCache getSession(String name, long life){
		LifeCache s=getSession(name);
		s.life=life;
		return s;
	}
	public static LifeCache remove(String name){
		return sessionMap.remove(name);
	}
}
