package me.jor.util;

import java.util.regex.Pattern;

public class UnicodeCodec {
	private static final Pattern splitor=RegexUtil.getRegex(";");
	private static final Pattern codeStarter=Pattern.compile("&#x");
	//&#x60a8;&#x8d2d;&#x4e70;&#x7684;&#x5305;&#x6708;&#x8fd8;&#x6ca1;&#x5230;&#x671f;
	public static String decode(String src){
		String[] code=splitor.split(src);
		StringBuilder builder=new StringBuilder();
		for(int i=0,l=code.length;i<l;i++){
			String[] c=codeStarter.split(code[i]);
			builder.append(c[0]);
			if(c.length==2){
				builder.append((char)Integer.parseInt(c[1],16));
			}
		}
		return builder.toString();
	}
	public static String encode(String src){
		StringBuilder result=new StringBuilder();
		for(int i=0,l=src.length();i<l;i++){
			char c=src.charAt(i);
			result.append("&#x").append(Integer.toHexString(c)).append(';');
		}
		return result.toString();
	}
	public static void main(String[] args) {
		System.out.println(UnicodeCodec.decode("&#x60a8;&#x7684;&#x7248;&#x672c;&#x53f7;&#x4e0d;&#x6b63;&#x786e;&#xff0c;&#x53ef;&#x80fd;&#x4e0d;&#x662f;&#x5b98;&#x65b9;&#x7248;&#x672c;&#xff0c;&#x8bf7;&#x901a;&#x8fc7;wifiin&#x5b98;&#x65b9;&#x6e20;&#x9053;&#x4e0b;&#x8f7d;&#x5e76;&#x91cd;&#x8bd5;"));
	}
}
