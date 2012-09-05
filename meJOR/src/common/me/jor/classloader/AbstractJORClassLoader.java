package me.jor.classloader;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

import me.jor.util.LockCache;


public abstract class AbstractJORClassLoader extends SecureClassLoader{
	private String startClassName;
	private boolean startClassInCustomPath;
	AbstractJORClassLoader(ClassLoader parent, String startClassName, boolean startClassInCustomPath){
		super(parent);
		this.startClassName=startClassName;
		this.startClassInCustomPath=startClassInCustomPath;
	}
	
	protected abstract InputStream getBytecodeInputStream(String name) throws IOException;
	protected abstract URL findJORResource(String name);
	
	private byte[] loadBytecode(InputStream in) throws IOException{
		ByteArrayOutputStream baos=null;
		try{
			baos=new ByteArrayOutputStream();
			byte[] b=new byte[1024];
			for(int r=0;;){
				r=in.read(b);
				if(r>=0){
					baos.write(b,0,r);
				}else{
					return baos.toByteArray();
				}
			}
		}finally{
			in.close();
			baos.close();
		}
	}
	private byte[] getBytecode(String name) throws IOException{
		if((!name.equals(startClassName)) || startClassInCustomPath){
			return loadBytecode(getBytecodeInputStream(name));
		}else{
			return loadBytecode(super.getParent().getResourceAsStream(convertPackagePath(name)));
		}
	}
	
	private Class<?> findJORClass(String name) throws IOException, ClassFormatError{
		Lock lock=LockCache.getReentrantLock("me.jor.classloader.JORClassLoader-"+name);
		try{
			lock.lock();
			Class c=super.findLoadedClass(name);
			if(c==null){
				byte[] bytecode=getBytecode(name);
				c=super.defineClass(name, bytecode, 0, bytecode.length);
			}
			return c;
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public Class<?> loadClass(String name)throws ClassNotFoundException{
		try{
			Class c=super.findLoadedClass(name);
			if(c==null){
				c=findJORClass(name);
			}
			return c;
		}catch(IOException e){
			return super.getParent().loadClass(name);
		}
	}
	
	protected String convertPackagePath(String className){
		return className.endsWith(".class")?className:className.replaceAll("\\.", "/")+".class";
	}
	
	@Override
	protected URL findResource(String name){
		if(!name.startsWith("/")){
			return findJORResource(name);
		}else{
			return null;
		}
	}
	@Override
	protected Enumeration<URL> findResources(String name){
		final URL url=findResource(name);
		if(url==null){
			return new Enumeration<URL>(){
				@Override
				public boolean hasMoreElements() {
					return false;
				}
				@Override
				public URL nextElement() {
					throw new NoSuchElementException();
				}
			};
		}else{
			return new Enumeration<URL>(){
				private boolean more=true;
				@Override
				public boolean hasMoreElements() {
					return more;
				}

				@Override
				public URL nextElement() {
					if(more){
						more=false;
						return url;
					}else{
						throw new NoSuchElementException();
					}
				}
			};
		}
	}
}
