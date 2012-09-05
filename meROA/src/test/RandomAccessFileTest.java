import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class RandomAccessFileTest {
	public static void main(String[] args) throws IOException {
		final File f=new File("f:\\a.txt");
		f.createNewFile();
//		final RandomAccessFile raf2=new RandomAccessFile(f,"rw");
//		raf2.seek(1000);
		new Thread(){
			public void run(){
				RandomAccessFile raf=null;
				try {
					raf = new RandomAccessFile(f,"rw");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for(int i=0;i<1000;i++){
					try {
						raf.write((byte)'a');
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		new Thread(){
			public void run(){
				RandomAccessFile raf2=null;
				try {
					raf2 = new RandomAccessFile(f,"rw");
					try {
						raf2.seek(1000);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for(int i=0;i<1000;i++){
					try {
						raf2.write((byte)'b');
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					raf2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
}
