package me.jor.net.test;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketTest {
	public static void main(String[] args) throws Exception{
		ServerSocket ss=new ServerSocket(8000);
		Socket s=ss.accept();
		while(true){
			System.out.println(
					s.getLocalPort()+"\r\n"+
					s.getPort()+"\r\n"+
					s.getLocalAddress()+"\r\n"+
					s.getLocalSocketAddress()+"\r\n"+
					s.getRemoteSocketAddress()+"\r\n"+
		            s.getInetAddress()+"\r\n");
			s.getOutputStream().write(1);
			s.getInputStream().read(new byte[4]);
			Thread.sleep(1000);
		}
	}
}
