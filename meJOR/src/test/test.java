import java.io.UnsupportedEncodingException;
import java.util.Date;

import me.jor.util.Help;

import com.opensymphony.xwork2.ActionSupport;


public class test {
	public static void main(String[] args) throws UnsupportedEncodingException {
//		System.out.println("aaaaaaaaaaaaaaaaaa");
//		System.out.println(System.class.getClassLoader());
//		System.out.println(Date.class.getClassLoader());
//		System.out.println(Help.class.getClassLoader());
//		System.out.println(ActionSupport.class.getClassLoader());
		System.out.println(new String("澶╄繍涓€鏀矾".getBytes("gbk"),"utf8"));
	}
}
