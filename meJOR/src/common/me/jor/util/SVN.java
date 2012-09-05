package me.jor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import me.jor.common.CommonConstant;
import me.jor.common.Task;
import me.jor.util.concurrent.ExecutingOnce;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * 可执行svn commit(ci) update(up) cleanup三种操作
 * 从 *constant.properties属性文件获取用户名、密码和使用的协议 
 * 如果不指定协议则初使化svn支持的全部协议 ：svn http/https file
 * properties.dev.project.svn.protocol=svn  #svn支持的协议
 * properties.dev.project.svn.auth.user=    #svn用户名
 * properties.dev.project.svn.auth.pass=    #svn密码
 */
public class SVN {
	
	public static final String SVN_PROTOCOL="svn";//初始化svn/svn+ssh
	public static final String HTTP_PROTOCOL="http";//初始化http/https协议
	public static final String FILE_PROTOCOL="file";//初始化file协议
	
	static{
		//在javasvn api内没有公开这三个类，但这是初始化svn repository必须的
		String protocol=CommonConstant.getSVN_PROTOCOL();
		if(SVN_PROTOCOL.equals(protocol)){
			SVNRepositoryFactoryImpl.setup();//仅支持svn svn+ssh协议，如果只需要svn可以只调用它
		}else if(HTTP_PROTOCOL.equals(protocol)){
			DAVRepositoryFactory.setup();//仅支持http/https协议，如果只需要https可以只调用它
		}else if(FILE_PROTOCOL.equals(protocol)){
			FSRepositoryFactory.setup();//仅支持本地文件系统协议
		}else{
			SVNRepositoryFactoryImpl.setup();
			DAVRepositoryFactory.setup();
			FSRepositoryFactory.setup();
		}
		
		String user=CommonConstant.getSVN_AUTH_USER();
		String pass=CommonConstant.getSVN_AUTH_PASS();
		if(Help.isNotEmpty(user) && Help.isNotEmpty(pass)){
			svn=SVNClientManager.newInstance(new DefaultSVNOptions(), user, pass);
		}else{
			svn=SVNClientManager.newInstance();
		}
	}
	
	private static final SVNClientManager svn;
	
	public static final ReentrantReadWriteLock.ReadLock SVN_READ_LOCK = LockCache
			.getReadLock("common_svn_lock");
	public static final ReentrantReadWriteLock.WriteLock SVN_WRITE_LOCK = LockCache
			.getWriteLock("common_svn_lock");

	/**
	 * @throws SVNException 
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * */
	public static void ci(String target, ReentrantReadWriteLock.WriteLock lock)
			throws FileNotFoundException, SVNException {
		ci(new File(target), lock);
	}

	/**
	 * @throws SVNException 
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * */
	public static void ci(File target, ReentrantReadWriteLock.WriteLock lock)
			throws FileNotFoundException, SVNException {
		try {
			lock.lock();
			SVNWCClient wc=add(target);
			svn.getCommitClient().doCommit(
				new File[] { target },
				false,
				new StringBuilder("commit ").append(target.toString()).append(" on ").append(new Date()).toString(),
				new SVNProperties(), null, false, false,
				SVNDepth.INFINITY);
			wc.doCleanup(target);
		} finally {
			lock.unlock();
		}
	}
	
	private static SVNWCClient add(File path) throws SVNException {
		SVNWCClient wc=svn.getWCClient();
		if(path.isDirectory()){
			wc.doAdd(path, true, true, true, SVNDepth.INFINITY, true, true);
			for(File f:path.listFiles()){
				if(f.toString().indexOf(".svn")<0){
					add(f);
				}
			}
		}else{
			wc.doAdd(path, true, false, true, SVNDepth.INFINITY, true, true);
		}
		return wc;
	}

	/**
	 * ABSOLUTE_PATH,//表示待操作对象是绝对路径字符串
	 * FILE          //表示待操作对象是File对象
	 * */
	public static enum CommitType {
		ABSOLUTE_PATH, FILE;
	}
	/**
	 * @param target
	 *            接受Set<File>或Set<String>
	 * @param filePath
	 *            true:targets是目录路径，false:是目录文件对象
	 * */
	@SuppressWarnings("unchecked")
	public static void ci(Set<?> targets, CommitType type,
			ReentrantReadWriteLock.WriteLock lock) throws IOException,
			InterruptedException, SVNException {
		switch (type) {
		case FILE:
			for (File target : (Set<File>) targets) {
				ci(target, lock);
			}
			break;
		case ABSOLUTE_PATH:
			for (String target : (Set<String>) targets) {
				ci(target, lock);
			}
			break;
		}
	}
	
