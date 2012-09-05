package me.jor.codereview.jslint4java;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import me.jor.util.CodeDetectUtil;
import me.jor.util.FileUtil;
import me.jor.util.Help;

import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;

public class JsChecker {
	private static final JSLintBuilder jslintBuilder=new JSLintBuilder();
	private static String uriPrefix="/";
	private static String webappPath;
	private static JSLint createJSLint(){
		return jslintBuilder.fromDefault();
	}
	private static void printResult(String result, String resultPath, String charset) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter pw=null;
		try{
			pw=new PrintWriter(new File(resultPath),charset);
			pw.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">");
			pw.println("<html>");
			pw.println("<body>");
			pw.println(result);
			pw.println("</body>");
			pw.println("</html>");
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
	}
	/**
	 * 当需要从html提取js时请务必调用此方法
	 * uriPrefix:uri前缀，默认是"/"
	 * webappPath:就是web应用路径，或叫做webroot路径，默认是当前html的父
	 * 这不是线程安全的，请确保这个方法至多调用一次。
	 * */
	public static void initPath(String uriPrefix, String webappPath){
		if(Help.isNotEmpty(uriPrefix)){
			JsChecker.uriPrefix=uriPrefix;
		}
		if(Help.isNotEmpty(webappPath)){
			JsChecker.webappPath=webappPath;
		}
	}
	
	/**
	 * @param jssrc      js源码文件路径，程序会检查路径是文件还是目录，如果是目录就遍历它的子目录和文件
	 *                   参数是绝对路径
	 * @param charset    结果输出字符集
	 * @param resultPath 检查结果输出路径，必须是文件路径
	 * @param checker    自定义检查路径
	 * */
	public static void check(String jssrc, String resultPath, String charset, JsCodeChecker...checker) throws MalformedURLException, IOException, Exception{
		check(new File(jssrc),resultPath,charset,checker);
	}
	/**
	 * @param jssrc      js源码文件路径，程序会检查路径是文件还是目录，如果是目录就遍历它的子目录和文件
	 *                   参数是绝对路径
	 * @param charset    结果输出字符集
	 * @param resultPath 检查结果输出路径，必须是文件路径
	 * @param checker    自定义检查路径
	 * */
	public static void check(String[] jssrc, String resultPath, String charset, JsCodeChecker... checker) throws MalformedURLException, IOException, Exception{
		PrintWriter pw=null;
		try{
			JSLint jslint=createJSLint();
			CodeDetectUtil.initCodeDetector();
			StringWriter writer=new StringWriter();
			pw=new PrintWriter(writer);
			StringBuilder result=new StringBuilder();
			for(String js:jssrc){
				result.append(check(js,jslint,pw,checker)).append("<br/>").append(writer.toString());
			}
			printResult(result.toString(),resultPath,charset);
		}finally{
			CodeDetectUtil.endCodeDetector();
		}
	}
	/**
	 * @param src      js源码文件路径，程序会检查路径是文件还是目录，如果是目录就遍历它的子目录和文件
	 * @param charset    结果输出字符集
	 * @param resultPath 检查结果输出路径，必须是文件路径
	 * @param checker    自定义检查路径
	 * */
	public static void check(File src, String resultPath, String charset, JsCodeChecker... checker) throws MalformedURLException, IOException, Exception{
		PrintWriter pw=null;
		try{
			CodeDetectUtil.initCodeDetector();
			StringWriter writer=new StringWriter();
			pw=new PrintWriter(writer);
			printResult(check(src, createJSLint(),pw,checker)+"<br/>"+writer.toString(),resultPath,charset);
		}finally{
			pw.close();
			CodeDetectUtil.endCodeDetector();
		}
	}
	/**
	 * @param src      js源码文件路径，程序会检查路径是文件还是目录，如果是目录就遍历它的子目录和文件
	 * @param charset    结果输出字符集
	 * @param resultPath 检查结果输出路径，必须是文件路径
	 * @param checker    自定义检查路径
	 * */
	public static void check(File[] src, String resultPath, String charset, JsCodeChecker... checker) throws MalformedURLException, IOException, Exception{
		JSLint jslint=createJSLint();
		PrintWriter pw=null;
		try{
			CodeDetectUtil.initCodeDetector();
			StringWriter writer=new StringWriter();
			pw=new PrintWriter(writer);
			StringBuilder result=new StringBuilder();
			for(File js:src){
				result.append(check(js,jslint,pw,checker)).append("<br/>").append(writer.toString());
			}
			printResult(result.toString(),resultPath,charset);
		}finally{
			pw.close();
			CodeDetectUtil.endCodeDetector();
		}
	}
	private static String check(String srcpath, JSLint jslint, PrintWriter resultWriter, JsCodeChecker... checker) throws MalformedURLException, IOException, Exception{
		return check(new File(srcpath),jslint,resultWriter,checker);
	}

	private static String check(File js, JSLint jslint, PrintWriter resultWriter, JsCodeChecker... checker) throws MalformedURLException, IOException, Exception{
		if(js.exists()){
			if(js.isDirectory()){
				File[] jsfiles=js.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						if(name.endsWith(".js")){
							return true;
						}else{
							return false;
						}
					}});
				for(File jsf:jsfiles){
					check(jsf,jslint,resultWriter,checker);
				}
			}else{
				String jspath=js.getAbsolutePath();
				if(jspath.endsWith(".js")){
					String jscode=FileUtil.loadTxt(js, CodeDetectUtil.detectCharset(js));
					resultWriter.println(jslint.report(jscode));
					resultWriter.flush();
					if(Help.isNotEmpty(checker)){
						JSLintResult result=jslint.lint(jspath, jscode);
						int i=0;
						for(JsCodeChecker jcc:checker){
							resultWriter.println(jcc.check(js, result));
							i++;
						}
						resultWriter.flush();
						if(i>0){
							return new StringBuilder("javascript path:").append(jspath).toString();
						}
					}
				}
			}
		}
		return "";
	}
	/**
	 * @param html        html源码文件路径，程序会检查路径是文件还是目录，如果是目录就遍历它的子目录和文件
	 *                    参数是绝对路径
	 * @param charset     结果输出字符集
	 * @param resultPath  检查结果输出路径，必须是文件路径
	 * @param checkers    自定义检查路径
	 * */
	public static void check(String html,String resultPath, String charset, JsCodeInHtmlChecker... checkers) throws MalformedURLException, IOException{
		check(new File(html),resultPath,charset,checkers);
	}
	/**
	 * @param html        html源码文件路径，程序会检查路径是文件还是目录，如果是目录就遍历它的子目录和文件
	 * @param charset     结果输出字符集
	 * @param resultPath  检查结果输出路径，必须是文件路径
	 * @param checkers    自定义检查路径
	 * */
	public static void check(File html, String resultPath, String charset, JsCodeInHtmlChecker... checkers) throws MalformedURLException, IOException{
		PrintWriter pw=null;
		try{
			StringWriter sw=new StringWriter();
			check(html,new PrintWriter(sw),checkers);
			printResult(sw.toString(),resultPath,charset);
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
		
	}
	private static void check(File html, PrintWriter resultWriter, JsCodeInHtmlChecker... checkers) throws MalformedURLException, IOException{
		if(html.isDirectory()){
			for(File f:html.listFiles(new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					return pathname.getAbsolutePath().endsWith(".html") || pathname.isDirectory();
			}})){
				check(f,resultWriter,checkers);
			}
		}else{
			Map<String, JSLintResult> results=lintAllJsInHtml(html,resultWriter);
			if(Help.isNotEmpty(checkers)){
				for(JsCodeInHtmlChecker checker:checkers){
					checker.check(html, results);
				}
			}
		}
	}
	private static Map<String, JSLintResult> lintAllJsInHtml(File html, PrintWriter resultWriter) throws MalformedURLException, IOException{
		JSLint jslint=createJSLint();
		Map<String, JSLintResult> map=new HashMap<String, JSLintResult>();
		for(Map.Entry<String, String> entry : extractJsFromHtml(html).entrySet()){
			String k=entry.getKey();
			String code=entry.getValue();
			String report=jslint.report(code);
			if(Help.isNotEmpty(report)){
				resultWriter.print("<h3>");
				resultWriter.print(k);
				resultWriter.println("</h3>");
				resultWriter.println(report);
			}
			map.put(k,jslint.lint(k, code));
		}
		return map;
	}
	/**
	 * @param html 提取单个html文件内嵌和引用的全部js
	 *             如果有多个script包含内嵌js，程序会首先把这些js源码连接成一个字符串以html绝对路径为key保存到返回值
	 *             引用的js文件源码，以js绝对路径为key，源码为值保存到返回结果
	 *             参数是html文件的绝对路径
	 * */
	public static Map<String,String> extractJsFromHtml(String html) throws MalformedURLException, IOException{
		return extractJsFromHtml(new File(html));
	}
	/**
	 * @param html 提取单个html文件内嵌和引用的全部js
	 *             如果有多个script包含内嵌js，程序会首先把这些js源码连接成一个字符串以html绝对路径为key保存到返回值
	 *             引用的js文件源码，以js绝对路径为key，源码为值保存到返回结果
	 * */
	public static Map<String,String> extractJsFromHtml(File html) throws MalformedURLException, IOException {
		PrintWriter pw=null;
		try{
			CodeDetectUtil.initCodeDetector();
			Document htmldoc=DataUtil.load(html, CodeDetectUtil.detectCharset(html), html.toURI().getPath());
			Elements scripts=htmldoc.select("script");
			Map<String,String> jscode=new HashMap<String,String>();
			StringWriter sw=new StringWriter();
			pw=new PrintWriter(sw);
			for(Element script : scripts){
				String type=script.attr("type");
				String language=script.attr("language");
				if((Help.isEmpty(type) || type.equals("text/javascript")) && 
				   (Help.isEmpty(language) || language.equals("javascript"))){
					String src=script.attr("src");
					if(Help.isEmpty(src)){
						pw.println(script.html().trim());
					}else{
						File jsfile=null;
						if(src.startsWith(JsChecker.uriPrefix)){
							jsfile=new File(Help.isEmpty(webappPath)?html.getParent():webappPath,src.substring(JsChecker.uriPrefix.length()));
						}else{
							jsfile=new File(html.getParentFile(),src);
						}
						jscode.put(jsfile.getAbsolutePath(),FileUtil.loadTxt(jsfile, CodeDetectUtil.detectCharset(jsfile)));
					}
				}
			}
			pw.flush();
			jscode.put(html.getAbsolutePath(),sw.toString());
			return jscode;
		}finally{
			CodeDetectUtil.endCodeDetector();
		}
	}
}