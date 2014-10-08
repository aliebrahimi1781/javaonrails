package me.jor.util;


public class GeoUtil {
	private static final double RAD = Math.PI / 180.0; 
	private static final double EARTH_RADIUS = 6378137.0;
	/**
	 * 计算两个经纬度之间的距离，单位是米
	 * @param lng1  第一个点的经度
	 * @param lat1 第一个点的纬度
	 * @param lng2 第二个点的经度
	 * @param lat2 第二个点的纬度
	 * @return
	 */
	public static double calculateDistance(double lng1, double lat1, double lng2, double lat2){
		double radLat1 = lat1*RAD;
	    double radLat2 = lat2*RAD;
	    return Math.abs(2 * EARTH_RADIUS * Math.asin(Math.sqrt(Math.pow(Math.sin((radLat1 - radLat2)/2),2)+
	    			Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin((lng1 - lng2)*RAD/2),2))));
	}
	/**
	 * 计算经纬度hash
	 * @param lng      经度
	 * @param lat      纬度
	 * @param iterate  迭代次数，最多32次，大于32或小于等于0按32次算
	 * @return
	 */
	public static long enGeoHashCode(double lng,double lat,int iterate){
		double minLng=-180,maxLng=180,minLat=-90,maxLat=90;
		long hash=0;
		if(iterate<=0 || iterate>32){iterate=32;}
		for(int i=0;i<iterate;i++){
			hash<<=1;
			double midLng=(minLng+maxLng)/2;
			if(lng>midLng){
				hash|=1;
				minLng=midLng;
			}else{
				maxLng=midLng;
			}
			hash<<=1;
			double midLat=(minLat+maxLat)/2;
			if(lat>midLat){
				hash|=1;
				minLat=midLat;
			}else{
				maxLat=midLat;
			}
		}
		return hash;
	}
	/**
	 * 根据geohash值计算大致的经纬度
	 * @param hash    geohash
	 * @param iterate 计算hash时的迭代次数最大32，如果小于等于0，或大于32，就按32算
	 * @return  从hash返回大致的经纬度数组[lng,lat]
	 */
	public static double[] deGeoHashCode(long hash,int iterate){
		if(iterate>32 || iterate<=0){iterate=32;}
		int start=iterate*2-2;
		long mask=0b11L<<start;
		double minLng=-180,maxLng=180,minLat=-90,maxLat=90,midLat=0,midLng=0;
		for(;mask!=0;mask>>>=2,start-=2){
			switch((int)((hash&mask)>>>start)){
			case 0:
				maxLng=midLng;
				maxLat=midLat;
				break;
			case 1:
				maxLng=midLng;
				minLat=midLat;
				break;
			case 2:
				minLng=midLng;
				maxLat=midLat;
				break;
			case 3:
				minLng=midLng;
				minLat=midLat;
				break;
			}
			midLng=(maxLng+minLng)/2;
			midLat=(maxLat+minLat)/2;
		}
		return new double[]{midLng,midLat};
	}
//	public static void main(String[] args) {
//		//漠河121°07′～124°20′，北纬52°10′～53°33′，
//		//三亚北纬18°09′34″——18°37′27″、东经108°56′30″——109°48′28″
//		//0   1   2   3    4      5     6      7       8       9         10           11          12          13             14            15                16                  17                 18
//		//90  0   45 22.5 11.25 5.625 2.8125 1.40625 0.703125 0.3515625 0.17578125 0.087890625 0.0439453125 0.02197265625 0.010986328125 0.0054931640625 0.00274608203125  0.001873041015625
//        //180 0   90 45   22.5  11.25 5.625  2.8125  1.40625  0.703125  0.3515625  0.17578125  0.087890625  0.0439453125  0.02197265625  0.010986328125  0.0054931640625   0.00274608203125  0.001873041015625
//		double lng1=((121+7/60)+(124+20/60))/2;
//		double lat1=(52+10/60+53+33/60)/2;
//		System.out.println(lng1+"     "+lat1);
//		System.out.println(calculateDistance(lng1,lat1,lng1,52.004492));
//		System.out.println(calculateDistance(lng1,lat1,122.007296,lat1));
//		System.out.println(calculateDistance(lng1,lat1,122.007296,52.004492));
//		System.out.println();
//		System.out.println(calculateDistance(lng1,lat1,lng1,52.089832));
//		System.out.println(calculateDistance(lng1,lat1,122.145911,lat1));
//		System.out.println(calculateDistance(lng1,lat1,122.145911,52.089832));
//		
//		System.out.println("*********************************");
//		lng1=(108+56/60+30/60/60+109+48/60+28/60/60)/2;
//		lat1=(18+9/60+34/60/60+18+37/60+27/60/60)/2;
//		System.out.println(lng1+"   "+lat1);
//		System.out.println(calculateDistance(lng1,lat1,lng1,18.004492));
//		System.out.println(calculateDistance(lng1,lat1,108.004723,lat1));
//		System.out.println(calculateDistance(lng1,lat1,108.004723,18.004492));
//		System.out.println();
//		System.out.println(calculateDistance(lng1,lat1,lng1,18.089832));
//		System.out.println(calculateDistance(lng1,lat1,108.094455,lat1));
//		System.out.println(calculateDistance(lng1,lat1,108.094455,18.089832));
//		System.out.println("***********************************");
//		long hash=enGeoHashCode(122.14591108203125, 52.08983208203125, 31);
//		String hs=Long.toBinaryString(hash);
//		System.out.println(hs+"     "+hs.length());
//		System.out.println(java.util.Arrays.toString(deGeoHashCode(4367129235981861236L,31)));
//		System.out.println(calculateDistance(122.14591108203125, 52.08983208203125,122.14591101743281, 52.08983200136572));
//	}
}
