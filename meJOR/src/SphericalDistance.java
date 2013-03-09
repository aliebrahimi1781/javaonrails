
public class SphericalDistance {
	//256   614.271
	//512   307.136
	//1024  153.568
	//2048  76.784
	//2560  61.427
	//3072  51.189
	//3584  43.877
	//3840  40.951
	//4096  38.392
	//5120  30.714
	//8192  19.196
	//16384 9.598
	private static final double RADIAN=Math.PI/180;
	
	public static void main(String[] args) {
//		System.out.println(Math.cos(0));
//		System.out.println(Math.cos(ONE_RADIAN));
//		System.out.println(Math.sin(0));
//		System.out.println(Math.sin(ONE_RADIAN));
//		System.out.println(Math.acos(Math.cos(RADIAN)*Math.cos(RADIAN))*6371004);
		double[] xs=new double[]{5,10,20,30,40,50,100,150,200,300,400,500};
		int R=6371004;
		for(int i=0,l=xs.length;i<l;i++){
			System.out.println(xs[i]+"  "+Math.acos(Math.sqrt(Math.cos(xs[i]/R)))*180/Math.PI);
		}
	}
}
