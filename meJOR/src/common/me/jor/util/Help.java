package me.jor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import me.jor.common.Task;
import me.jor.exception.ObjectPopulationException;

/**
 * <div>类简介</div>
 * <p>为减少重复逻辑而创建的小工具<br/>
 * dateToTxt txtToDate<br/>
 * 包含日期格式化与反格式化<br/>
 * 
 * isEmpty isNotEmpty<br/>
 * 判断对象是否是null<br/>
 * 判断集合是不是null或是不是空集合<br/>
 * 判断是不是空字符串<br/>
 * 
 * convert<br/>
 * 当指定对象是null,数字是0，字符串是空时返回指定值<br/>
 * 
 * concat<br/>
 * 合并集合，合并字符串<br/>
 * 
 * stringToArray，把指定字符串以指定子串分割，并构造出指定类型的数组对象<br/>
 * 
 * loadProperties storeProperties<br/>
 * 加载/保存properties文件<br/>
 * 
 * trim<br/>
 * 去掉字符串两头的空白字符和\u00a0<br/>
 * 
 * join<br/>
 * 当指定thread!=null时，执行thread.join()和thread.join(milliseconds)<br/>
 * 
 * populate<br/>
 * 向指定对象指定属性名的赋指定值<br/>
 * 
 * sync reentrantSync readSync writeSync<br/>
 * 同步指定相同key值的任务，如果担心使用Lock对象时忘记使用try catch加解锁或应该unlock时误用了lock，或嫌每次使用try catch太麻烦，就可以使用这几个方法<br/>
 * 
 * toString()<br/>
 * 此方法仅做调试用。<br/>
 * 把指定对象属性转化成字符串，格式是：<br/>
 * {<br/>
 * 		name=value<br/>
 * }
 *</p>
 */
