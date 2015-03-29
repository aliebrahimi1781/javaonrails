package me.jor.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.maven.surefire.shade.org.apache.commons.lang.CharSet;

public class AzDGCrypt {
	/**
	* Md5加密
	* 
	* @param x
	* @return
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	* @throws Exception
	*/
	private static String md5(String x,String charset) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return MessageDigestUtil.md5Hex(x,charset);
	}

	/**
	* 本函数将字符串以 MIME BASE64 编码。此编码方式可以让中文字或者图片也能在网络上顺利传输。在 BASE64
	* 编码后的字符串只包含英文字母大小写、阿拉伯数字、加号与反斜线，共 64 个基本字符，不包含其它特殊的字符， 因而才取名
	* BASE64。编码后的字符串比原来的字符串长度再加 1/3 左右。更多的 BASE64 编码信息可以参考 RFC2045 文件之 6.8 节
	* 
	* @param txt
	* 等待编码的原字串
	* @return
	 * @throws UnsupportedEncodingException 
	*/
	private static String base64Decode(String txt, String charset) throws UnsupportedEncodingException {
		return new String(Base64.decode(txt, charset));
	}

	private static String base64Encode(String txt) throws UnsupportedEncodingException {
		return Base64.encode(txt);
	}

	/**
	* Passport 加密函数
	* 
	* @param string
	* 等待加密的原字串
	* @param string
	* 私有密匙(用于解密和加密)
	* 
	* @return string 原字串经过私有密匙加密后的结果
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	*/
	public static String encrypt(String content, String key,String charset) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Charset cs=Charset.forName(charset);
		return Base64.encode(encrypt(content.getBytes(cs), key.getBytes(cs)));
	}
	
	public static byte[] encrypt(byte[] content,byte[] key) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		Random random = new Random();
		String rad = String.valueOf(random.nextInt(32000));
		// 使用随机数发生器产生 0~32000 的值并 MD5()
		// srand((double)microtime() * 1000000);

		// 变量初始化
		int ctr = 0;
		ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		byte[] encryptKey = MessageDigestUtil.md5(rad.getBytes());

		for (int i = 0; i < content.length; i++) {
			// 如果 $ctr = $encrypt_key 的长度，则 $ctr 清零
			ctr = ctr == encryptKey.length ? 0 : ctr;
			// $tmp 字串在末尾增加两位，其第一位内容为 $encrypt_key 的第 $ctr 位，
			// 第二位内容为 $txt 的第 $i 位与 $encrypt_key 的 $ctr 位取异或。然后 $ctr = $ctr + 1
			byteOut.write(encryptKey[ctr]);
			byteOut.write(content[i] ^ encryptKey[ctr++]);
		}
		// 返回结果，结果为 passport_key() 函数返回值的 base65 编码结果
		return key(byteOut.toByteArray(), key);
	}
	
	/**
	* Passport 解密函数
	* 
	* @param string
	* 加密后的字串
	* @param string
	* 私有密匙(用于解密和加密)
	* 
	* @return string 字串经过私有密匙解密后的结果
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	*/
	public static String decrypt(String content, String key,String charset) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		// $txt 的结果为加密后的字串经过 base64 解码，然后与私有密匙一起，
		// 经过 passport_key() 函数处理后的返回值
		Charset cs=Charset.forName(charset);

		// 返回 $tmp 的值作为结果
		return new String(decrypt(Base64.decode(content),key.getBytes(cs)),cs);
	}
	public static byte[] decrypt(byte[] content,byte[] key) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		content = key(content, key);

		// 变量初始化
		StringBuilder tmp = new StringBuilder();
		ByteArrayOutputStream bytesOut=new ByteArrayOutputStream();
		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		for (int i = 0; i < content.length; i++) {
			// $tmp 字串在末尾增加一位，其内容为 $txt 的第 $i 位，
			// 与 $txt 的第 $i + 1 位取异或。然后 $i = $i + 1
			bytesOut.write(content[i] ^ content[++i]);
		}
		return bytesOut.toByteArray();
	}
	/**
	* Passport 密匙处理函数
	* 
	* @param string
	* 待加密或待解密的字串
	* @param string
	* 私有密匙(用于解密和加密)
	* 
	* @return string 处理后的密匙
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	*/
	private static byte[] key(byte[] content, byte[] encryptKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		// 将 $encrypt_key 赋为 $encrypt_key 经 md5() 后的值
		encryptKey = MessageDigestUtil.md5(encryptKey);
		// 变量初始化
		int ctr = 0;
		
		ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
		
		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		for (int i = 0; i < content.length; i++) {
			// 如果 $ctr = $encrypt_key 的长度，则 $ctr 清零
			ctr = ctr == encryptKey.length ? 0 : ctr;
			// $tmp 字串在末尾增加一位，其内容为 $txt 的第 $i 位，
			// 与 $encrypt_key 的第 $ctr + 1 位取异或。然后 $ctr = $ctr + 1
			byteOut.write(content[i] ^ encryptKey[ctr++]);
		}

		// 返回 $tmp 的值作为结果
		return byteOut.toByteArray();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String txt="helloworld";
		String key="r1pcL0e9jJfur0Zv";
		System.out.println(encrypt(txt,key,"utf8"));
		System.out.println(decrypt("MWsHoHfjtUaEB+g5XcLAYKzyY8U=", key, "utf8"));
	}
}
