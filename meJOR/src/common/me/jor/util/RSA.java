package me.jor.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA{
	private static final Cache<String,byte[]> keyCache=Cache.getCache(RSA.class.getName()+".key");
	private static byte[] getKeyBytes(String key){
		byte[] kbs=keyCache.get(key);
		if(kbs==null){
			kbs=keyCache.putIfAbsent(key, Base64.decode(key));
		}
		return kbs;
	}
	/**
	 * 解密
	 * @param content 密文
	 * 
	 * @param key 密钥
 	 * @return 解密后的字符串
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 * @throws NoSuchPaddingException 
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String decryptByPublicKey(String content, String key) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException{
		return decrypt(content,key,false);
	}
	public static String decryptByPrivateKey(String content, String key) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		return decrypt(content,key,true);
	}
	private static String decrypt(String content, String key, boolean privateKey) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		return new String(cipher(createCipher(key,Cipher.DECRYPT_MODE,privateKey),Base64.decode(content),false),"utf8");
	}
	/**
	 * 加密
	 * @param content  明文
	 * @param key      密钥
	 * @return         加密后的字符串
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String encryptByPrivateKey(String content, String key) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
		return encrypt(content,key,true);
	}
	public static String encryptByPublicKey(String content, String key) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
		return encrypt(content,key,false);
	}
	private static String encrypt(String content,String key, boolean privateKey) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
		return Base64.encode(cipher(createCipher(key,Cipher.ENCRYPT_MODE,privateKey),content.getBytes("utf8"),true));
	}
	private static Cipher createCipher(String key, int mode, boolean privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(mode, privateKey?createPrivateKey(key):createPublicKey(key));
		return cipher;
	}
	private static byte[] cipher(Cipher cipher, byte[] content,boolean encrypt) throws IllegalBlockSizeException, BadPaddingException, IOException{
		InputStream in = new ByteArrayInputStream(content);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //rsa解密的字节大小最多是128，将待加/解密的内容，按128位拆分
        byte[] buf = buffer(encrypt);
        int bufl;
        while ((bufl = in.read(buf)) != -1) {
        	out.write(cipher.doFinal(buf,0,bufl));
        }
        return out.toByteArray();
	}
	public void decryptByPrivateKey(String srcPath, String dstPath, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		decryptByPrivateKey(new File(srcPath),new File(dstPath),key);
	}
	public void decryptByPrivateKey(File src, File dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		decryptByPrivateKey(new FileInputStream(src),new FileOutputStream(dst),key);
	}
	public void decryptByPrivateKey(InputStream src, OutputStream dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		decrypt(src,dst,key,true);
	}
	public void decryptByPublicKey(String srcPath, String dstPath, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		decryptByPublicKey(new File(srcPath),new File(dstPath),key);
	}
	public void decryptByPublicKey(File src, File dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		decryptByPublicKey(new FileInputStream(src),new FileOutputStream(dst),key);
	}
	public void decryptByPublicKey(InputStream src, OutputStream dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		decrypt(src,dst,key,false);
	}
	private void decrypt(InputStream src, OutputStream dst, String key, boolean privateKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		cipher(createCipher(key,Cipher.DECRYPT_MODE,privateKey),src,dst,false);
	}
	public static void encryptByPrivateKey(String srcPath, String dstPath, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		encryptByPrivateKey(new File(srcPath),new File(dstPath),key);
	}
	public static void encryptByPrivateKey(File src, File dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		encryptByPrivateKey(new FileInputStream(src),new FileOutputStream(dst),key);
	}
	public static void encryptByPrivateKey(InputStream src, OutputStream dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		encrypt(src,dst,key,true);
	}
	public static void encryptByPublicKey(String srcPath, String dstPath, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		encryptByPublicKey(new File(srcPath),new File(dstPath),key);
	}
	public static void encryptByPublicKey(File src, File dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, FileNotFoundException, IOException{
		encryptByPublicKey(new FileInputStream(src),new FileOutputStream(dst),key);
	}
	public static void encryptByPublicKey(InputStream src, OutputStream dst, String key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		encrypt(src,dst,key,false);
	}
	private static void encrypt(InputStream src,OutputStream dst,String key, boolean privateKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException{
		cipher(createCipher(key,Cipher.ENCRYPT_MODE,privateKey),src,dst,true);
	}
	private static void cipher(Cipher cipher, InputStream src, OutputStream dst, boolean encrypt) throws IllegalBlockSizeException, BadPaddingException, IOException{
		byte[] buf=buffer(encrypt);
		int bufl;
		while((bufl=src.read(buf))!=-1){
			dst.write(cipher.doFinal(buf,0,bufl));
		}
	}
	private static byte[] buffer(boolean encrypt){
		return new byte[encrypt?117:128];
	}
	
	/**

	* 得到私钥

	* @param key 密钥字符串（经过base64编码）
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 

	* @throws Exception

	*/

	private static PrivateKey createPrivateKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException{
		return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(getKeyBytes(key)));
	}
	
	public static final String  SIGN_ALGORITHMS = "SHA1WithRSA";
	/**
	* RSA签名
	* @param content 待签名数据
	* @param privateKey 商户私钥
	* @return 签名串
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 * @throws UnsupportedEncodingException 
	 * @throws SignatureException 
	*/
	public static String sign(String content, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, SignatureException, UnsupportedEncodingException{
        java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(createPrivateKey(privateKey));
        signature.update(content.getBytes("utf-8"));
        return Base64.encode(signature.sign());
    }
	
	private static PublicKey createPublicKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException{
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(getKeyBytes(key)));
	}
	
	/**
	* RSA验签
	* @param content 待验签字符串
	* @param sign 签名串
	* @param publicKey 公钥
	* @return 验签通过：true,不通过：false
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 * @throws UnsupportedEncodingException 
	 * @throws SignatureException 
	*/
	public static boolean verify(String content, String sign, String publicKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, SignatureException, UnsupportedEncodingException{
		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		signature.initVerify(createPublicKey(publicKey));
		signature.update(content.getBytes("utf-8"));
		return signature.verify(Base64.decode(sign));
	}
}
