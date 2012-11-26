import java.util.Date;

import me.jor.util.Help;

import com.opensymphony.xwork2.ActionSupport;


public class test {
	public static void main(String[] args) {
		System.out.println("aaaaaaaaaaaaaaaaaa");
		System.out.println(System.class.getClassLoader());
		System.out.println(Date.class.getClassLoader());
		System.out.println(Help.class.getClassLoader());
		System.out.println(ActionSupport.class.getClassLoader());
	}
}
