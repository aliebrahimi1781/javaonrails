package me.jor.roa.common.constant;

import java.math.BigInteger;

import me.jor.common.CommonConstant;
import me.jor.util.Help;

public class ROAConstant {
	public static final String LOG_TOKEN_NAME="LOG_TOKEN";
	public static final BigInteger MAX_INT=new BigInteger(Integer.MAX_VALUE+"");
	public static final BigInteger MIN_INT=new BigInteger(Integer.MIN_VALUE+"");
	public static final BigInteger MAX_LONG=new BigInteger(Long.MAX_VALUE+"");
	public static final BigInteger MIN_LONG=new BigInteger(Long.MIN_VALUE+"");
	public static final String ROA_CONF_PARSER_DEF="/me/ROAConfParserDef";
	
	private static String defaultDataType;
	private static String defaultErrorType;
	
	public static final String resourcesPath="resources";
	
	public static String getDefaultDataType(){
		if(defaultDataType==null){
			synchronized(ROAConstant.class){
				if(defaultDataType==null){
					defaultDataType=CommonConstant.getPROPERTIES().getProperty("me.jor.roa.DefaultDataType");
					if(Help.isEmpty(defaultDataType)){
						defaultDataType="json";
					}
				}
			}
		}
		return defaultDataType;
	}

	public static String getDefaultErrorType() {
		if(defaultErrorType==null){
			synchronized(ROAConstant.class){
				if(defaultErrorType==null){
					defaultErrorType=CommonConstant.getPROPERTIES().getProperty("me.jor.roa.DefaultErrorType");
					if(Help.isEmpty(defaultErrorType)){
						defaultErrorType="json";
					}
				}
			}
		}
		return defaultErrorType;
	}
	public static boolean loadResOnStartup(){
		return CommonConstant.getBooleanConstant("me.jor.roa.loadresonstartup", true);
	}
}
