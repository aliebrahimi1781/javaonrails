package me.jor.nio.socket.constant;

import me.jor.common.CommonConstant;

/**
 * 以下常量在整个应用的运行期不变。
 * BYTE_BUFFER_POOL_SIZE：ByteBufferPool的字节数。默认是100*1024*1024。
 * 常量定义文件的key是：properties.dev.project.niosocket.constant.bytebuffer.poolsize<br/>
 * DIRECT_BYTE_BUFFER:决定创建的ByteBuffer对象是不是直接的。默认是true。
 * 常量定义文件的key是：properties.dev.project.niosocket.constant.direct.bytebuffer<br/>
 * FIXED_BYTE_BUFFER_SIZE:决定每次从ByteBufferPool返回的ByteBuffer对象是否大小固定。
 * 如果值大于0，则每个ByteBuffer大小都是此值(如果池大小不能被此值整除，最后一个BytBuffer可能会比较小)，否则每个ByteBuffer大小就是要传输的字节数。此值的单位是字节。默认是0。
 * 常量定义文件的key是：properties.dev.project.niosocket.constant.fixedsize.bytebuffer<br/>
 * ACTION_ON_POOL_EMPTY：池内没有空间分配给ByteBuffer时，将要采取的策略：WAIT RETURN_NULL RETURN_TEMP。
 * 常量定义文件的key是：properties.dev.project.niosocket.constant.ActionOnPoolEmpty。<br/>
 * <br/>
 * MONITOR_READ_WRITE_THREAD_POOL_SIZE:默认的监控方式，如果指定了此值且大于0，则下面两个值（MONITOR_READ_THREAD_POOL_SIZE、MONITOR_WRITE_THREAD_POOL_SIZE）会被忽略，
 * 此值决定Selector数量，一个Selector一个线程，读写共享相同的Selector对象。常量定义文件的key是：properties.dev.project.niosocket.constant.monitor.readwrite.poolsize<br/>
 * MONITOR_READ_THREAD_POOL_SIZE：如果指定了此值，必须同时指定MONITOR_WRITE_THREAD_POOL_SIZE，否则仍然使用MONITOR_READ_WRITE_THREAD_POOL_SIZE。
 * 此值决定监控读操作的Selector数量，一个Selector一个线程。常量定义文件的key是：properties.dev.project.niosocket.constant.monitor.read.poolsize<br/>
 * MONITOR_WRITE_THREAD_POOL_SIZE：如果指定了此值，必须同时指定MONITOR_READ_THREAD_POOL_SIZE，否则仍然使用MONITOR_READ_WRITE_THREAD_POOL_SIZE。
 * 此值决定监控写操作的Selector数量，一个Selector一个线程。常量定义文件的key是：properties.dev.project.niosocket.constant.monitor.write.poolsize
 * <br/>
 * SERVER_HOST:常量定义文件的key是：properties.dev.project.niosocket.constant.serverhost。<br/>
 *             格式：ip1:port1,ip2:port2<br/>
 * SOCKETCHANNEL_TIMEOUT:常量定义文件的key是：properties.dev.project.niosocket.constant.socketchannel.timeout。<br/>
 *             表示SocketChannelPool池内的SocketChannel超时时间。默认是0，即不在池中维护池对象<br/>
 * RECYCLE_IN_CHANNEL_GETTER:常量定义文件的key是：properties.dev.project.niosocket.constant.socketchannel.recycleInGetter。<br/>
 *             值：true,false，默认false
 */
public class NIOSocketConstant {
	private static int BYTE_BUFFER_POOL_SIZE;
	private static boolean DIRECT_BYTE_BUFFER;
	private static int FIXED_BYTE_BUFFER_SIZE;
	private static ActionOnPoolNoIdleSpace ACTION_ON_POOL_EMPTY;
	
	private static int MONITOR_READ_THREAD_POOL_SIZE;
	private static int MONITOR_WRITE_THREAD_POOL_SIZE;
	private static int MONITOR_READ_WRITE_THREAD_POOL_SIZE;
	
	private static String SERVERHOST;
	private static int SOCKETCHANNEL_TIMEOUT;
	private static boolean RECYCLE_IN_CHANNEL_GETTER;
	
