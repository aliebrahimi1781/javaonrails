package me.jor.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
	public static String readString(InputStream in, String charset) throws IOException{
		ByteArrayOutputStream buf=new ByteArrayOutputStream();
		byte[] bs=new byte[1024];
		int l=0;
		while((l=in.read(bs))>0){
			buf.write(bs,0,l);
		}
		bs=buf.toByteArray();
		String string=null;
		if(bs!=null && bs.length>0){
			string=new String(bs,Help.isEmpty(charset)?"UTF-8":charset);
		}
		return string;
	}
}
