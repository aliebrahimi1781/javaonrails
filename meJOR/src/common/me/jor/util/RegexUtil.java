package me.jor.util;

import java.util.regex.Pattern;

public class RegexUtil {
	/**类c语法的注释正则表达式*/
	private static volatile Pattern COMMENT;
	private static volatile Pattern DIGIT;
	private static volatile Pattern BASE64;
	private static volatile Pattern blankCharRegex;
	private static volatile Pattern IP4;
	
	private static Pattern getBase64(){
		if(BASE64==null){
			synchronized(RegexUtil.class){
				if(BASE64==null){
					BASE64=Pattern.compile("^[a-zA-Z0-9/+]{2,}={0,2}$");
				}
			}
		}
		return BASE64;
	}
	
	private static Pattern getCommentRegex(){
		if(COMMENT==null){
			synchronized(RegexUtil.class){
				if(COMMENT==null){
					COMMENT=Pattern.compile("//.*|(/\\*([^(\\*/)]*))+(\\*/{1}?)");
				}
			}
		}
		return COMMENT;
	}
	/**
	 * 删除文本中的所有类c语法注释内容
	 * */
	public static String removeComments(String code){
		return getCommentRegex().matcher(code).replaceAll("");
	}
	
	private static Pattern getBlankCharRegex(){
		if(blankCharRegex==null){
			synchronized(RegexUtil.class){
				if(blankCharRegex==null){
					blankCharRegex=Pattern.compile("\\s+");
				}
			}
		}
		return blankCharRegex;
	}
	/**
	 * 把txt中所有空白字符替换成target
	 * @param txt
	 * @param target
	 * @return String
	 * @see
	 */
	public static String replaceAllBlanks(String txt, String target){
		return getBlankCharRegex().matcher(txt).replaceAll(target);
	}
	
	private static Pattern getDigitRegex(){
		if(DIGIT==null){
			synchronized(RegexUtil.class){
				if(DIGIT==null){
					DIGIT=Pattern.compile("^(\\d*\\.)?\\d+|\\d+(\\.?\\d*)?$");
				}
			}
		}
		return DIGIT;
	}
	/**
	 * 判断src是否数字串
	 * @param src
	 * @return boolean
	 */
	public static boolean isDigit(String src){
		return getDigitRegex().matcher(src).matches();
	}
	
	public static boolean isBase64(String src){
		return getBase64().matcher(src).matches();
	}
	
	private static Pattern getIp4Regex(){
		if(IP4==null){
			synchronized(Pattern.class){
				if(IP4==null){
					IP4=Pattern.compile("^(((00)?\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.){3}((00)?\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$");
				}
			}
		}
		return IP4;
	}
	public static boolean isIp4(String src){
		return getIp4Regex().matcher(src).matches();
	}
}
