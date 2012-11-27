package me.jor.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import me.jor.common.CommonConstant;
import me.jor.common.GlobalObject;
import me.jor.exception.DES3Exception;


public class DES3 extends EnDecryptionUtil{
	public String encrypt(String src) throws UnsupportedEncodingException{
		return GlobalObject.BASE64_ENCODER.encode(encrypt(src.getBytes(CommonConstant.DEFAULT_CHARSET)));
	}
	public byte[] encrypt(byte[] src){
		try{
			byte[] key=singleUUIDBytes(), iv=singleUUIDBytes();
			src=Encryptor.encrypt(src, key);
	        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
	        byte[] encryptedKey=Encryptor.encrypt(key, iv),encryptedIv=Encryptor.encrypt(iv, key);
	        cipher.init(Cipher.ENCRYPT_MODE,
	        		SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(encryptedKey)),
	        		new IvParameterSpec(encryptedIv));
	        return Help.merge(Encryptor.encrypt(cipher.doFinal(src), encryptedKey),encryptedIv,key);
		}catch(Exception e){
			throw new DES3Exception(e);
		}
	}
	
	public String decrypt(String src) throws IOException{
		return new String(decrypt(GlobalObject.BASE64_DECODER.decodeBuffer(src)),CommonConstant.DEFAULT_CHARSET);
	}
	public byte[] decrypt(byte[] src){
		try{
			int srcl=src.length;
			byte[] key=new byte[16],encryptedIv=new byte[32],ciphed=new byte[srcl-48];
			System.arraycopy(src, srcl-=16, key, 0, 16);
			System.arraycopy(src, srcl-=32, encryptedIv, 0, 32);
			System.arraycopy(src, 0, ciphed, 0, srcl);
			byte[] iv=Decryptor.decrypt(encryptedIv, key),encryptedKey=Encryptor.encrypt(key, iv);
			ciphed=Decryptor.decrypt(ciphed, encryptedKey);
	        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
	        cipher.init(Cipher.DECRYPT_MODE, 
	        		SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(encryptedKey)), 
	        		new IvParameterSpec(encryptedIv));  
	        return cipher.doFinal(ciphed);
		}catch(Exception e){
			throw new DES3Exception(e);
		}
	}
}
