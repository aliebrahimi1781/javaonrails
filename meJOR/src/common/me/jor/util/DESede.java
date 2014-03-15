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

public class DESede {
    private static byte[] cipher(byte[] key,byte[] src, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
        return SymmetricEncryptor.init("DESede", "PKCS5", 168,8).cipher(src, key, mode);
    }
    /**
     * 加密方法
     * @param src 源数据的字节数组
     * @return 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws InvalidKeyException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchProviderException 
     */
    public static byte[] encrypt(byte[] key,byte[] src) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, NoSuchProviderException {
         return cipher(key,src,Cipher.ENCRYPT_MODE);
     }
    /**
     * 把明文加密并转化成Base64字符串
     * @param algorithm
     * @param src
     * @param charset
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchProviderException 
     */
    public static String encryptToBase64(String key, String src,String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, NoSuchProviderException{
    	return Base64.encode(encrypt(build3DesKey(key,charset),src.getBytes(charset)));
    }
    
    /**
     * 解密函数
     * @param algorithm 算法名称
     * @param src 密文的字节数组
     * @return
     * @throws InvalidKeyException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchProviderException 
     */
    public static byte[] decrypt(byte[] key,byte[] src) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {      
        return cipher(key, src, Cipher.DECRYPT_MODE);
     }
    /**
     * 把base64字符串解密
     * @param algorithm
     * @param src
     * @param charset
     * @return
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchProviderException 
     */
    public static String decryptFromBase64(String key, String src, String charset) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException{
    	return new String(decrypt(build3DesKey(key,charset),Base64.decode(src)),charset);
    }
    
    
    /*
     * 根据字符串生成密钥字节数组 
     * @param keyStr 密钥字符串
     * @return 
     * @throws UnsupportedEncodingException
     */
    private static byte[] build3DesKey(String keyStr, String charset) throws UnsupportedEncodingException{
        byte[] key = new byte[24];    //声明一个24位的字节数组，默认里面都是0
        byte[] temp = keyStr.getBytes(charset);    //将字符串转成字节数组
        
        /*
         * 执行数组拷贝
         * System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
         */
        if(key.length > temp.length){
            //如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, temp.length);
        }else{
            //如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }
//	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {
//		System.out.println(java.util.Arrays.toString(encrypt("helloworld".getBytes("utf8"),"1234567890".getBytes("utf8"))));
//		System.out.println(encryptToBase64("helloworld","1234567890","utf8"));
//		System.out.println(Base64.encode("helloworld".getBytes("utf8")));
//	}
}
