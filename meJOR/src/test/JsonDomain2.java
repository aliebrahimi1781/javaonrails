import java.util.Date;
import java.util.Random;
import java.util.UUID;


public class JsonDomain2 {
	private int i;
	private long l;
	private boolean b;
	private String s;
	private Date d;
	private EnumForJsonTest efjt;
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
}
