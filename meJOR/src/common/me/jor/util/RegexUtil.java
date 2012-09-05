package me.jor.util;

import java.util.regex.Pattern;

public class RegexUtil {
	/**类c语法的注释正则表达式*/
	private static Pattern commentRegex;
	private static Pattern digitRegex;
	
	private static Pattern getCommentRegex(){
		if(commentRegex==null){
			synchronized(RegexUtil.class){
				if(commentRegex==null){
					commentRegex=Pattern.compile("//.*|(/\\*([^(\\*/)]*))+(\\*/{1}?)");
				}
			}
		}
		return commentRegex;
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
		if(digitRegex==null){
			synchronized(Pattern.class){
				if(digitRegex==null){
					digitRegex=Pattern.compile("^(\\d*\\.)?\\d+|\\d+(\\.?\\d*)?$");
				}
			}
		}
		return digitRegex;
	}
	/**
	 * 判断src是否数字串
	 * @param src
	 * @return boolean
	 */
	public static boolean isDigit(String src){
		return getDigitRegex().matcher(src).matches();
	}
}