	static{
		BYTE_BUFFER_POOL_SIZE=initByteBufferPoolSize();
		DIRECT_BYTE_BUFFER=initDirectByteBuffer();
		FIXED_BYTE_BUFFER_SIZE=initFixedByteBufferSize();
		ACTION_ON_POOL_EMPTY=initActionOnPoolEmpty();
		
		SERVERHOST=initServerHost();
		SOCKETCHANNEL_TIMEOUT=initSocketChannelTimeout();
		
		RECYCLE_IN_CHANNEL_GETTER=initRecycleInGetter();
		
		MONITOR_READ_WRITE_THREAD_POOL_SIZE=initMonitorReadWriteThreadPoolSize();
		if(MONITOR_READ_WRITE_THREAD_POOL_SIZE<1){
			MONITOR_READ_THREAD_POOL_SIZE=initMonitorReadThreadPoolSize();
			MONITOR_WRITE_THREAD_POOL_SIZE=initMonitorWriteThreadPoolSize();
		}else{
			MONITOR_READ_THREAD_POOL_SIZE=MONITOR_WRITE_THREAD_POOL_SIZE=0;
		}
		if(MONITOR_READ_WRITE_THREAD_POOL_SIZE<1 && (MONITOR_READ_THREAD_POOL_SIZE<1 || MONITOR_WRITE_THREAD_POOL_SIZE<1)){
			MONITOR_READ_WRITE_THREAD_POOL_SIZE=Runtime.getRuntime().availableProcessors();
		}
	}
	private static int initByteBufferPoolSize(){
		return CommonConstant.getIntConstant("properties.dev.project.niosocket.constant.bytebuffer.poolsize", 100*1024*1024);
	}
	private static int initMonitorReadWriteThreadPoolSize(){
		return CommonConstant.getIntConstant("properties.dev.project.niosocket.constant.monitor.readwrite.poolsize", 0);
	}
	private static int initMonitorReadThreadPoolSize() {
		return CommonConstant.getIntConstant("properties.dev.project.niosocket.constant.monitor.read.poolsize", 0);
	}
	private static int initMonitorWriteThreadPoolSize() {
		return CommonConstant.getIntConstant("properties.dev.project.niosocket.constant.monitor.write.poolsize", 0);
	}
	private static boolean initDirectByteBuffer(){
		return CommonConstant.getBooleanConstant("properties.dev.project.niosocket.constant.direct.bytebuffer", true);
	}
	private static int initFixedByteBufferSize(){
		int size=CommonConstant.getIntConstant("properties.dev.project.niosocket.constant.fixedsize.bytebuffer", 0);
		return size>0?size:0;
	}
	public static ActionOnPoolNoIdleSpace initActionOnPoolEmpty(){
		return CommonConstant.getEnumConstant("properties.dev.project.niosocket.constant.ActionOnPoolEmpty", ActionOnPoolNoIdleSpace.class, ActionOnPoolNoIdleSpace.WAIT);
	}
	public static String initServerHost(){
		return CommonConstant.getStringConstant("properties.dev.project.niosocket.constant.serverhost", "");
	}
	public static int initSocketChannelTimeout(){
		return CommonConstant.getIntConstant("properties.dev.project.niosocket.constant.socketchannel.timeout", 0);
	}
	public static boolean initRecycleInGetter(){
		return CommonConstant.getBooleanConstant("properties.dev.project.niosocket.constant.socketchannel.recycleInGetter", false);
	}
	
	
	public static int getBYTE_BUFFER_POOL_SIZE(){
		return BYTE_BUFFER_POOL_SIZE;
	}
	public static boolean getDIRECT_BYTE_BUFFER(){
		return DIRECT_BYTE_BUFFER;
	}
	public static int getFIXED_BYTE_BUFFER_SIZE(){
		return FIXED_BYTE_BUFFER_SIZE;
	}
	public static int getMONITOR_READ_THREAD_POOL_SIZE(){
		return MONITOR_READ_WRITE_THREAD_POOL_SIZE>1?0:MONITOR_READ_THREAD_POOL_SIZE;
	}
	public static int getMONITOR_WRITE_THREAD_POOL_SIZE(){
		return MONITOR_READ_WRITE_THREAD_POOL_SIZE>1?0:MONITOR_WRITE_THREAD_POOL_SIZE;
	}
	public static int getMONITOR_READ_WRITE_THREAD_POOL_SIZE(){
		return MONITOR_READ_WRITE_THREAD_POOL_SIZE;
	}
	public static ActionOnPoolNoIdleSpace getACTION_ON_POOL_EMPTY(){
		return ACTION_ON_POOL_EMPTY;
	}
	public static String getSERVERHOST(){
		return SERVERHOST;
	}
	public static int getSOCKETCHANNEL_TIMEOUT(){
		return SOCKETCHANNEL_TIMEOUT;
	}
	public static boolean getRECYCLE_IN_CHANNEL_GETTER(){
		return RECYCLE_IN_CHANNEL_GETTER;
	}
}
