package me.jor.roa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessParamGenerator {
	public static Map<String, Object> addMap(Map<String,Object> map, String name){
		Map<String, Object> m=new HashMap<String, Object>();
		map.put(name, m);
		return m;
	}
	public static Map<String, Object> addMap(List<Object> list){
		Map<String, Object> m=new HashMap<String, Object>();
		list.add(m);
		return m;
	}
	public static List<Object> addList(Map<String, Object> map, String name){
		List<Object> l=new ArrayList<Object>();
		map.put(name, l);
		return l;
	}
	public static List<Object> addList(List<Object> list){
		List<Object> l=new ArrayList<Object>();
		list.add(l);
		return l;
	}
	public static Map<String, Object> insertMap(List<Object> list, int index){
		Map<String, Object> value=new HashMap<String, Object>();
		insert(list,index,value);
		return value;
	}
	public static List<Object> insertList(List<Object> list, int index){
		List<Object> value=new ArrayList<Object>();
		insert(list,index,value);
		return value;
	}
//	public static Object remove(Map<String, Object> map, String name){
//		return map.remove(name);
//	}
//	public static Object remove(List<Object> list, int index){
//		return list.remove(index);
//	}
//	public static void addString(Map<String, Object> map, String name, String value){
//		map.put(name, value);
//	}
//	public static void addString(List<Object> list, String value){
//		list.add(value);
//	}
//	public static void addNumber(Map<String, Object> map, String name, Number value){
//		map.put(name, value);
//	}
//	public static void addNumber(List<Object> list, Number value){
//		list.add(value);
//	}
//	public static void addBool(Map<String, Object> map, String name, boolean value){
//		map.put(name, value);
//	}
//	public static void addBool(List<Object> list, boolean value){
//		list.add(value);
//	}
	public static void insertString(List<Object> list, int index, String value){
		insert(list,index,value);
	}
	public static void insertNumber(List<Object> list, int index, Number value){
		insert(list,index,value);
	}
	public static void insertBool(List<Object> list, int index, boolean value){
		insert(list,index,value);
	}
	
	private static void insert(List<Object> list, int index, Object value){
		for(int i=index,l=list.size();i<l;i++){
			Object cur=list.get(i);
			list.set(i, value);
			value=cur;
		}
		list.add(value);
	}
}
