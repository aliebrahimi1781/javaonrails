package me.jor.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricEncryptor {
	public static final String AES_ALGORITHM="AES";
	public static final String DES_AlGORITHM="DES";
	public static final String DESEDE_ALGORITHM="DESede";
	public static final String PKCS5_PADDING="PKCS5";
	public static final String PKCS7_PADDING="PKCS7";
	public static final String BC_PROVIDER="BC";
	
	private String algorithm;
	private String padding;
	private byte[] iv;
	private int keysize;
	private String charset;
	private String provider;
	private int ivsize;
	
	private SymmetricEncryptor(){}
	
	public static SymmetricEncryptor init(String algorithm, String padding, String iv,String charset,int keysize) throws UnsupportedEncodingException{
		return init(algorithm,padding,iv.getBytes(charset),charset,keysize,0,null);
	}
	public static SymmetricEncryptor init(String algorithm, String padding,int keysize) throws UnsupportedEncodingException{
		return init(algorithm,padding,(byte[])null,"utf8",keysize,0,null);
	}
	public static SymmetricEncryptor init(String algorithm, String padding, String iv,String charset,int keysize,String provider) throws UnsupportedEncodingException{
		return init(algorithm,padding,iv.getBytes(charset),charset,keysize,0,provider);
	}
	public static SymmetricEncryptor init(String algorithm, String padding,int keysize,String provider) throws UnsupportedEncodingException{
		return init(algorithm,padding,(byte[])null,"utf8",keysize,0,provider);
	}
	public static SymmetricEncryptor init(String algorithm, String padding, String iv,String charset,int keysize,int ivsize) throws UnsupportedEncodingException{
		return init(algorithm,padding,iv.getBytes(charset),charset,keysize,ivsize,null);
	}
	public static SymmetricEncryptor init(String algorithm, String padding,int keysize,int ivsize) throws UnsupportedEncodingException{
		return init(algorithm,padding,(byte[])null,"utf8",keysize,ivsize,null);
	}
	public static SymmetricEncryptor init(String algorithm, String padding, String iv,String charset,int keysize,int ivsize,String provider) throws UnsupportedEncodingException{
		return init(algorithm,padding,iv.getBytes(charset),charset,keysize,ivsize,provider);
	}
	public static SymmetricEncryptor init(String algorithm, String padding,int keysize,int ivsize,String provider) throws UnsupportedEncodingException{
		return init(algorithm,padding,(byte[])null,"utf8",keysize,ivsize,provider);
	}
	public static SymmetricEncryptor init(String algorithm, String padding, byte[] iv,String charset,int keysize,int ivsize,String provider){
		SymmetricEncryptor encryptor=new SymmetricEncryptor();
		encryptor.algorithm=algorithm;
		encryptor.padding=padding;
		encryptor.keysize=keysize;
		encryptor.ivsize=ivsize;
		encryptor.charset=charset;
		encryptor.provider=provider;
		if(iv!=null){
			encryptor.iv=Arrays.copyOf(iv, ivsize);
		}
		return encryptor;
	}
	public byte[] cipher(byte[] content, byte[] key, int mode) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		return cipher(content,key,null,mode);
	}
	public byte[] cipher(byte[] content, byte[] key,byte[] iv, int mode) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		KeyGenerator kgen = KeyGenerator.getInstance(algorithm);  
		SecureRandom secure=SecureRandom.getInstance("SHA1PRNG");
		secure.setSeed(key);
        kgen.init(keysize, secure);  
        SecretKeySpec secretSpec = new SecretKeySpec(kgen.generateKey().getEncoded(), algorithm);
        String transformation=algorithm;
        if(Help.isNotEmpty(padding)){
        	transformation+="/CBC/"+(padding.endsWith("Padding")?padding:(padding+"Padding"));
        }
        Cipher cipher=null;
        if(Help.isNotEmpty(provider)){
        	cipher=Cipher.getInstance(transformation,provider);
        }else{
        	cipher=Cipher.getInstance(transformation);
        }
        if(Help.isEmpty(iv)){
	        if(ivsize==0){
	        	cipher.init(mode, secretSpec,secure);// 初始化
	        }else{
	        	cipher.init(mode, secretSpec, new IvParameterSpec(Help.isEmpty(this.iv)?Arrays.copyOf(key, ivsize):this.iv),secure);// 初始化
	        }
        }else{
        	cipher.init(mode, secretSpec, new IvParameterSpec(iv));// 初始化
        }
        return cipher.doFinal(content);
	}
	public byte[] encrypt(byte[] src, byte[] key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		return cipher(src,key,Cipher.ENCRYPT_MODE);
	}
	public byte[] encrypt(byte[] src, byte[] key, byte[] iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		return cipher(src,key,iv,Cipher.ENCRYPT_MODE);
	}
	public String encryptToBase64(String src, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
		return Base64.encode(encrypt(src.getBytes(charset),key.getBytes(charset)));
	}
	public String encryptToBase64(String src, String key,String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
		return Base64.encode(encrypt(src.getBytes(charset),key.getBytes(charset)));
	}
	public String encryptToBase64(String src, String key,String iv,String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
		return Base64.encode(encrypt(src.getBytes(charset),key.getBytes(charset),iv.getBytes(charset)));
	}
	
	public byte[] decrypt(byte[] src, byte[] key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		return cipher(src,key,Cipher.DECRYPT_MODE);
	}
	public byte[] decrypt(byte[] src, byte[] key, byte[] iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		return cipher(src,key,iv,Cipher.DECRYPT_MODE);
	}
	public String decryptToBase64(String src, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
		return Base64.encode(encrypt(src.getBytes(charset),key.getBytes(charset)));
	}
	public String decryptFromBase64(String src, String key,String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
		return Base64.encode(encrypt(src.getBytes(charset),key.getBytes(charset)));
	}
	public String decryptFromBase64(String src, String key,String iv,String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
		return Base64.encode(encrypt(src.getBytes(charset),key.getBytes(charset),iv.getBytes(charset)));
	}
	public static void main(String[] args) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException {
		System.out.println(init("AES","PKCS7",128,16,"BC").encryptToBase64("helloworld", "19hlcHCh13070t43"));
		System.out.println(init("AES","PKCS7",128,0,"BC").encryptToBase64("helloworld", "1234567890abcdef"));
		System.out.println(init("AES","PKCS7",128,16,"BC").encryptToBase64("helloworld", "1234567890abcdef"));
	}
}
