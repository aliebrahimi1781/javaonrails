package me.jor.codereview.jslint4java;

import java.io.File;

import com.googlecode.jslint4java.JSLintResult;

/**
 * 它的实现类包含自定义js检查逻辑，
 * 
 * */
public interface JsCodeChecker {
	/**
	 * @param file   js文件对象
	 * @param result jslint4java构造的js语法分析结果对象
	 * @return 检查结果字符串，格式必须是html文本
	 * */
	public String check(File file, JSLintResult result);

}