public class Help {
	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 * */
	public static final String fullDateFormat="yyyy-MM-dd HH:mm:ss.SSS";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 * */
	public static final String datetimeFormat="yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyy-MM-dd日期格式
	 * */
	public static final String dateFormat="yyyy-MM-dd";
	/**
	 * HH:mm:ss时间格式
	 * */
	public static final String timeFormat="HH:mm:ss";
	/**
	 * 按照yyyy-MM-dd HH:mm:ss格式转换当前时间
	 * @throws 
	 * @exception
	 * @return String 转换后的当前日期字符串
	 */
	public static String currentTimeToTxt(){
		return dateToTxt(System.currentTimeMillis());
	}
	/**
	 * 按照指定日期格式转换当前时间
	 * @param format 需要转化的日期格式,如(yyyy-MM-dd HH:mm:ss.SSS)
	 * @throws 
	 * @exception
	 * @return String 转换后的日期字符串
	 */
	public static String currentTimeToTxt(String format){
		return currentTimeToTxt(new SimpleDateFormat(format));
	}
	/**
	 * 按照指定日期格式转换当前时间
	 * @param format 需要转化的SimpleDateFormat对象
	 * @throws 
	 * @exception
	 * @return String 转化后的日期字符串
	 */
	public static String currentTimeToTxt(SimpleDateFormat format){
		return dateToTxt(System.currentTimeMillis(),format);
	}
	/**
	 * 把当前时间转化成指定格式的字符串
	 */
	public static String nowToTxt(String format){
		return dateToTxt(System.currentTimeMillis(),format);
	}
	/**
	 * 按照yyyy-MM-dd HH:mm:ss格式把date转化成日期字符串格式
	 * @param date 需要转换的日期对象
	 * @return String 转换后的日期字符串
	 * @throws 
	 * @exception
	 */
	public static String dateToTxt(Date date){
		return dateToTxt(date, datetimeFormat);
	}
	/**
	 * 按照指定格式把date转化成日期字符串
	 * @param date 需要转化的日期对象
	 * @param format 需要转化的日期格式,如(yyyy-MM-dd HH:mm:ss.SSS)
	 * @return String 转换后的日期字符串
	 * @throws 
	 * @exception
	 */
	public static String dateToTxt(Date date, String format){
		return dateToTxt(date, new SimpleDateFormat(format)); 
	}
	/**
	 * 按照指定格式把date转化成日期字符串
	 * @param date 需要转化的日期
	 * @param dateFormat 需要转化的SimpleDateFormat对象
	 * @return String 转换后的日期字符串
	 * @throws 
	 * @exception
	 */
	public static String dateToTxt(Date date, SimpleDateFormat dateFormat){
		return date!=null?dateFormat.format(date):null;
	}
	/**
	 * 按照yyyy-MM-dd HH:mm:ss格式把date转化成日期字符串
	 * @param date 
	 * @return String 转换后的日期字符串
	 * @throws 
	 * @exception
	 */
	public static String dateToTxt(long date){
		return dateToTxt(date, datetimeFormat);
	}
	/**
	 * 按照format把date转化成日期字符串
	 * @param date 
	 * @param format 需要转化的日期格式,如(yyyy-MM-dd HH:mm:ss.SSS)
	 * @return String 转换后的日期字符串
	 * @throws 
	 * @exception
	 */
	public static String dateToTxt(long date, String format){
		return dateToTxt(date, new SimpleDateFormat(format));
	}
	/**
	 * 按照dateFormat把date转化成字符串
	 * @param date
	 * @param dateFormat 需要转化的日期格式SimpleDateFormat对象
	 * @return String 转换后的日期字符串
	 * @throws 
	 * @exception
	 */
	public static String dateToTxt(long date, SimpleDateFormat dateFormat){
		return dateFormat.format(date);
	}
	/**
	 * 按照dateFormat把date转化成Date
	 * @param date 日期字符串格式,如(yyyy-MM-dd HH:mm:ss.SSS)
	 * @param dateFormat 需要转化的日期格式SimpleDateFormat对象
	 * @return  Date 转换后的日期对象
	 * @throws ParseException
	 * @throws 
	 * @exception
	 */
	public static Date txtToDate(String date, SimpleDateFormat dateFormat) throws ParseException{
		return dateFormat.parse(date);
	}
	/**
	 * 按照指定format把date转化成Date
	 * @param date 日期字符串格式,如(yyyy-MM-dd HH:mm:ss.SSS)
	 * @param format 字符串格式,如(yyyy-MM-dd HH:mm:ss.SSS)
	 * @return Date 转换后的日期对象
	 * @throws ParseException 
	 * @throws 
	 * @exception
	 */
	public static Date txtToDate(String date, String format) throws ParseException{
		return txtToDate(date, new SimpleDateFormat(format));
	}
	/**
	 * 按照yyyy-MM-dd HH:mm:ss.SSS格式把指定字符串转化成Date
	 * @param date 日期字符串格式,如(yyyy-MM-dd HH:mm:ss.SSS)
	 * @return Date 转换后的日期对象
	 * @throws ParseException 
	 * @throws 
	 * @exception
	 */
	public static Date txtToDate(String date) throws ParseException{
		return txtToDate(date, datetimeFormat);
	}
	public static Date addDateFromTodayStart(int date){
		Calendar c=Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if(date!=0){
			c.add(Calendar.DATE, date);
		}
		return c.getTime();
	}
	public static Date yestoday(){
		return addDateFromTodayStart(-1);
	}
	/**
	 * 得到当天00:00:00.000的时刻
	 */
	public static Date today(){
		return addDateFromTodayStart(0);
	}
	public static Date tomorrow(){
		return addDateFromTodayStart(1);
	}
	/**
	 * 为date在指定的时间域增加amount，amount支持负数，借助Calendar.add(field,amount)实现
	 * @param date
	 * @param value
	 * @param timeField 参考Calendar类的时间常量
	 */
	public static Date addTimeField(Date date, int amount, int timeField){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(timeField, amount);
		return calendar.getTime();
	}
	/**
	 * 为date把指定时间域设置成value，借助Calendar.set(field,value)实现
	 * @param date
	 * @param value
	 * @param timeField
	 * @return
	 */
	public static Date setTimeField(Date date, int value, int timeField){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(timeField, value);
		return calendar.getTime();
	}
	public static Date dateStart(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	/**
	 * 判断字符串是否为空
	 * @param src 需判断的字符串
	 * @return boolean 
	 * @throws 
	 * @exception
	 */
	public static boolean isNotEmpty(String src){
		return !isEmpty(src);
	}
	/**
	 * 判断字符串是否为空或字符串值是否为"null"，判断null值时不分大小写
	 * @param src 要判断的字符串
	 * @return
	 */
	public static boolean isNotEmptyAndNull(String src){
		return !isEmptyOrNull(src);
	}
	/**
	 * 判断对象是否为空
	 * @param src 需判断的对象
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isNotEmpty(Object src){
		return !isEmpty(src);
	}
	/**
	 * 判断集合是否为空
	 * @param collection 需判断的集合
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isNotEmpty(Collection<?> collection){
		return !isEmpty(collection);
	}
	/**
	 * 判断Map是否为空
	 * @param map 需判断的Map
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isNotEmpty(Map<?,?> map){
		return !isEmpty(map);
	}
	/**
	 * 判断数组是否为空
	 * @param array 需判断的数组
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isNotEmpty(Object[] array){
		return !isEmpty(array);
	}
	/**
	 * 判断字符串是否为null、空字符串或只包含空白符
	 */
	public static boolean isNotEmptyAndBlank(String src){
		return !isEmptyOrBlank(src);
	}
	/**
	 * 判断字符串是否为null nill 空串或只包含空白符，不分大小写
	 * @param src
	 * @return
	 */
	public static boolean isNotEmptyAndBlankNull(String src){
		return !isEmptyOrBlankNull(src);
	}
	/**
	 * 字符串类型为空的判断
	 * @param src 需判断的字符串
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isEmpty(String src){
		return src==null || src.length()==0;
	}
	/**
	 * 判断字符串是否为空或字符串值是否为"null"，判断null值时不分大小写
	 * @param src 要判断的字符串
	 * @return
	 */
	public static boolean isEmptyOrNull(String src){
		return isEmpty(src) || "null".equalsIgnoreCase(src) || "nil".equalsIgnoreCase(src);
	}
	/**
	 * 判断字符串是否为null、空字符串或只包含空白符
	 */
	public static boolean isEmptyOrBlank(String src){
		return src==null || src.trim().equals("");
	}
	/**
	 * 判断字符串是否为null nill 空串或只包含空白符，不分大小写
	 * @param src
	 * @return
	 */
	public static boolean isEmptyOrBlankNull(String src){
		return src==null || src.trim().equals("") || "null".equalsIgnoreCase(src) || "nil".equalsIgnoreCase(src);
	}
	/**
	 * 对象为空的判断
	 * @param src 需判断的对象
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isEmpty(Object src) {
		if(src==null){
			return true;
		}else if(src instanceof String){
			return isEmpty((String)src);
		}else if(src instanceof Map){
			return isEmpty((Map<?,?>)src);
		}else if(src instanceof Collection){
			return isEmpty((Collection<?>)src);
		}else if(src.getClass().isArray()){
			return Array.getLength(src)==0;
		}else{
			return src==null;
		}
	}
	/**
	 * 集合为空的判断
	 * @param src 需判断的集合
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isEmpty(Collection<?> collection){
		return collection==null || collection.isEmpty();
	}
	/**
	 * Map为空的判断
	 * @param src 需判断的Map
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isEmpty(Map<?,?> map){
		return map==null || map.isEmpty();
	}
	/**
	 * 数组为空的判断
	 * @param src 需判断的数组
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isEmpty(Object[] array){
		return array==null || array.length==0;
	}
	/**
	 * 判断value是否包含在array内
	 * @param array
	 * @param value
	 * @return
	 */
	public static boolean contains(Object[] array,Object value){
		for(int i=0,l=array.length;i<l;i++){
			Object o=array[i];
			if((o==null && value==null) || (o!=null && o.equals(value))){
				return true;
			}
		}
		return false;
	}
	public static boolean contains(String[] array,String value){
		for(int i=0,l=array.length;i<l;i++){
			Object o=array[i];
			if((o==null && value==null) || (o!=null && o.equals(value))){
				return true;
			}
		}
		return false;
	}
	public static boolean contains(int[] array,int value){
		for(int i=0,l=array.length;i<l;i++){
			int o=array[i];
			if(o==value){
				return true;
			}
		}
		return false;
	}
	public static boolean contains(long[] array,long value){
		for(int i=0,l=array.length;i<l;i++){
			long o=array[i];
			if(o==value){
				return true;
			}
		}
		return false;
	}
	public static boolean contains(Long[] array,Long value){
		for(int i=0,l=array.length;i<l;i++){
			Long o=array[i];
			if((o==null && value==null) || (o!=null && o.equals(value))){
				return true;
			}
		}
		return false;
	}
	public static boolean contains(Integer[] array,Integer value){
		for(int i=0,l=array.length;i<l;i++){
			Integer o=array[i];
			if((o==null && value==null) || (o!=null && o.equals(value))){
				return true;
			}
		}
		return false;
	}

	/**
	 * 如果src!=null && src.intValue()!=0，返回src，否则返回dst中第一个符合前述条件的参数
	 * @param <T>
	 * @param src 返回的符合条件元素
	 * @param dst 传入参数
	 * @return T 返回传入类型
	 * @throws 
	 * @exception
	 */
	public static <T> T convert(T src, T... dst){
		int i=0,l=dst.length;
		while(i<l && (src==null || 
			  "".equals(src) || 
			 ((src instanceof Long || src instanceof Integer || src instanceof AtomicInteger || src instanceof AtomicLong || src instanceof BigInteger) && ((Number)src).intValue()==0) ||
			 (src instanceof Double && ((Double)src).compareTo(0.0)==0) || 
			 (src instanceof Float && ((Float)src).compareTo(0.0f)==0) ||
			 (src instanceof BigDecimal && ((BigDecimal)src).compareTo(BigDecimal.ZERO)==0) || 
			 (src instanceof Collection && ((Collection)src).isEmpty()) || 
			 (src instanceof Map && ((Map)src).isEmpty()))){
			src=dst[i++];
		}
		return src;
	}
	/**
	 * 如果isNotEmpty(src)==true，返回src，否则返回dst中第一个符合前述条件的参数
	 * @param src 
	 * @param dst 
	 * @return String
	 * @throws 
	 * @exception
	 */
	public static String convert(String src, String... dst){
		int i=0,l=dst.length;
		while(i<l && Help.isEmpty(src)){
			src=dst[i++];
		}
		return src;
	}
	public static int convert(String src, int v){
		if(isNotEmpty(src)){
			return Integer.parseInt(src);
		}else{
			return v;
		}
	}
	public static long convert(String src, long v){
		if(isNotEmpty(src)){
			return Long.parseLong(src);
		}else{
			return v;
		}
	}
	public static double convert(String src, double v){
		if(isNotEmpty(src)){
			return Double.parseDouble(src);
		}else{
			return v;
		}
	}
	public static float convert(String src, float v){
		if(isNotEmpty(src)){
			return Float.parseFloat(src);
		}else{
			return v;
		}
	}
	public static byte convert(String src, byte v){
		if(isNotEmpty(src)){
			return Byte.parseByte(src);
		}else{
			return v;
		}
	}
	public static Short convert(String src, short v){
		if(isNotEmpty(src)){
			return Short.parseShort(src);
		}else{
			return v;
		}
	}
	public static BigInteger convert(String src, BigInteger v){
		if(isNotEmpty(src)){
			return new BigInteger(src);
		}else{
			return v;
		}
	}
	public static BigDecimal convert(String src, BigDecimal v){
		if(isNotEmpty(src)){
			return new BigDecimal(src);
		}else{
			return v;
		}
	}
	public static boolean convert(String src, boolean v){
		if(isNotEmpty(src)){
			return Boolean.parseBoolean(src);
		}else{
			return v;
		}
	}
	public static boolean convert(String src, boolean emptyAsFalse, boolean zeroAsFalse, boolean blankAsFalse){
		if(isEmpty(src) && emptyAsFalse){
			return false;
		}else if(src.equals("0") && zeroAsFalse){
			return false;
		}else if(src.matches("^true|false$")){
			return Boolean.parseBoolean(src);
		}else if(src.matches("^\\s+$") && blankAsFalse){
			return false;
		}else{
			throw new IllegalArgumentException(src);
		}
	}
	public static boolean convertEmptyAsFalse(String src){
		return convert(src, true,false,false);
	}
	public static boolean convertZeroAsFalse(String src){
		return convert(src, false,true,false);
	}
	public static boolean convertBlankAsFalse(String src){
		return convert(src, false,false,true);
	}
	public static boolean convertEmptyZeroAsFalse(String src){
		return convert(src, true,true,false);
	}
	public static boolean convertEmptyBlankAsFalse(String src){
		return convert(src, true,false,true);
	}
	public static boolean convertZeroBlankAsFalse(String src){
		return convert(src, false,true,true);
	}
	public static boolean convertEmptyZeroBlankAsFalse(String src){
		return convert(src, true,true,true);
	}
	public static boolean convert(Integer src, boolean nullAsFalse){
		if(src==null){
			if(nullAsFalse){
				return false;
			}else{
				throw new NullPointerException("arg src must not be null");
			}
		}else{
			return src!=0?true:false;
		}
	}
	public static <E extends Enum<E>> E convert(String src, E e){
		if(isNotEmpty(src)){
			return (E)Enum.valueOf(e.getClass(), src);
		}else{
			return e;
		}
	}
	/**
	 *  把src连接成以seperator分隔的字符串，且不以seperator结尾
	 * @param src 集合
	 * @param seperator 分隔字符串
	 * @return String 连接后的字符串
	 * @throws 
	 * @exception
	 */
	@SuppressWarnings("rawtypes")
	public static String concat(Collection src, String seperator){
		return concat(src, seperator, false);
	}
	/**
	 * 把src连接成以seperator分隔的字符串，且不以seperator结尾
	 * @param src
	 * @param seperator
	 * @return
	 */
	public static String concat(Object[] src, String seperator){
		return concat(Arrays.asList(src),seperator);
	}
	/**
	 * 把src连接成以seperator分隔的字符串
	 * @param src
	 * @param seperator
	 * @param endsWithSeperator  true:以seperator结尾，false:不以seperator结尾
	 * @return
	 */
	public static String concat(Object[] src, String seperator, boolean endsWithSeperator){
		return concat(Arrays.asList(src),seperator,endsWithSeperator);
	}
	/**
	 * 把src连接成以seperator分隔的字符串，endsWithSeperator指示是否以seperator结尾
	 * @param src  集合
	 * @param seperator 分隔字符串
	 * @param endsWithSeperator 是否以seperator结尾(true:以seperator结尾,false:不以seperator结尾)
	 * @return String 连接后的字符串
	 * @throws 
	 * @exception
	 */
	@SuppressWarnings("rawtypes")
	public static String concat(Collection src, String seperator, boolean endsWithSeperator){
		if(src == null || src.size()==0){
			return "";
		}
		if(seperator==null){
			seperator="";
		}
		StringBuilder result=new StringBuilder();
		for(Object o:src){
			result.append(o).append(seperator);
		}
		if(!("".equals(seperator) || endsWithSeperator)){
			int l=result.length();
			result.delete(l-seperator.length(),l);
		}
		return result.toString();
	}
	/**
	 * 把sub重复count次构造新的字符串
	 * @param sub 需重复的字符串
	 * @param count 重复的次数
	 * @return String 重复后构造的字符串
	 * @throws 
	 * @exception
	 */
	public static String concat(String sub, int count){
		return concat(sub,count,"");
	}
	/**
	 * 把sub重复count次构造新的字符串，并以seperator分隔，且不以seperator结尾
	 * seperator默认是""
	 * @param sub 需重复的字符串
	 * @param count 重复的次数
	 * @param seperator 构造时以seperator分隔
	 * @return String 重复后构造的字符串
	 * @throws  
	 * @exception
	 */
	public static String concat(String sub, int count, String seperator){
		return concat(sub,count,seperator,false);
	}
	/**
	 * 把sub重复count次构造新的字符串，并以seperator分隔
	 * seperator默认是""
	 * endsWithSeperator指示是否以seperator结尾
	 * @param sub 需重复的字符串
	 * @param count 重复次数
	 * @param seperator 构造时以seperator分隔
	 * @param endsWithSeperator 是否以分隔符结尾(ture:以分隔符结尾，false:不以分隔符结尾)
	 * @return String 重复构造后的字符串
	 * @throws 
	 * @exception
	 */
	public static String concat(String sub,int count, String seperator, boolean endsWithSeperator){
		if(seperator==null){
			seperator="";
		}
		StringBuilder result=new StringBuilder(sub.length()*count);
		for(int i=0;i<count;i++){
			result.append(sub).append(seperator);
		}
		if(!("".equals(seperator) || endsWithSeperator)){
			int l=result.length();
			result.delete(l-seperator.length(),l);
		}
		return result.toString();
	}
	/**
	 * 把若干同类型的单个元素或同类型的一维数组合并到一个一维数组中
	 * @param cls 数组类型，似乎用基本类型会出错
	 * @param src 参数是一系列单个元素，或一系列一维数组，或单个元素与一维数组的混合，且必须是同类型
	 *            当src是单个元素与一维数组的混合，或不只一个一维数组时，cls必须是Object.class
	 * cls不是Object.class时，src必须是指定类的单个一维数组，或一系列单个元素，eg.
	 *     concat(Long.class,1L,2L);
	 *     concat(Long.class,new Long[]{1L,2L}
	 *     concat(Object.class, 1, 2L, new Long[]{3L},4L, new Long[]{5L})
	 *     concat(Object.class, new Long[]{1L,2L},new Long[]{3L,4L});
	 * */
	@SuppressWarnings("unchecked")
	public static <T> T[] concat(Class<T> cls, T... src){
		List<T> list=new ArrayList<T>();
		for(T t:src){
			if(t.getClass().isArray()){
				for(int i=0,l=Array.getLength(t);i<l;i++){
					list.add((T)Array.get(t, i));
				}
			}else{
				list.add(t);
			}
		};
		Object arr=Array.newInstance(cls, list.size());
		for(int i=0,l=list.size();i<l;i++){
			Array.set(arr, i, list.get(i));
		}
		return (T[])arr;
	}
	
	/**
	 * 把一个字符串根据splitor截取并把截取获得的字符串数组格式化成指定的对象
	 * @param src 要格式化的字符串
	 * @param splitor 截取文本
	 * @param cls 转化的目标类
	 * @param otherarggs 转化成指定类所需的其它参数
	 * @throws Exception 
	 * */
	public static <T> T[] stringToArray(String src, String splitor, final Class<T> cls, Object... otherargs) throws Exception{
		return stringToArray(src, splitor,cls, new InstanceCreator(){
			@SuppressWarnings("rawtypes")
			public Object create(Object... args) throws Exception{
				Class[] pt=new Class[args.length];
				for(int i=0,l=pt.length;i<l;i++){
					pt[i]=args[i].getClass();
				}
				return cls.getConstructor(pt).newInstance(args);
			}
		},otherargs);
	}
	/**
	 * 把一个字符串根据splitor截取并把截取获得的字符串数组格式化成指定的对象
	 * @param src 要格式化的字符串
	 * @param splitor 截取文本
	 * @param cls 转化的目标类
	 * @param creator 回调接口，指定把字符串转化到指定类的算法
	 * @param otherarggs 转化成指定类所需的其它参数
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 * @throws ArrayIndexOutOfBoundsException 
	 * */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] stringToArray(String src, String splitor, Class<T> cls, InstanceCreator creator,Object... otherargs) throws Exception{
		String[] splited=src.split(splitor);
		int l=splited.length;
		Object array=Array.newInstance(cls, l);
		Class<Object> objcls=Object.class;
		for(int i=0;i<l;i++){
			Array.set(array,i,creator.create(concat(objcls,splited[i],otherargs)));
		}
		return (T[])array;
	}
	public static interface InstanceCreator{
		public Object create(Object... args) throws Exception;
	}
	/**
	 * 从reader加载Properties数据
	 * @param reader 字符流
	 * @return
	 * @throws IOException Properties
	 * @throws 
	 * @exception
	 */
	public static Properties loadProperties(Reader reader) throws IOException{
		Properties props=new Properties();
		props.load(reader);
		return props;
	}
	/**
	 * 以默认字符集从in加载Properties
	 * @param in 字节流
	 * @return
	 * @throws IOException Properties
	 * @throws 
	 * @exception
	 */
	public static Properties loadProperties(InputStream in) throws IOException{
		Properties props=new Properties();
		props.load(in);
		return props;
	}
	/**
	 * 以charset指定的字符集从in加载Properties
	 * @param in
	 * @param charset
	 * @return
	 * @throws IOException Properties
	 * @throws 
	 * @exception
	 */
	public static Properties loadProperties(InputStream in, String charset) throws IOException{
		return loadProperties(new InputStreamReader(in, charset));
	}
	/**
	 * 从file加载Properties
	 * @param file 保存着Properties数据的文件
	 * @return 从文件加载的Properties对象
	 * @throws FileNotFoundException
	 * @throws IOException Properties
	 */
	public static Properties loadProperties(File file) throws FileNotFoundException, IOException{
		InputStream in=null;
		try{
			in=new FileInputStream(file);
			return loadProperties(in);
		}finally{
			if(in!=null){
				in.close();
			}
		}
	}
	/**
	 * 从file以charset指定字符集加载Properties
	 * @param file 保存着Properies数据的文件
	 * @param charset file的字符集
	 * @return 从file加载的Properties对象
	 * @throws IOException Properties
	 */
	public static Properties loadProperties(File file, String charset) throws IOException{
		InputStream in=null;
		try{
			in=new FileInputStream(file);
			return loadProperties(in,charset);
		}finally{
			if(in!=null){
				in.close();
			}
		}
	}
	/**
	 * 将properties写入writer
	 * @param properties
	 * @param writer
	 * @throws IOException
	 */
	public static void storeProperties(Properties properties,Writer writer) throws IOException{
		properties.store(writer,null);
	}
	/**
	 * 以默认字符集将properties写入输出流
	 * @throws IOException 
	 * */
	public static void storeProperties(Properties properties, OutputStream out) throws IOException{
		properties.store(out, null);
	}
	/**
	 * 将properties写入out，字符集是charset
	 * @param properties
	 * @param out
	 * @param charset
	 * @throws IOException
	 */
	public static void storeProperties(Properties properties, OutputStream out, String charset) throws IOException{
		properties.store(new OutputStreamWriter(out, charset), null);
	}
	/**
	 * 去掉src开头和结尾的空白字符，包括所有小于\u0020的字符和\t\n\u000B\f\r\u00A0
	 * @param src 需过滤的字符串
	 * @return String 过滤后的字符串
	 */
	public static String trim(String src){
		return src.trim().replaceAll("^[\\s\u00A0]+|[\\s\u00A0]+$", "");
	}
	/**
	 * 当指定thread!=null时，执行thread.join()
	 * @param thread 
	 * @throws 
	 * @exception
	 */
	public static void join(Thread thread){
		if(thread!=null){
			try{
				thread.join();
			}catch(Exception e){}
		}
	}
	/**
	 * 当指定thread!=null时，执行thread.join(milliseconds)
	 * @param thread void
	 * @throws 
	 * @exce
	 **/
	public static void join(Thread thread, long millisec){
		if(thread!=null){
			try{
				thread.join(millisec);
			}catch(Exception e){}
		}
	}
	/**
	 * 将字符串转换为指定的类型
	 * @param <E> 转换的类型
	 * @param type 转换类型
	 * @param value 需转换的字符串
	 * @return E 转换后类型
	 * @throws 
	 * @exception
	 */
	public static <E> E parse(Class<E> type, String value){
		if(type.equals(Byte.TYPE) || type.equals(Byte.class) || type==Byte.TYPE || type==Byte.class){
			return (E)new Byte(value);
		}else if(type.equals(Short.TYPE) || type.equals(Short.class) || type==Short.TYPE || type==Short.class){
			return (E)new Short(value);
		}else if(type.equals(Integer.TYPE) || type.equals(Integer.class) || type==Integer.TYPE || type==Integer.class){
			return (E)new Integer(value);
		}else if(type.equals(Long.TYPE) || type.equals(Long.class) || type==Long.TYPE || type==Long.class){
			return (E)new Long(value);
		}else if(type.equals(Float.TYPE) || type.equals(Float.class) || type==Float.TYPE || type==Float.class){
			return (E)new Float(value);
		}else if(type.equals(Double.TYPE) || type.equals(Double.class) || type==Double.TYPE || type==Double.class){
			return (E)new Double(value);
		}else if(type.equals(BigInteger.class)){
			return (E)new BigInteger(value);
		}else if(type.equals(BigDecimal.class)){
			return (E)new BigDecimal(value);
		}else if(type.equals(Character.TYPE) || type.equals(Character.class) || type==Character.TYPE || type==Character.class){
			return (E)new Character(value.charAt(0));
		}else if(type.equals(String.class)){
			return (E)value;
		}else if(type.equals(Boolean.TYPE) || type.equals(Boolean.class) || type==Boolean.TYPE || type==Boolean.class){
			return (E)new Boolean(value);
		}else if(type.equals(UUID.class) || type==UUID.class){
			return (E)UUID.fromString(value);
		}else{
			Class sc=null,pc=null,oc=Object.class;
			do{
				pc=sc;
				sc=type.getSuperclass();
			}while(sc!=null && !(sc!=oc || sc.equals(oc)));
			if(Enum.class==pc || Enum.class.equals(pc)){
				return (E)Enum.valueOf((Class<Enum>)type, value);
			}else if(type==String.class || type.equals(String.class)){
				return (E)value;
			}else{
				return null;
			}
		}
	}
	/**
	 * @param f 要检查的对象属性
	 * @return
	 */
	public static boolean isFinalField(Field f){
		return (f.getModifiers()&Modifier.FINAL)>0;
	}
	/**
	 * @param f  要检查的Field对象
	 * @return
	 */
	public static boolean isStaticField(Field f){
		return (f.getModifiers()&Modifier.STATIC)>0;
	}
	public static boolean isFinalOrStaticField(Field f){
		return isFinalField(f) || isStaticField(f);
	}
	/**
	 * 将value值赋给对象的field属性
	 * @param <E>
	 * @param e 对象
	 * @param field 属性
	 * @param fieldType 属性的类型
	 * @param value 值
	 * @return
	 * @throws Exception 
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(E e, Field field, Class fieldType, Object value){
		try{
			if(isFinalOrStaticField(field)){
				return e;
			}
			field.setAccessible(true);
			if(value instanceof String){
				field.set(e,parse(fieldType,trim((String)value)));
			}else if(Integer.TYPE==fieldType || Long.TYPE==fieldType || Float.TYPE==fieldType || Double.TYPE==fieldType || Short.TYPE==fieldType || 
					 Boolean.TYPE==fieldType || Character.TYPE==fieldType || Byte.TYPE==fieldType ||
					 value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof Short ||
					 value instanceof Boolean || value instanceof Character || value instanceof Byte){
				field.set(e,value);
			}else if(fieldType==BigInteger.class){
				if(value instanceof byte[]){
					field.set(e, new BigInteger((byte[])value));
				}else if(value instanceof BigInteger){
					field.set(e, value);
				}else{
					field.set(e, value.toString());
				}
			}else if(fieldType==BigDecimal.class){
				if(value instanceof BigInteger){
					field.set(e,new BigDecimal((BigInteger)value));
				}else if(value instanceof Integer){
					field.set(e,new BigDecimal(((Integer)value)).intValue());
				}else if(value instanceof Long){
					field.set(e,new BigDecimal(((Long)value)).longValue());
				}else if(value instanceof Float){
					field.set(e,new BigDecimal(((Float)value)).doubleValue());
				}else if(value instanceof Double){
					field.set(e,new BigDecimal(((Double)value)).doubleValue());
				}else if(value instanceof BigDecimal){
					field.set(e,(BigDecimal)value);
				}else if(value instanceof char[]){
					field.set(e,new BigDecimal((char[])value));
				}else{
					field.set(e,value.toString());
				}
			}else if(fieldType==AtomicBoolean.class){
				field.set(e,new AtomicBoolean(((Boolean)value)).get());
			}else if(fieldType==AtomicLong.class){
				field.set(e, new AtomicLong(((Number)value).longValue()));
			}else if(fieldType==AtomicInteger.class){
				field.set(e, new AtomicInteger(((Number)value).intValue()));
			}else if(value instanceof Map){
				String fieldTypeName=fieldType.getSimpleName();
				if(fieldTypeName.endsWith("Map")){
					Map map=(Map)fieldType.newInstance();
					Set<Map.Entry> me=((Map)value).entrySet();
					for(Map.Entry entry:me){
						map.put(entry.getKey(), entry.getValue());
					}
				}else{
					field.set(e,populate(fieldType.newInstance(),(Map)value,false));
				}
			}else if(value instanceof Collection){
				Collection c=(Collection)value.getClass().newInstance();
				for(Object o:(Collection)value){
					Class oc=o.getClass();
					Object dest=null;
					if(o instanceof Collection || o instanceof Map){
						c.add(o);
					}else{
						dest=oc.newInstance();
						populate(dest, o, true);
						c.add(dest);
					}
				}
				field.set(e,c);
			}else{
				field.set(e, value);
			}
			return e;
		}catch(Exception ex){
			throw new ObjectPopulationException(ex);
		}
	}
	/**
	 * 将value值赋给对象的field属性
	 * @param <E>
	 * @param e
	 * @param field 属性
	 * @param value 值
	 * @return
	 * @throws Exception 
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(E e, Field field, Object value){
		return populate(e, field, field.getType(), value);
	}
	/**
	 * 将value值赋给对象的field属性
	 * @param <E>
	 * @param ce 类
	 * @param e 对象
	 * @param name 属性名称
	 * @param value 值
	 * @return
	 * @throws Exception 
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(Class<E> ce, E e, String name, Object value){
		try{
			return populate(e, ce.getDeclaredField(name),value);
		}catch(Exception ex){
			throw new ObjectPopulationException(ex);
		}
	}
	/**
	 * 将value值赋给对象的field属性
	 * @param <E>
	 * @param e 对象 
	 * @param name 属性名称
	 * @param value 值
	 * @return
	 * @throws Exception 
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(E e, String name, Object value){
		return (E)populate((Class<E>)e.getClass(),e,name,value);
	}
	/**
	 * 用map填充对象e，深复制
	 * 将map里的值作为对象e中与map的键同名的成员变量的值
	 * @exception
	 * @param <E>
	 * @param e
	 * @param param
	 * @param populateEmpty 确定是否填充空值对象
	 * @return E
	 * @see
	 */
	public static <E> E populate(E e, Map<String,Object> param, boolean populateEmpty){
		Class<E> c=(Class<E>)e.getClass();
		Field[] fs=c.getDeclaredFields();
		for(int i=0,l=fs.length;i<l;i++){
			Field f=fs[i];
			String name=f.getName();
			Object v=param.get(name);
			if(populateEmpty||isNotEmpty(v)){
				try{
					populate(e,f,f.getType(),v);
				}catch(Exception ex){};
			}
		}
		return e;
	}
	public static <E> E populate(E dst, Object ori, boolean populateEmpty){
		Class<E> c=(Class<E>)dst.getClass();
		Class oc=ori.getClass();
		Field[] fs=c.getDeclaredFields();
		for(int i=0,l=fs.length;i<l;i++){
			Field f=fs[i];
			f.setAccessible(true);
			try{
				Field of=oc.getDeclaredField(f.getName());
				of.setAccessible(true);
				Object v=of.get(ori);
				if(isNotEmpty(v)||populateEmpty){
					try{
						populate(dst,f,f.getType(),v);
					}catch(Exception ex){};
				}
			}catch(Exception e){}
		}
		return dst;
	}
	/**
	 * 将param填充到指定类类型的对象里面，param的key是属性名，值是属性值
	 * @param <E>
	 * @param c
	 * @param src
	 * @param populateEmpty
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <E> E populate(Class<E> c, Map<String,Object> src, boolean populateEmpty){
		try {
			return populate(c.newInstance(),src,populateEmpty);
		} catch (Exception e) {
			throw new ObjectPopulationException(e);
		}
	}
	public static <E> E populate(E e, String fieldName, Object value, boolean populateEmpty){
		if(isNotEmpty(value) || populateEmpty){
			populate(e,fieldName,value);
		}
		return e;
	}
	public static <E> E populate(Class<E> c, Map<String, Object> src, String[] fields, boolean ignoreFields, boolean populateEmpty){
		try {
			return populate(c.newInstance(),src,fields,ignoreFields,populateEmpty);
		} catch (Exception e) {
			throw new ObjectPopulationException(e);
		}
	}
	public static <E> E populate(Class<E> c, Object src, String[] fields, boolean ignoreFields, boolean populateEmpty){
		try {
			return populate(c.newInstance(),src,fields,ignoreFields,populateEmpty);
		} catch (Exception e) {
			throw new ObjectPopulationException(e);
		}
	}
	public static <E> E populate(E e, Map<String, Object> src, String[] fields, boolean ignoreFields, boolean populateEmpty){
		if(ignoreFields){
			Set<String> fset=new HashSet<String>(Arrays.asList(fields));
			for(Map.Entry<String, Object> entry:src.entrySet()){
				String fname=entry.getKey();
				if(!fset.contains(fname)){
					populate(e,fname,entry.getValue(),populateEmpty);
				}
			}
		}else{
			for(int i=0,l=fields.length;i<l;i++){
				String fname=fields[i];
				populate(e,fname,src.get(fname),populateEmpty);
			}
		}
		return e;
	}
	public static <E> E populate(E e, Object src, String[] fields, boolean ignoreFields, boolean populateEmpty){
		try{
			Class<E> c=(Class<E>) src.getClass();
			if(ignoreFields){
				Set<String> fset=new HashSet<String>(Arrays.asList(fields));
				Field[] fs=c.getDeclaredFields();
				for(int i=0,l=fs.length;i<l;i++){
					Field f=fs[i];
					f.setAccessible(true);
					String fname=f.getName();
					if(!fset.contains(fname)){
						populate(e,fname,f.get(src),populateEmpty);
					}
				}
			}else{
				for(int i=0,l=fields.length;i<l;i++){
					String fname=fields[i];
					Field f=c.getDeclaredField(fname);
					f.setAccessible(true);
					populate(e,fname,f.get(src),populateEmpty);
				}
			}
			return e;
		}catch(Exception ex){
			throw new ObjectPopulationException(ex);
		}
	}
	public static <E> E merge(E dst, Object... src){
		for(int i=0,l=src.length;i<l;i++){
			Object s=src[i];
			if(s instanceof Map){
				return populate(dst,(Map<String,Object>)s,true);
			}else{
				Class srcCls=s.getClass();
				Class dstCls=dst.getClass();
				Field[] dstF=dstCls.getDeclaredFields();
				for(int j=0,jl=dstF.length;j<jl;j++){
					try{
						Field f=dstF[j];
						f.setAccessible(true);
						Field srcF=srcCls.getDeclaredField(f.getName());
						srcF.setAccessible(true);
						f.set(dst, srcF.get(s));
					}catch(Exception e){}
				}
			}
		}
		return dst;
	}
	public static <E> Collection<E> merge(Collection<E> dst, Collection<E>... src){
		for(int i=0,l=src.length;i<l;i++){
			Collection<E> c=src[i];
			for(E e:c){
				dst.add(e);
			}
		}
		return dst;
	}
	public static <E> E[] merge(E[] dst, E[]... src){
		Collection c=Arrays.asList(dst);
		for(int i=0,l=src.length;i<l;i++){
			c=merge(c,Arrays.asList(src[i]));
		}
//		Object r=Array.newInstance(dst.getClass(), c.size());
//		System.arraycopy(c.toArray(), 0, r, 0, c.size());
		return (E[])c.toArray();
	}
	public static <E> E[] merge(E[] dst, E... src){
		Object r=Array.newInstance(dst.getClass(), dst.length+src.length);
		System.arraycopy(dst, 0, r, 0, dst.length);
		System.arraycopy(src, 0, r, dst.length, src.length);
		return (E[])r;
	}
	public static byte[] merge(byte[] dst, byte... src){
		byte[] r=new byte[dst.length+src.length];
		System.arraycopy(dst, 0, r, 0, dst.length);
		System.arraycopy(src, 0, r, dst.length, src.length);
		return r;
	}
	public static int[] merge(int[] dst, int... src){
		int[] r=new int[dst.length+src.length];
		System.arraycopy(dst, 0, r, 0, dst.length);
		System.arraycopy(src, 0, r, dst.length, src.length);
		return r;
	}
	public static long[] merge(long[] dst, long... src){
		long[] r=new long[dst.length+src.length];
		System.arraycopy(dst, 0, r, 0, dst.length);
		System.arraycopy(src, 0, r, dst.length, src.length);
		return r;
	}
	public static <E> E[] merge(Class<E> cls, E[]... src){
		E[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			E[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=(E[])Array.newInstance(cls, src1l+srcl);
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static byte[] merge(byte[]... src){
		byte[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			byte[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new byte[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static short[] merge(short[]... src){
		short[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			short[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new short[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static char[] merge(char[]... src){
		char[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			char[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new char[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static int[] merge(int[]... src){
		int[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			int[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new int[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static long[] merge(long[]... src){
		long[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			long[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new long[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static float[] merge(float[]... src){
		float[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			float[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new float[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static double[] merge(double[]... src){
		double[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			double[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new double[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	public static boolean[] merge(boolean[]... src){
		boolean[] dst=null,src1=src[0];
		for(int i=1,l=src.length;i<l;i++){
			boolean[] es=src[i];
			int srcl=es.length,src1l=src1.length;
			dst=new boolean[src1l+srcl];
			System.arraycopy(src1, 0, dst, 0, src1l);
			System.arraycopy(es, 0, dst, src1l, srcl);
			src1=dst;
		}
		return dst;
	}
	/**
	 * 把指定数组转化成List对象，Arrays.asList返回固定大小的List。这个方法返回的是ArrayList对象
	 * @param array 
	 * @return List<E>
	 */
	public static <E> List<E> arrayToList(E[] array){
		List<E> list=new ArrayList<E>();
		for(int i=0,l=array.length;i<l;i++){
			list.add(array[i]);
		}
		return list;
	}
	
