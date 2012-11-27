package me.jor.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import me.jor.common.CommonConstant;
import me.jor.common.GlobalObject;
import me.jor.exception.DES3Exception;


public class DES3 extends EnDecryptionUtil{
	public static String encrypt(String src) throws UnsupportedEncodingException{
		byte[] dst=encrypt(src.getBytes(CommonConstant.DEFAULT_CHARSET));
		return GlobalObject.BASE64_ENCODER.encode(dst);
	}
	public static byte[] encrypt(byte[] src){
		try{
			byte[] key=new byte[24], iv=new byte[8];
			new Random().nextBytes(key);
			new Random().nextBytes(iv);
			src=Encryptor.encrypt(src, key);
	        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
//	        byte[] encryptedIv=Encryptor.encrypt(iv, key);
	        cipher.init(Cipher.ENCRYPT_MODE,
	        		SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(key)),
	        		new IvParameterSpec(iv));
	        byte[] ciphed=cipher.doFinal(src);
//	        return Help.merge(Encryptor.encrypt(ciphed, encryptedIv),Encryptor.encrypt(encryptedIv,key),key);
	        return Help.merge(ciphed,iv,key);
		}catch(Exception e){
			throw new DES3Exception(e);
		}
	}
	
	public static String decrypt(String src) throws IOException{
		byte[] dst=decrypt(GlobalObject.BASE64_DECODER.decodeBuffer(src));
		return new String(dst,CommonConstant.DEFAULT_CHARSET);
	}
	public static byte[] decrypt(byte[] src){
		try{
			int srcl=src.length;
//			byte[] key=new byte[24],encryptedIv=new byte[32],ciphed=new byte[srcl-56];
//			System.arraycopy(src, srcl-=24, key, 0, 24);
//			System.arraycopy(src, srcl-=32, encryptedIv, 0, 32);
//			System.arraycopy(src, 0, ciphed, 0, srcl);
//			encryptedIv=Decryptor.decrypt(encryptedIv, key);
//			byte[] iv=Decryptor.decrypt(encryptedIv, key);
			byte[] key=new byte[24],iv=new byte[8],ciphed=new byte[srcl-32];
			System.arraycopy(src, srcl-=24, key, 0, 24);
			System.arraycopy(src, srcl-=8, iv, 0, 8);
			System.arraycopy(src, 0, ciphed, 0, srcl);

	        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, 
	        		SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(key)), 
	        		new IvParameterSpec(iv));
//	        ciphed=Decryptor.decrypt(ciphed, encryptedIv);
	        return cipher.doFinal(ciphed);
		}catch(Exception e){
			throw new DES3Exception(e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		String s=encrypt("123456");
		System.out.println(s);
		System.out.println(decrypt(s));
	}
}