	public static void up(String target) throws Throwable {
		up(new File(target));
	}
	
	public static void cleanup(String target, ReentrantReadWriteLock.WriteLock lock) throws SVNException{
		cleanup(new File(target),lock);
	}
	public static void cleanup(File target, ReentrantReadWriteLock.WriteLock lock) throws SVNException{
		try{
			lock.lock();
			svn.getWCClient().doCleanup(target);
		}finally{
			lock.unlock();
		}
	}
	
	public static long up(final File target) throws Throwable {
		return ExecutingOnce.executeAndWait("SVN.up-"+target.getAbsolutePath(), new Task(){
			@Override
			public Long execute() throws SVNException, FileNotFoundException {
				svn.getWCClient().doCleanup(target);
				return svn.getUpdateClient().doUpdate(target, SVNRevision.HEAD, SVNDepth.INFINITY, true, true);
			}
		});
	}
//	/**
//	 * 只接受目录路径
//	 * @throws InterruptedException 
//	 * @throws IOException 
//	 * */
//	public static void commitSvn(String target, ReentrantReadWriteLock.WriteLock lock) throws IOException, InterruptedException{
//		commitSvn(new File(target),lock);
//	}
//	public static void commitSvn(File target,ReentrantReadWriteLock.WriteLock lock) throws IOException, InterruptedException {
//		lock.lock();
//		try{
//			if(target.exists()){
//				File parentFile=target;
//				File svnFile=new File(parentFile,"/.svn");
//				String parentName=parentFile.toString();
//				while(!svnFile.exists()){
//					parentFile=parentFile.getParentFile();
//					parentName=parentFile.toString();
//					svnFile=new File(parentFile,"/.svn");
//				}
//				String svnName=parentName+"/*";
//				Process process=new ProcessBuilder("svn","add",svnName).start();
//				BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
//				while(br.readLine()!=null);
//				int waitfor=process.waitFor();
//				if(waitfor==0){
//					process=new ProcessBuilder("svn","ci","-m","\"commiting on "+new Date()+"\"",svnName).start();
//					br=new BufferedReader(new InputStreamReader(process.getInputStream()));
//					while(br.readLine()!=null);
//					process.waitFor();
//				}
//			}else{
//				throw new FileNotFoundException(target.toString());
//			}
//		}finally{
//			lock.unlock();
//		}
//	}
//	@SuppressWarnings("unchecked")
//	public static void commitSvn(Set<?> targets, CommitType type, ReentrantReadWriteLock.WriteLock lock) throws IOException, InterruptedException {
//		switch(type){
//		case FILE:
//			for(File target:(Set<File>)targets){
//				commitSvn(target, lock);
//			}
//			break;
//		case ABSOLUTE_PATH:
//			for(String target:(Set<String>)targets){
//				commitSvn(target, lock);
//			}
//			break;
//		}
//	}
//	public static void updateSvn(String target, ReentrantReadWriteLock.WriteLock lock) throws IOException, InterruptedException {
//		updateSvn(new File(target), lock);
//	}
//	public static void updateSvn(File target, ReentrantReadWriteLock.WriteLock lock) throws IOException, InterruptedException {
//		lock.lock();
//		try{
//			if(target.exists()){
//				Process process=new ProcessBuilder("svn","up",target.toString()).start();
//				BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
//				String line=null;
//				while((line=br.readLine())!=null){Log4jUtil.log.info("svn_up_"+line);}
//				process.waitFor();
//			}else{
//				throw new FileNotFoundException(target.toString());
//			}
//		}finally{
//			lock.unlock();
//		}
//	}
//		public static void main(String[] args) throws Throwable {
//			SVN.up("E:\\workspace\\xBankManager\\WebRoot\\WEB-INF\\test.xml");
//		}
}
