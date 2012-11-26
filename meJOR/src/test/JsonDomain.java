import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.alibaba.fastjson.JSON;


public class JsonDomain {
	private int i;
	private long l;
	private boolean b;
	private String s;
	private Date d;
	private EnumForJsonTest efjt;
	private JsonDomain2 jd;
	private List<JsonDomain2> jdl;
	public int getI() {
		if(i==0){
			i=new Random().nextInt();
		}
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public long getL() {
		if(l==0){
			l=new Random().nextLong();
		}
		if(i==0){
			i=new Random().nextInt();
		}
		return l;
	}
	public void setL(long l) {
		this.l = l;
	}
	public boolean getB() {
		b=new Random().nextBoolean();
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public String getS() {
		if(s==null){
			s=UUID.randomUUID().toString();
		}
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public Date getD() {
		if(d==null){
			d=new Date(new Random().nextLong());
		}
		return d;
	}
	public void setD(Date d) {
		this.d = d;
	}
	public JsonDomain2 getJd() {
		if(jd==null){
			jd=new JsonDomain2();
		}
		return jd;
	}
	public void setJd(JsonDomain2 jd) {
		this.jd = jd;
	}
	public List<JsonDomain2> getJdl() {
		if(jdl==null){
			jdl=new ArrayList<JsonDomain2>();
			for(int i=0,l=10;i<l;i++){
				jdl.add(new JsonDomain2());
			}
		}
		return jdl;
	}
	public void setJdl(List<JsonDomain2> jdl) {
		this.jdl = jdl;
	}
	public EnumForJsonTest getEfjt() {
		String[] ev={"A","B","C","D"};
		if(efjt==null){
			efjt=Enum.valueOf(EnumForJsonTest.class, ev[Math.abs(new Random().nextInt())%4]);
		}
		return efjt;
	}
	public void setEfjt(EnumForJsonTest efjt) {
		this.efjt = efjt;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {
		File json=new File("e:/json.txt");
		PrintWriter pw=new PrintWriter(new OutputStreamWriter(new FileOutputStream(json),"utf8"));
		long s=System.currentTimeMillis();
		for(int i=0;i<100000;i++){
			pw.println(JSON.toJSONString(new JsonDomain()));
		}
		pw.close();
		System.out.println(System.currentTimeMillis()-s);
	}
}
