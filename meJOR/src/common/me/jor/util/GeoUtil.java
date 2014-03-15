package me.jor.util;


public class GeoUtil {
	private static final double RAD = Math.PI / 180.0; 
	private static final double EARTH_RADIUS = 6378137.0;
	/**
	 * @param lng1  第一个点的经度
	 * @param lat1 第一个点的纬度
	 * @param lng2 第二个点的经度
	 * @param lat2 第二个点的纬度
	 * @return
	 */
	public static double calculateDistance(double lng1, double lat1, double lng2, double lat2){
		double radLat1 = lat1*RAD;
	    double radLat2 = lat2*RAD;
	       return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(Math.pow(Math.sin((radLat1 - radLat2)/2),2)+
		    		Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin((lng1 - lng2)*RAD/2),2)));
	 }
}
