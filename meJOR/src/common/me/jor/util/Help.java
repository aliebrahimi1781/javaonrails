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
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

import me.jor.common.Task;

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
	public static final SimpleDateFormat fullDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/**
	 * yyyy-MM-dd HH:mm:ss
	 * */
	public static final SimpleDateFormat datetimeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * yyyy-MM-dd日期格式
	 * */
	public static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * HH:mm:ss时间格式
	 * */
	public static final SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
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
			return isEmpty((Object[])src);
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
		return collection==null || collection.size()==0;
	}
	/**
	 * Map为空的判断
	 * @param src 需判断的Map
	 * @return boolean
	 * @throws 
	 * @exception
	 */
	public static boolean isEmpty(Map<?,?> map){
		return map==null || map.size()==0;
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
	 * 如果src==null，返回dst中第一个不是null的参数，否则返回src
	 * @param src 
	 * @param dst
	 * @return Object 第一个不是空的元素
	 * @throws 
	 * @exception
	 */
	public static Object convert(Object src, Object... dst){
		int i=0;
		while(src==null){
			src=dst[i++];
		}
		return src;
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
	public static <T extends Number> T convert(T src, T... dst){
		int i=0;
		while(src==null || src.intValue()==0){
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
		while(Help.isEmpty(src) && i<l){
			src=dst[i++];
		}
		return src;
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
		if(type.equals(Byte.TYPE) || type.equals(Byte.class) || type==Byte.TYPE || type.equals(Byte.TYPE)){
			return (E)new Byte(value);
		}else if(type.equals(Short.TYPE) || type.equals(Short.class) || type==Short.TYPE || type.equals(Short.TYPE)){
			return (E)new Short(value);
		}else if(type.equals(Integer.TYPE) || type.equals(Integer.class) || type==Integer.TYPE || type.equals(Integer.TYPE)){
			return (E)new Integer(value);
		}else if(type.equals(Long.TYPE) || type.equals(Long.class) || type==Long.TYPE || type.equals(Long.TYPE)){
			return (E)new Long(value);
		}else if(type.equals(Float.TYPE) || type.equals(Float.class) || type==Float.TYPE || type.equals(Float.TYPE)){
			return (E)new Float(value);
		}else if(type.equals(Double.TYPE) || type.equals(Double.class) || type==Double.TYPE || type.equals(Double.TYPE)){
			return (E)new Double(value);
		}else if(type.equals(BigInteger.class)){
			return (E)new BigInteger(value);
		}else if(type.equals(BigDecimal.class)){
			return (E)new BigDecimal(value);
		}else if(type.equals(Character.TYPE) || type.equals(Character.class) || type==Character.TYPE || type.equals(Character.TYPE)){
			return (E)new Character(value.charAt(0));
		}else if(type.equals(String.class)){
			return (E)value;
		}else if(type.equals(Boolean.TYPE) || type.equals(Boolean.class) || type==Boolean.TYPE || type.equals(Boolean.TYPE)){
			return (E)new Boolean(value);
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
	 * 将value值赋给对象的field属性
	 * @param <E>
	 * @param e 对象
	 * @param field 属性
	 * @param fieldType 属性的类型
	 * @param value 值
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException E
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(E e, Field field, Class fieldType, String value) throws IllegalArgumentException, IllegalAccessException{
		field.setAccessible(true);
		field.set(e,parse(fieldType, trim(value)));
		return e;
	}
	/**
	 * 将value值赋给对象的field属性
	 * @param <E>
	 * @param e
	 * @param field 属性
	 * @param value 值
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException E
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(E e, Field field, String value) throws IllegalArgumentException, IllegalAccessException{
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
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException E
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(Class<E> ce, E e, String name, String value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		return populate(e, ce.getDeclaredField(name),value);
	}
	/**
	 * 将value值赋给对象的field属性
	 * @param <E>
	 * @param e 对象 
	 * @param name 属性名称
	 * @param value 值
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException E
	 * @throws 
	 * @exception
	 */
	public static <E> E populate(E e, String name, String value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		return (E)populate((Class<E>)e.getClass(),e,name,value);
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
