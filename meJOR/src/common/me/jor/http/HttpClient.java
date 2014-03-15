package me.jor.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.jor.util.Help;

public class HttpClient {
	private HttpURLConnection connection;
	private String url;
	private String charset;
	private List<String> cookie;
	private Map<String,String> headers;
	private boolean cache;
	/*
	 * connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(cache);
		connection.setReadTimeout(10000);
		connection.setConnectTimeout(10000);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestProperty("Accept-Charset", Help.isEmpty(charset)?"UTF-8":charset);
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		connection.setRequestProperty("User-Agent", "meJOR-httpclient");
	 * */
	private boolean doInput=true;
	private boolean doOutput=true;
	private int readTimeout;
	private int connectTimeout;
	private boolean instanceFollowRedirects;
	private String acceptCharset;
	private String contentType;
	private String userAgent;
	
	public HttpClient(String sslAlgorithm, String url, String charset,boolean cache) throws NoSuchAlgorithmException, KeyManagementException, MalformedURLException{
		create(sslAlgorithm,url,charset,cache);
	}
	public HttpClient(String url, String charset) throws MalformedURLException, KeyManagementException, NoSuchAlgorithmException{
		this("TLS",url,charset,false);
	}
	public HttpClient(String url, String charset, boolean supportSSL) throws KeyManagementException, NoSuchAlgorithmException{
		if(supportSSL){
			create("TLS",url,charset,false);
		}else{
			create("",url,charset,false);
		}
	}
	private void create(String sslAlgorithm, String url, String charset,boolean cache) throws NoSuchAlgorithmException, KeyManagementException{
		if(Help.isNotEmpty(sslAlgorithm)){
			SSLContext ssl=SSLContext.getInstance(sslAlgorithm);
			ssl.init(null, new TrustManager[]{
					new X509TrustManager(){
						public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
						public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
						public X509Certificate[] getAcceptedIssuers() {return null;}
					}
			}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				public boolean verify(String hostname, SSLSession session) {return true;}
			});
		}
		HttpURLConnection.setFollowRedirects(false);
		this.url=url;
		this.charset=charset;
		this.cache=cache;
	}
	private void setRequestHeaders(HttpURLConnection connection){
		if(Help.isNotEmpty(headers)){
			for(Map.Entry<String, String> entry:headers.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}
	private void setCookies(HttpURLConnection connection){
		if(cookie!=null){
			for(String c:cookie){
				connection.setRequestProperty("Cookie", c);
			}
		}
	}
	private HttpURLConnection createConnection() throws IOException{
		connection=(HttpURLConnection)new URL(url).openConnection();
		connection.setDoInput(doInput);
		connection.setDoOutput(doOutput);
		connection.setUseCaches(cache);
		connection.setReadTimeout(readTimeout==0?10000:readTimeout);
		connection.setConnectTimeout(connectTimeout==0?10000:connectTimeout);
		connection.setInstanceFollowRedirects(instanceFollowRedirects);
		connection.setRequestProperty("Accept-Charset", Help.isEmpty(charset)?"UTF-8":charset);
		connection.setRequestProperty("Content-Type",Help.isEmpty(contentType)?"application/x-www-form-urlencoded":contentType);
		connection.setRequestProperty("User-Agent", Help.isEmpty(userAgent)?"meJOR-httpclient":userAgent);
		setCookies(connection);
		setRequestHeaders(connection);
		return connection;
	}
	
	public String post(String content) throws IOException{
		return communicate(content,"POST");
	}
	public String get() throws IOException{
		return communicate(null,"GET");
	}
	private String communicate(String content, String method) throws IOException{
		try{
			createConnection();
			connection.setRequestMethod(method);
			connection.connect();
			if(content!=null){
				write(connection,content);
			}
			//write(connection,content==null?"":content);
			return read(connection);
		}finally{
			if(connection!=null){
				connection.disconnect();
			}
		}
	}
	private void write(HttpURLConnection connection,String content) throws IOException{
		PrintStream ps=null;
		OutputStream out=null;
		try{
			out=connection.getOutputStream();
			ps=new PrintStream(out,true,charset);
			ps.print(content);
		}finally{
			if(ps!=null){
				ps.close();
			}
			if(out!=null){
				out.close();
			}
		}
	}
	private String read(HttpURLConnection connection) throws IOException{
//		System.out.println(connection.getResponseCode()+" "+connection.getResponseMessage());
		cookie=connection.getHeaderFields().get("Set-Cookie");
		StringBuilder response=new StringBuilder();
		BufferedReader reader=null;
		InputStream in=null;
		try{
			in=connection.getInputStream();
			reader=new BufferedReader(new InputStreamReader(in,charset));
			String line=null;
			while((line=reader.readLine())!=null){
				response.append(line);
			}
			return response.toString();
		}finally{
			if(reader!=null){
				reader.close();
			}
			if(in!=null){
				in.close();
			}
		}
	}
	public int getResponseCode() throws IOException{
		return connection.getResponseCode();
	}
	public void setCookie(List<String> cookie) {
		this.cookie = cookie;
	}
	public List<String> getCookie(){
		return cookie;
	}
	public void addCookie(String cookie) {
		if(this.cookie==null){
			this.cookie=new ArrayList<String>();
		}
		this.cookie.add(cookie);
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public boolean isDoInput() {
		return doInput;
	}
	public void setDoInput(boolean doInput) {
		this.doInput = doInput;
	}
	public boolean isDoOutput() {
		return doOutput;
	}
	public void setDoOutput(boolean doOutput) {
		this.doOutput = doOutput;
	}
	public long getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public long getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public boolean isInstanceFollowRedirects() {
		return instanceFollowRedirects;
	}
	public void setInstanceFollowRedirects(boolean instanceFollowRedirects) {
		this.instanceFollowRedirects = instanceFollowRedirects;
	}
	public String getAcceptCharset() {
		return acceptCharset;
	}
	public void setAcceptCharset(String acceptCharset) {
		this.acceptCharset = acceptCharset;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public void addHeader(String name, String value){
		if(headers==null){
			headers=new HashMap<String,String>();
		}
		headers.put(name, value);
	}
	
	private static String HTTP_PROXY_HOST="http.proxyHost";
	private static String HTTP_PROXY_PORT="http.proxyPort";
	private static String HTTP_NON_PROXY_HOST="http.nonProxyHosts";
	private static String HTTPS_PROXY_HOST="https.proxyHost";
	private static String HTTPS_PROXY_PORT="https.proxyPort";
	private static Pattern STARTS_WITH_OR_SYMBOL=Pattern.compile("^\\|");
	private static Pattern ENDS_WITH_OR_SYMBOL=Pattern.compile("\\|$");
	private static Pattern DOUBLE_OR_SYMBOL=Pattern.compile("\\|\\|");
	private static void addProxyHost(String name,String value){
		Properties props=System.getProperties();
		String host=props.getProperty(name);
		if(Help.isEmpty(host)){
			props.setProperty(name, value);
		}else{
			props.setProperty(name, host+'|'+value);
		}
	}
	private static void setProxyPort(String name, int port){
		System.getProperties().setProperty(name,Integer.toString(port));
	}
	private static void replaceProxyHost(String name, String ip){
		System.getProperties().setProperty(name, ip);
	}
	private static void removeProxyHost(String name,String ip){
		Properties props=System.getProperties();
		String host=props.getProperty(name);
		if(Help.isNotEmpty(host)){
			int start=host.indexOf(ip);
			if(start>=0){
				int end=start+ip.length();
				ip=STARTS_WITH_OR_SYMBOL.matcher(new StringBuilder(host).replace(start, end, ip)).replaceFirst("");
				ip=ENDS_WITH_OR_SYMBOL.matcher(ip).replaceFirst("");
				ip=DOUBLE_OR_SYMBOL.matcher(ip).replaceAll("");
				props.setProperty(name, ip);
			}
		}
	}
	public static void addHttpProxyHost(String ip){
		addProxyHost(HTTP_PROXY_HOST,ip);
	}
	public static void setHttpProxyPort(int port){
		setProxyPort(HTTP_PROXY_PORT,port);
	}
	public static void replaceHttpProxyHost(String ip){
		replaceProxyHost(HTTP_PROXY_HOST,ip);
	}
	public static void removeHttpProxyHost(String ip){
		removeProxyHost(HTTP_PROXY_HOST,ip);
	}
	public static void removeAllHttpProxy(){
		Properties props=System.getProperties();
		props.remove(HTTP_PROXY_HOST);
		props.remove(HTTP_PROXY_PORT);
	}
	
	public static void addNonHttpProxyHost(String ip){
		addProxyHost(HTTP_NON_PROXY_HOST,ip);
	}
	public static void replaceNonHttpProxyHost(String ip){
		replaceProxyHost(HTTP_NON_PROXY_HOST,ip);
	}
	public static void removeNonHttpProxyHost(String ip){
		removeProxyHost(HTTP_NON_PROXY_HOST,ip);
	}
	public static void removeAllNonHttpProxy(){
		System.getProperties().remove(HTTP_NON_PROXY_HOST);
	}
	
	public static void addHttpsProxyHost(String ip){
		addProxyHost(HTTPS_PROXY_HOST,ip);
	}
	public static void setHttpsProxyPort(int port){
		setProxyPort(HTTPS_PROXY_PORT,port);
	}
	public static void replaceHttpsProxyHost(String ip){
		replaceProxyHost(HTTPS_PROXY_HOST,ip);
	}
	public static void removeHttpsProxyHost(String ip){
		removeProxyHost(HTTPS_PROXY_HOST,ip);
	}
	public static void removeAllHttpsProxy(){
		Properties props=System.getProperties();
		props.remove(HTTPS_PROXY_HOST);
		props.remove(HTTPS_PROXY_PORT);
	}
	public static void removeAllProxy(){
		removeAllHttpProxy();
		removeAllHttpsProxy();
	}
}
