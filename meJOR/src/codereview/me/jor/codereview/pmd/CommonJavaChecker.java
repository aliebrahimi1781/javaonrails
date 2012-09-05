package me.jor.codereview.pmd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerConfigurationException;

import me.jor.common.CommonConstant;
import me.jor.util.FileUtil;
import me.jor.util.Help;
import me.jor.util.xml.BatchXSLTExecutor;
import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;

import org.dom4j.io.DOMReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * 基于pmd的xml语法树和用xslt实现检查规则
 * */
public class CommonJavaChecker extends AbstractJavaRule{
	private BatchXSLTExecutor xsltExecutor;
	private PrintStream nodexml;
	private boolean outputNodexml;
	private String xsltPath;
	private String xsltNameRegex;
	private String destCharset=CommonConstant.DEFAULT_CHARSET;
	private ByteArrayOutputStream report=new ByteArrayOutputStream();
	
	public CommonJavaChecker() throws UnsupportedEncodingException{
		xsltExecutor=new BatchXSLTExecutor();
	}
	
	@Override
	public Object visit(ASTCompilationUnit node, Object data) {
		try {
			Node xmlnode=node.asXml();
			if(outputNodexml){
				nodexml.println(new DOMReader().read((Document)xmlnode).asXML());
			}
			xsltExecutor.transform(xmlnode, report);
			super.addViolationWithMessage(data,node,new String(report.toByteArray(),destCharset));
		} catch (Exception e) {
			super.addViolationWithMessage(data,node,e.toString()+"  "+e.getMessage());
		}
		return super.visit(node, data);
	}
	
	private void init() throws TransformerConfigurationException, IOException{
		if(Help.isNotEmpty(xsltPath) && Help.isNotEmpty(xsltNameRegex)){
			xsltExecutor=new BatchXSLTExecutor(xsltPath,xsltNameRegex);
		}
	}
	
	public String getDestCharset(){
		return destCharset;
	}
	public void setDestCharset(String destCharset){
		this.destCharset=destCharset;
	}
	
	public boolean getOutputNodexml() {
		return outputNodexml;
	}
	public void setOutputNodexml(boolean outputNodexml) {
		this.nodexml=System.out;
		this.outputNodexml = outputNodexml;
	}
	
	public String getXsltPath() {
		return xsltPath;
	}

	public void setXsltPath(String xsltPath) throws TransformerConfigurationException, IOException {
		this.xsltPath = xsltPath;
		init();
	}

	public String getXsltNameRegex() {
		return xsltNameRegex;
	}

	public void setXsltNameRegex(String xsltNameRegex) throws TransformerConfigurationException, IOException {
		this.xsltNameRegex = xsltNameRegex;
		init();
	}

	public String getNodexml(){
		return this.nodexml.toString();
	}
	public void setNodexml(String nodexml) throws IOException{
		this.nodexml=new PrintStream(FileUtil.getFile(nodexml),destCharset);
	}
}
