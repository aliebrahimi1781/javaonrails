package me.jor.util;

import java.util.regex.Pattern;

public class RegexUtil {
	/**类c语法的注释正则表达式*/
	private static Pattern COMMENT;
	private static Pattern DIGIT;
	private static Pattern BASE64;
	
	private static Pattern getBase64(){
		if(BASE64==null){
			synchronized(RegexUtil.class){
				if(BASE64==null){
					BASE64=Pattern.compile("^[a-zA-Z0-9/\r\n+]{4,}={0,2}$");
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
	private static Pattern blankCharRegex;
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
			synchronized(Pattern.class){
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
}
