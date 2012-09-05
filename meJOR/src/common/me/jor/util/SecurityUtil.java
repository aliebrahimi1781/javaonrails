package me.jor.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {
	public static final int MESSAGE_DIGEST_OUTPUT_TYPE_BASE64=0;
	public static final int MESSAGE_DIGEST_OUTPUT_TYPE_HEX=1;
	public static final String MESSAGE_DIGEST_MD2="md2";
	public static final String MESSAGE_DIGEST_MD5="md5";
	public static final String MESSAGE_DIGEST_SHA="sha";
	public static final String MESSAGE_DIGEST_SHA1="sha-1";

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
	public static String messageDigest(byte[] src, String algorithm, int outputType){
		try{
			byte[] digest=MessageDigest.getInstance(algorithm).digest(src);
			switch(outputType){
			case MESSAGE_DIGEST_OUTPUT_TYPE_BASE64:
				return new sun.misc.BASE64Encoder().encode(digest);
			case MESSAGE_DIGEST_OUTPUT_TYPE_HEX:
				StringBuilder result=new StringBuilder();
				for(int i=0,l=digest.length;i<l;i++){
					result.append(Integer.toHexString(digest[i]&0xff));
				}
				return result.toString();
			default:
				throw new IllegalArgumentException("illegal outputType value:"+outputType);
			}
		}catch(NoSuchAlgorithmException e){
			throw new SecurityException(e);
		}
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
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