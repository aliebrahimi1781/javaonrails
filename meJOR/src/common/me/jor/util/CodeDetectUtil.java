package me.jor.util;

import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 判断指定文本的字符集
 *
 */
public class CodeDetectUtil {
	private static CodepageDetectorProxy codeDetector;
	private static AtomicInteger threads;
	
	/**
	 * 初始化CodeDetectUtil，每次使用前必须调用此方法
	 * 
	 */
	public static void initCodeDetector(){
		if(threads==null){
			synchronized(CodeDetectUtil.class){
				if(threads==null){
					threads=new AtomicInteger(1);
					codeDetector=CodepageDetectorProxy.getInstance();
					codeDetector.add(new ParsingDetector(false));
					codeDetector.add(JChardetFacade.getInstance());
					codeDetector.add(UnicodeDetector.getInstance());
					codeDetector.add(new ByteOrderMarkDetector());
				}else{
					threads.incrementAndGet();
				}
			}
		}else{
			threads.incrementAndGet();
		}
	}
	/**
	 * 判断结束后调用此方法释放此类占用的资源。
	 */
	public static void endCodeDetector(){
		if(threads.get()>0){
			threads.decrementAndGet();
		}
		if(threads.get()<=0){
			synchronized(CodeDetectUtil.class){
				if(threads.get()<=0){
					codeDetector=null;
					threads=null;
				}
			}
		}
	}
	/**
	 * 返回指定文件的字符集
	 * @param file
	 *        待判断字符集的文件
	 * @return 文件字符集的名称
	 * @throws MalformedURLException
	 * @throws IOException String
	 */
	public static String detectCharset (File file) throws MalformedURLException, IOException{
		return codeDetector.detectCodepage(file.toURI().toURL()).name();
	}
	/**
	 * 返回指定输入流的字符集
	 * @param in
	 *        判断字符集的输入流
	 * @param length
	 *        使用输入流的前面length个字节判断。
	 *        不建议使用太大的值，因为InputStream对象会标记调用时流的位置并在方法返回前缓存所有读过的字节，
	 *        并在方法返回前重置流到标记的位置；否则会占用过多的内存
	 * @return 输入流的字符集名称
	 * @throws MalformedURLException
	 * @throws IOException String
	 */
	public static String detectCharset (InputStream in, int length) throws MalformedURLException, IOException{
		try{
			in.mark(length);
			return codeDetector.detectCodepage(in,length).name();
		}finally{
			in.reset();
		}
	}
	/**
	 * 返回指定字节数组的字符集
	 * @param src  要判断字符集的字节
	 * @return  字节数组的字符集
	 * @throws MalformedURLException 
	 * @throws IOException String
	 */
	public static String detectCharset(byte[] src) throws MalformedURLException, IOException{
		return detectCharset(src,src.length);
	}
	/**
	 * 返回指定字节数组的字符集
	 * @param src     要判断字符集的字节
	 * @param length  用来判断的字节数量，从数组开头计算
	 * @return        字节数组的字符集
	 * @throws MalformedURLException
	 * @throws IOException String
	 */
	public static String detectCharset(byte[] src, int length) throws MalformedURLException, IOException{
		return detectCharset(new ByteArrayInputStream(src),length);
	}
	
//	public static void main(String[] args) throws MalformedURLException, IOException {
//		//E:\workspace\xBankMibsDev\xBankMIBS2.0Dev\WebRoot\mappingXml\middle\DeputizeCharge\agreement2\PROSCT1720001.xml
//		CodeDetectUtil.initCodeDetector();
//		System.out.println(CodeDetectUtil.detectCharset(new File("E:\\workspace\\xBankMibsDev\\xBankMIBS2.0Dev\\WebRoot\\mappingXml\\middle\\DeputizeCharge\\agreement2\\PROSCT1720001.xml")));
//		CodeDetectUtil.endCodeDetector();
//		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("E:\\workspace\\xBankMibsDev\\xBankMIBS2.0Dev\\WebRoot\\mappingXml\\middle\\DeputizeCharge\\agreement2\\PROSCT1720001.xml"),"gb2312"));
//		String line=null;
//		while((line=br.readLine())!=null){
//			System.out.println(line);
//		}
//	}
}
