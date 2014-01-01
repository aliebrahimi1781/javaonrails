package me.jor.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AES {
	private static byte[] cipher(byte[] content, byte[] key,byte[] iv, int mode) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
        return SymmetricEncryptor.init("AES", "PKCS5", 128,16).cipher(content, key, iv, mode);
	}
	/** 
	 * 加密 
	 *  
	 * @param content 需要加密的内容 
	 * @param password  加密密码 
	 * @return 
	 * @throws NoSuchAlgorithmException 
	 * @throws NoSuchPaddingException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 */  
	public static byte[] encrypt(byte[] content, byte[] key,byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {  
        return cipher(content,key,iv,Cipher.ENCRYPT_MODE);
	}
	public static String encryptToBase64(String content, String key,String iv, String charset) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		return Base64.encode(encrypt(content.getBytes(charset),key.getBytes(charset),iv==null?null:iv.getBytes(charset)));
	}
	
	/**解密 
	 * @param content  待解密内容 
	 * @param password 解密密钥 
	 * @return 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidKeyException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 * @throws UnsupportedEncodingException 
	 */  
	public static byte[] decrypt(byte[] content, byte[] key,byte[] iv) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException {  
         return cipher(content,key,iv,Cipher.DECRYPT_MODE);
	}
	public static String decryptFromBase64(String content, String key,String iv, String charset) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
		return new String(decrypt(Base64.decode(content),key.getBytes(charset),iv==null?null:iv.getBytes(charset)),charset);
	}
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {
		System.out.println(encryptToBase64("密码","加密的密钥",null,"utf8"));
		System.out.println(decryptFromBase64("3X6dyYotA9mekKGaXH52vQ==", "2w174023821G3l2487ry7520C2864574", null, "utf8"));
	}
}
