import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import me.jor.util.Help;


public class SVNClear {
	public static void main(String[] args) throws IOException{
		if(Help.isEmpty(args)){
			throw new IllegalArgumentException("args should be -co(copy)|-mv(move)|-cl(clear) SRCPATH DSTPATH");
		}
		String op=args[0];
		if(args.length<2){
			throw new IllegalArgumentException("src path should be specified");
		}
		File src=new File(args[1]);
		if(src.isFile()){
			throw new IllegalArgumentException("src should be a directory");
		}
		if(op.equals("-cl")){
			for(File f:src.listFiles(new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory() && pathname.getName().equals(".svn");
				}
			})){
				f.delete();
			}
		}else if(op.equals("-co") || op.equals("-mv")){
			if(args.length<3){
				throw new IllegalArgumentException("dest path should be specified on copying or moving");
			}
			File dst=new File(args[2]);
			if(!dst.exists() || dst.isFile()){
				dst.mkdirs();
			}
			for(File f:src.listFiles(new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					return !(pathname.isDirectory() && pathname.getName().equals(".svn"));
				}
			})){
				if(f.isDirectory()){
					main(new String[]{op,new File(dst,f.getName()).getAbsolutePath(),f.getAbsolutePath()});
				}else{
					BufferedInputStream bis=null;
					BufferedOutputStream bos=null;
					try{
						bis=new BufferedInputStream(new FileInputStream(f));
						bos=new BufferedOutputStream(new FileOutputStream(new File(dst,f.getName())));
						byte[] buf=new byte[8192];
						int l=0;
						while((l=bis.read(buf))>0){
							bos.write(buf,0,l);
						}
						bis.close();
						bos.close();
					}finally{
						if(bis!=null){
							try {
								bis.close();
							} catch (IOException e) {}
						}
						if(bos!=null){
							try{
								bos.close();
							}catch(IOException e){}
						}
					}
				}
			}
			if(op.equals("-mv")){
				src.delete();
			}
		}
	}
}
