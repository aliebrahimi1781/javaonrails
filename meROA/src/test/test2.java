import me.jor.roa.core.ResourceAccessContext;


public class test2 {
	
	public static String s="aaa";
	public static ResourceAccessContext c;
	public Class t;
	private test1 t1;
	public test2() throws ClassNotFoundException{
		t=Class.forName("test1");
		System.out.println(t.getClassLoader()+"****");
	}
}