	public static <T> T sync(Task task, Lock lock) throws Throwable{
		try{
			lock.lock();
			return task.execute();
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 同步执行runnable
	 * @param runnable
	 * @param lock
	 */
	public static void sync(Runnable runnable, Lock lock){
		try{
			lock.lock();
			runnable.run();
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 同步执行callable
	 * @param <T>
	 * @param callable
	 * @param lock
	 * @return
	 * @throws Exception
	 */
	public static <T> T sync(Callable<T> callable, Lock lock) throws Exception{
		try{
			lock.lock();
			return callable.call();
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 使用ReentrantLock同步runnable
	 * @param key       相同的key对应相同的Lock对象
	 * @param runnable  同步执行的任务
	 */
	public static void reentrantSync(String key, Runnable runnable){
		sync(runnable,LockCache.getReentrantLock(key));
	}
	/**
	 * 使用ReentrantLock同步Callable
	 * @param key      相同的key相同的lock对象
	 * @param callable 同步执行的任务
	 * @return         callable的返回结果
	 * @throws Exception
	 */
	public static <T> T reentrantSync(String key, Callable<T> callable) throws Exception{
		return sync(callable,LockCache.getReentrantLock(key));
	}
	/**
	 * 使用ReadLock同步Runnable
	 * @param key
	 * @param runnable
	 */
	public static void readSync(String key, Runnable runnable){
		sync(runnable,LockCache.getReadLock(key));
	}
	/**
	 * 使用WriteLock同步Runnable
	 * @param key
	 * @param runnable
	 */
	public static void writeSync(String key, Runnable runnable){
		sync(runnable,LockCache.getWriteLock(key));
	}
	/**
	 * 使用ReadLock同步Callable
	 * @param key
	 * @param callable
	 * @return
	 * @throws Exception
	 */
	public static <T> T readSync(String key, Callable<T> callable) throws Exception{
		return sync(callable,LockCache.getReadLock(key));
	}
	/**
	 * 使用WriteLock同步Callable
	 * @param <T>
	 * @param key
	 * @param callable
	 * @return
	 * @throws Exception
	 */
	public static <T> T writeSync(String key, Callable<T> callable) throws Exception{
		return sync(callable, LockCache.getWriteLock(key));
	}
	
	/**
     * 从ip的字符串形式得到字节数组形式
     * @param ip 字符串形式的ip
     * @return 字节数组形式的ip
     */
    public static byte[] getIpByteArrayFromString(String ip) {
        StringTokenizer st = new StringTokenizer(ip, ".");
        return new byte[]{(byte)(Integer.parseInt(st.nextToken()) & 0xFF),
        		          (byte)(Integer.parseInt(st.nextToken()) & 0xFF),
        		          (byte)(Integer.parseInt(st.nextToken()) & 0xFF),
        		          (byte)(Integer.parseInt(st.nextToken()) & 0xFF)};
    }
    /**
     * @param ip ip的字节数组形式
     * @return 字符串形式的ip
     */
    public static String getIpStringFromBytes(byte[] ip) {
    	return ""+(ip[0]&0xff)+'.'+(ip[1]&0xff)+'.'+(ip[2]&0xff)+'.'+(ip[3]&0xff);
    }
    private static final char[] radix64={'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m','0','1','2','3','4','5','6','7','8','9','Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M','-','_'};
	/**
	 * 任意字符串转化成64进制字符串，包含大小写字母、数字、-、_。以utf8编码转化参数
	 * @param src
	 * @return
	 * @throws UnsupportedEncodingException
	 */
    public static String toRadix64(String src) throws UnsupportedEncodingException{
		BigInteger md5=new BigInteger(src.getBytes("utf8"));
		BigInteger radix=new BigInteger("64");
        StringBuilder result=new StringBuilder(0);
        while(!md5.equals(BigInteger.ZERO)){
        	BigInteger tmp=md5.divide(radix);
            result.append(radix64[md5.mod(radix).intValue()]);
            md5=tmp;
        }
        return result.toString();
	}
	/**
	 * 获得参数的所有属性并格式化成字符串
	 * 不要试图用这个方法重写类的toString()方法，这个方法仅用于测试时方便把对象字符串化输出到控制台。
	 * 这个方法把对象内的Number String Character Boolean及它们对应的基本类型和Date Calendar等属性的字符串化。
	 * 对于对象内的集合 数组和其它对象类型的属性，会递归调用此方法
	 * null会转化成"null"，"null"会转化成"\"null\""
	 * @param o 转换的对象
	 * @return String 转换后的字符串
	 * @throws 
	 * @exception
	 */
	public static String toString(Object o){
		return toString(o,"");
	}
	@SuppressWarnings("rawtypes")
	private static String toString(Object o,String recursive){
		if(o==null){
			return "null";
		}else if(o instanceof String){
			return '"'+o.toString()+'"';
		}else if(o instanceof Character){
			return '\''+o.toString()+'\'';
		}else  if(o instanceof Number || o instanceof Boolean || o instanceof Date || o instanceof Calendar){
			return o.toString();
		}else{
			Class cls=o.getClass();
			if(cls.isArray()){
				StringBuilder result=new StringBuilder("[\r\n");
				for(int i=0,l=Array.getLength(o);i<l;i++){
					result.append(recursive).append(toString(Array.get(o, i),recursive+'\t')).append(",\r\n");
				}
				return result.append(recursive).append("]\r\n").toString();
			}else if(o instanceof Collection){
				StringBuilder result=new StringBuilder("[\r\n");
				for(Object oe:(Collection)o){
					result.append(recursive).append(toString(oe,recursive+'\t')).append(",\r\n");
				}
				return result.append(recursive).append("]\r\n").toString();
			}else if(o instanceof Map){
				StringBuilder result=new StringBuilder("{\r\n");
				Map map=(Map)o;
				Set set=map.entrySet();
				for(Object oe:set){
					Map.Entry entry=(Map.Entry)oe;
					result.append(recursive).append(entry.getKey()).append(':').append(toString(entry.getValue(),recursive+'\t')).append(";\r\n");
				}
				return result.append(recursive).append("}\r\n").toString();
			}else{
				Field[] fs=cls.getDeclaredFields();
				StringBuilder result=new StringBuilder("{\r\nclass=").append(cls.getName()).append(";\r\n");
				for(int i=0,l=fs.length;i<l;i++){
					Field f=fs[i];
					f.setAccessible(true);
					String fn=f.getName();
					Object v=null;
					try {v = f.get(o);
					}catch(Exception e) {}
					result.append(recursive).append(fn).append('=').append(toString(v,recursive+'\t')).append(";\r\n");
				}
				return result.append(recursive).append("}\r\n").toString();
			}
		}
	}
	
}
