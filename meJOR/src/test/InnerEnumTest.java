
public class InnerEnumTest {
	private int i;
	private static int si;
	private void test(){
		System.out.println("*****************");
	}
	
	public enum InnerEnum implements Creatable{
		TEST;
		@Override
		public void create() {
			int ii=InnerEnumTest.this.i;
		}
	}
}
