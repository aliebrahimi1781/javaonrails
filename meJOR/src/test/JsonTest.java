import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.alibaba.fastjson.JSON;


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
	
	
	
	public static void main(String[] args) throws IOException {
		test2();
	}
}
