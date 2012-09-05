package me.jor.util.xml;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public class XmlChecker extends BatchXSLTExecutor{
	
	private OutputStream reportOut=System.out;
	
	public XmlChecker() {}
	public XmlChecker(String ruleRegex) throws TransformerConfigurationException, IOException{
		super(XmlChecker.class.getResource("/").getFile().toString(),"rule-esdef");
	}
	public XmlChecker(String ruleRegex, String output) throws TransformerConfigurationException, IOException{
		this(ruleRegex,new File(output));
		
	}
	public XmlChecker(String ruleRegex, File output) throws TransformerConfigurationException, IOException{
		this(ruleRegex);
		if(!output.exists()){
			output.createNewFile();
		}
		this.reportOut=new FileOutputStream(output);
	}
	public XmlChecker(String ruleRegex, OutputStream output) throws TransformerConfigurationException, IOException{
		this(ruleRegex);
		this.reportOut=output;
	}
	
	public void check(String src, boolean recursive) throws TransformerException, IOException{
		check(new File(src), recursive);
	}
	public void check(File src, final boolean recursive) throws TransformerException, IOException{
		if(src.isFile() && src.getName().endsWith(".xml")){
			super.transform(src, reportOut);
		}else if(src.isDirectory()){
			for(File f:src.listFiles(new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					return (pathname.isFile() && pathname.getName().endsWith(".xml")) || (recursive && pathname.isDirectory());
				}})){
				check(f,recursive);
			}
		}
	}
	public void close() throws IOException{
		reportOut.close();
	}
}