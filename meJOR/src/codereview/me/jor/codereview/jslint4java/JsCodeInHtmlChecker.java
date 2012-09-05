package me.jor.codereview.jslint4java;

import java.io.File;
import java.util.Map;

import com.googlecode.jslint4java.JSLintResult;

/**
 * 它的实现类包含js自定义检查逻辑
 * 
 * */
public interface JsCodeInHtmlChecker {
	/**
	 * @param html   待检查的html文本
	 * @param result jslint4java解析的html内嵌的与引用的全部js语法单元
	 *               key是js绝对路径，value是js源码。如果是内嵌的js，key就是html路径
	 *               如果多个script包含内嵌js源码，这些源码的分析结果作为一个整体以html绝对路径为key传入result参数
	 * @return 检查结果字符串，格式必须是html文本
	 * */
	public String check(File html, Map<String,JSLintResult> result);
}
