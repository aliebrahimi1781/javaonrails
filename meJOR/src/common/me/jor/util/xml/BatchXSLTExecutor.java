package me.jor.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import me.jor.common.CommonConstant;
import me.jor.util.CodeDetectUtil;
import me.jor.util.Help;
import me.jor.util.Log4jUtil;

public class BatchXSLTExecutor {
	private static String DEFAULT_METHOD = "html";
	private static String DEFAULT_VERSION = "4.0";
	private static final ConcurrentHashMap<String, Transformer> transformerMap = new ConcurrentHashMap<String, Transformer>();
	private List<Transformer> transformerList=new ArrayList<Transformer>();
	
	static{
		try {
			String path=BatchXSLTExecutor.class.getResource("/").getFile().toString();
			init(path,"^rule-.*$");
		} catch (Exception e) {
			Log4jUtil.log.error("error occured in static block of class "+BatchXSLTExecutor.class.getName(), e);
		}
	}
	
	private static TransformerFactory newTransformerFactory() {
		return TransformerFactory.newInstance();
	}
	private static void init(Map<String, String> xsltmap, String destCharset,String method, String version)
			throws TransformerConfigurationException, IOException {
		try{
			CodeDetectUtil.initCodeDetector();
			TransformerFactory factory = newTransformerFactory();
			if (Help.isEmpty(method)) {
				method = DEFAULT_METHOD;
			}
			if (Help.isEmpty(version)) {
				version = DEFAULT_VERSION;
			}
			if(Help.isEmpty(destCharset)){
				destCharset=CommonConstant.DEFAULT_CHARSET;
			}
			for (Map.Entry<String, String> entry : xsltmap.entrySet()) {
				File path = new File(entry.getKey());
				final String xsltRegex = entry.getValue();
				for (File xslt : path.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if (name.endsWith(".xslt") && name.matches(xsltRegex)) {
							return true;
						} else {
							return false;
						}
					}
				})) {
					String xsltpath = xslt.toString();
					if (!transformerMap.containsKey(xsltpath)) {
						synchronized (transformerMap) {
							if (!transformerMap.containsKey(xsltpath)) {
								Source xsltSource = new StreamSource(new InputStreamReader(new FileInputStream(xslt),CodeDetectUtil.detectCharset(xslt)));
								Transformer transformer = factory.newTransformer(xsltSource);
								transformer.setOutputProperty(OutputKeys.ENCODING,destCharset);
								transformer.setOutputProperty(OutputKeys.METHOD,method);
								transformer.setOutputProperty(OutputKeys.VERSION,version);
								transformerMap.put(xsltpath, transformer);
							}
						}
					}
				}
			}
		}finally{
			CodeDetectUtil.endCodeDetector();
		}
	}
	private static void init(String xsltPath, String xsltNameRegex) throws TransformerConfigurationException, IOException{
		init(xsltPath, xsltNameRegex,null,null,null);
	}
	private static void init(String xsltPath, String xsltNameRegex, String destCharset,String method, String version) throws TransformerConfigurationException, IOException{
		Map<String,String> map=new HashMap<String,String>();
		map.put(xsltPath, xsltNameRegex);
		init(map,destCharset,method,version);
	}
	
	public BatchXSLTExecutor(){
		for(Transformer transformer:transformerMap.values()){
			transformerList.add(transformer);
		}
	}
	public BatchXSLTExecutor(String xsltPath, final String xsltNameRegex) throws TransformerConfigurationException, IOException{
		this(xsltPath, xsltNameRegex,null,null,null);
	}
	public BatchXSLTExecutor(String xsltPath, final String xsltNameRegex, String destCharset,String method, String version) throws TransformerConfigurationException, IOException{
		init(xsltPath,xsltNameRegex, destCharset,method,version);
		File path=new File(xsltPath);
		for (File xslt : path.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".xslt") && name.matches(xsltNameRegex)) {
					return true;
				} else {
					return false;
				}
			}
		})){
			transformerList.add(transformerMap.get(xslt.toString()));
		}
	}
	public BatchXSLTExecutor(Map<String, String> xsltRegexMap) throws TransformerConfigurationException, IOException{
		this(xsltRegexMap,null,null,null);
	}
	public BatchXSLTExecutor(Map<String, String> xsltRegexMap, String destCharset,String method, String version) throws TransformerConfigurationException, IOException{
		init(xsltRegexMap, destCharset, method, version);
		for (Map.Entry<String, String> entry : xsltRegexMap.entrySet()) {
			File path = new File(entry.getKey());
			final String xsltRegex = entry.getValue();
			for (File xslt : path.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xslt") && name.matches(xsltRegex)) {
						return true;
					} else {
						return false;
					}
				}
			})) {
				transformerList.add(transformerMap.get(xslt.toString()));
			}
		}
	}
	public void transform(File xml, File dest) throws TransformerException, IOException{
		try{
			CodeDetectUtil.initCodeDetector();
			transform(new FileInputStream(xml),new FileOutputStream(dest),CodeDetectUtil.detectCharset(xml));
		}finally{
			CodeDetectUtil.endCodeDetector();
		}
	}
	public void transform(InputStream xml, OutputStream dest, String xmlCharset) throws TransformerException, UnsupportedEncodingException{
		transform(new InputStreamReader(xml,xmlCharset),dest);
	}
	public void transform(Reader xml, OutputStream dest) throws TransformerException{
		transform(new StreamSource(xml),new StreamResult(dest));
	}
	public void transform(File xml, OutputStream dest) throws TransformerException, IOException{
		try{
			CodeDetectUtil.initCodeDetector();
			transform(new FileInputStream(xml),dest,CodeDetectUtil.detectCharset(xml));
		}finally{
			CodeDetectUtil.endCodeDetector();
		}
	}
	
	
	
	
	public void transform(org.w3c.dom.Node node, String destpath) throws TransformerException{
		transform(node, new File(destpath));
	}
	public void transform(org.w3c.dom.Node node, File dest) throws TransformerException{
		transform(new DOMSource(node),new StreamResult(dest));
	}
	public void transform(org.w3c.dom.Node node, OutputStream dest) throws TransformerException{
		transform(new DOMSource(node),new StreamResult(dest));
	}
	public void transform(org.w3c.dom.Node node, Writer dest) throws TransformerException{
		transform(new DOMSource(node),new StreamResult(dest));
	}
	private void transform(Source xmlSource, Result result) throws TransformerException{
		for(Transformer transformer:transformerList){
			transformer.transform(xmlSource, result);
		}
	}
	
}
