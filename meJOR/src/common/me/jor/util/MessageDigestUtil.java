package me.jor.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.jor.common.CommonConstant;

public class MessageDigestUtil{
	public static final int MESSAGE_DIGEST_OUTPUT_TYPE_BASE64=0;
	public static final int MESSAGE_DIGEST_OUTPUT_TYPE_HEX=1;
	public static final String MESSAGE_DIGEST_MD2="md2";
	public static final String MESSAGE_DIGEST_MD5="md5";
	public static final String MESSAGE_DIGEST_SHA="sha";
	public static final String MESSAGE_DIGEST_SHA1="sha-1";
	private static final char[] HEXBUF=new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

	public static String messageDigest(long src, String algorithm, int outputType){
		byte[] b=new byte[8];
		for(int i=0;i<8;i++){
			b[i]=(byte)(src>>>(7-i)*8);
		}
		return messageDigest(b,algorithm, outputType);
	}
	
	public static String messageDigest(String src, String charset, String algorithm, int outputType){
		try{
			return messageDigest(src.getBytes(charset), algorithm, outputType);
		}catch(UnsupportedEncodingException e){
			throw new SecurityException(e);
		}
	}
	public static String md5Hex(String src, String charset){
		return messageDigest(src,charset,MESSAGE_DIGEST_MD5,MESSAGE_DIGEST_OUTPUT_TYPE_HEX);
	}
	public static String md5Base64(String src, String charset){
		return messageDigest(src,charset,MESSAGE_DIGEST_MD5,MESSAGE_DIGEST_OUTPUT_TYPE_BASE64);
	}
	public static String md5Hex(String src){
		return messageDigest(src,CommonConstant.DEFAULT_CHARSET,MESSAGE_DIGEST_MD5,MESSAGE_DIGEST_OUTPUT_TYPE_HEX);
	}
	public static String md5Base64(String src){
		return messageDigest(src,CommonConstant.DEFAULT_CHARSET,MESSAGE_DIGEST_MD5,MESSAGE_DIGEST_OUTPUT_TYPE_BASE64);
	}
	public static String shaHex(String src, String charset){
		return messageDigest(src,charset,MESSAGE_DIGEST_SHA,MESSAGE_DIGEST_OUTPUT_TYPE_HEX);
	}
	public static String shaBase64(String src, String charset){
		return messageDigest(src,charset,MESSAGE_DIGEST_SHA,MESSAGE_DIGEST_OUTPUT_TYPE_BASE64);
	}
	public static String shaHex(String src){
		return messageDigest(src,CommonConstant.DEFAULT_CHARSET,MESSAGE_DIGEST_SHA,MESSAGE_DIGEST_OUTPUT_TYPE_HEX);
	}
	public static String shaBase64(String src){
		return messageDigest(src,CommonConstant.DEFAULT_CHARSET,MESSAGE_DIGEST_SHA,MESSAGE_DIGEST_OUTPUT_TYPE_BASE64);
	}
	public static byte[] messageDigest(String src, String algorithm) throws UnsupportedEncodingException{
		return messageDigest(src,CommonConstant.DEFAULT_CHARSET,algorithm);
	}
	public static byte[] messageDigest(String src, String charset, String algorithm) throws UnsupportedEncodingException{
		return messageDigest(src.getBytes(charset),algorithm);
	}
	public static byte[] md5(String src) throws UnsupportedEncodingException{
		return messageDigest(src,MESSAGE_DIGEST_MD5);
	}
	public static byte[] sha(String src) throws UnsupportedEncodingException{
		return messageDigest(src,MESSAGE_DIGEST_SHA);
	}
	public static byte[] messageDigest(byte[] src, String algorithm){
		try{
			return MessageDigest.getInstance(algorithm).digest(src);
		}catch(NoSuchAlgorithmException e){
			throw new SecurityException(e);
		}
	}
	public static byte[] md5(byte[] src){
		return messageDigest(src,MESSAGE_DIGEST_MD5);
	}
	public static byte[] sha(byte[] src){
		return messageDigest(src,MESSAGE_DIGEST_SHA);
	}
	public static String messageDigest(byte[] src, String algorithm, int outputType){
		byte[] digest=messageDigest(src,algorithm);
		switch(outputType){
		case MESSAGE_DIGEST_OUTPUT_TYPE_BASE64:
			return new sun.misc.BASE64Encoder().encode(digest);
		case MESSAGE_DIGEST_OUTPUT_TYPE_HEX:
			StringBuilder result=new StringBuilder();
			int bitmask=0xf;
			for(int i=0,l=digest.length;i<l;i++){
				byte b=digest[i];
				//0x00&bitmask==>>"0" 0x0a&bitmask==>>"a"
				//but we want 0x00&bitmask==>>"00"
				//(b>>4)&bitmask is necessary, or b>>>4 will be a very large number if b is less than 0
				result.append(HEXBUF[b&bitmask]).append(HEXBUF[(b>>4)&bitmask]);
			}
			return result.toString();
		default:
			throw new IllegalArgumentException("illegal outputType value:"+outputType);
		}
	}
	
	public static void main(String[] args) throws IOException {
		String[] pwds=new String[]
		         {"666666","888888","123456","root","root1357","root123","root1234","rootroot",
				  "admin","adminroot","rootadmin","adminadmin","admin123","admin1234","admin1357"};
		for(String pwd:pwds){
			System.out.println(pwd+"\t"+messageDigest(pwd,"utf8","md5",MESSAGE_DIGEST_OUTPUT_TYPE_BASE64)+" "+messageDigest(pwd,"utf8","md5",MESSAGE_DIGEST_OUTPUT_TYPE_HEX));
		}
		long s=System.nanoTime();
		for(int i=0;i<1000000;i++){
			messageDigest("","utf8","md5",MESSAGE_DIGEST_OUTPUT_TYPE_BASE64);
		}
		System.out.println((System.nanoTime()-s)/1000000);
	}
}