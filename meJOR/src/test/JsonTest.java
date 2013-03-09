import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;


public class JsonTest {

	public static void jackson1() throws JsonParseException, JsonMappingException, IOException{
		File json=new File("e:/json.txt");
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(json),"utf8"));
		long s=System.currentTimeMillis();
		String line=null;
		ObjectMapper jm=new ObjectMapper();
		long consume=0, consume2=0,consume3=0;
		while((line=br.readLine())!=null){
			long start=System.currentTimeMillis();
			JsonDomain jd=jm.readValue(line, JsonDomain.class);
			consume+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			String jsonStr=JSON.toJSONString(jd);
			consume2+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			jsonStr=jm.writeValueAsString(jd);
			consume3+=System.currentTimeMillis()-start;
		}
		br.close();
		System.out.println(consume+"  "+consume2+"  "+consume3);
	}
	public static void jackson2(){
		
	}
	public static void test1() throws IOException{
		File json=new File("e:/json.txt");
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(json),"utf8"));
		long s=System.currentTimeMillis();
		String line=null;
		ObjectMapper jm=new ObjectMapper();
		
		long consume1=0/*fast parse*/,consume2=0/*jack parse*/,consume3=0/*fast str*/,consume4=0/*jack str*/;
		for(int c=0;c<1000;c++)
		while((line=br.readLine())!=null){
			long start=System.currentTimeMillis();
			JsonDomain jd=JSON.parseObject(line,JsonDomain.class);
			consume1+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			jd=jm.readValue(line, JsonDomain.class);
			consume2+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			String jsonStr=JSON.toJSONString(jd);
			consume3+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			jsonStr=jm.writeValueAsString(jd);
			consume4+=System.currentTimeMillis()-start;
			
		}
		br.close();
		System.out.println(consume1+"  "+consume2+"  "+consume3+"  "+consume4);
	}
	public static void test2() throws IOException{
		File json=new File("e:/json.txt");
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(json),"utf8"));
		long s=System.currentTimeMillis();
		String line=br.readLine();
		ObjectMapper jm=new ObjectMapper();
		long consume1=0/*fast parse*/,consume2=0/*jack parse*/,consume3=0/*fast str*/,consume4=0/*jack str*/;
		
		for(int c=0;c<100000;c++){
			long start=System.currentTimeMillis();
			JsonDomain jd=JSON.parseObject(line,JsonDomain.class);
			consume1+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			jd=jm.readValue(line, JsonDomain.class);
			consume2+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			String jsonStr=JSON.toJSONString(jd);
			consume3+=System.currentTimeMillis()-start;
			start=System.currentTimeMillis();
			jsonStr=jm.writeValueAsString(jd);
			consume4+=System.currentTimeMillis()-start;
		}
		br.close();
		System.out.println(consume1+"  "+consume2+"  "+consume3+"  "+consume4);
	}
	
	private static void test3() throws JsonGenerationException, JsonMappingException, IOException{
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("bool", true);
		m.put("date", new Date());
		m.put("integer",1);
		m.put("longinteger", System.currentTimeMillis());
		m.put("string", "teststring");
		m.put("array", new Object[10]);
		m.put("map",new HashMap());
		new ObjectMapper(new BsonFactory()).writeValue(new FileOutputStream("d:\\testbson.bson"), m);
		new ObjectMapper().writeValue(new FileOutputStream("d:\\testjson.json"), m);
	}
	
	public static void main(String[] args) throws IOException {
//		test2();
		test3();
	}
}
