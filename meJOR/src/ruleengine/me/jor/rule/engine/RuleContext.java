package me.jor.rule.engine;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class RuleContext {
	private static Map<String, RuleGroup> rules=new HashMap<String,RuleGroup>();
	
	public static RuleGroup get(String name){
		return rules.get(name);
	}
	public static boolean charge(String name, Object o){
		return get(name).charge(o);
	}
	
	public static enum ComparationDescriptor{
		GT,LT,GTE,LTE,EQ,NEQ,EMPTY,NOT_EMPTY,CONTAINS,NOT_CONTAINS,STARTS_WITH,ENDS_WITH,MATCHES_REGEX;
	}
	public static Rule create(Class cls, String fieldName, Object value, String descriptor){
		return create(cls,fieldName,value,ComparationDescriptor.valueOf(descriptor));
	}
	public static Rule create(Class cls, String fieldName, Object value, ComparationDescriptor descriptor){
		
		return null;
	}
	public static Rule create(Class cls, String filedName, Object value, int at){
		
		return null;
	}
	public static RuleGroup next(String name, Rule rule){
		return null;
	}
	public static RuleGroup before(String name, Rule rule){
		return null;
	}
	
	public static RuleGroup read(String path){
		return null;
	}
	public static RuleGroup read(File path){
		return null;
	}
	public static RuleGroup read(InputStream in){
		return null;
	}
	public static RuleGroup read(Reader reader){
		return null;
	}
	public static void write(RuleGroup group,String path){
		
	}
	public static void write(RuleGroup group, File path){
		
	}
	public static void write(RuleGroup group, OutputStream out){
		
	}
	public static void write(RuleGroup group, Writer writer){
		
	}
}
