package me.jor.net.test;

import java.net.Socket;

public class SocketTest {
	public static void main(String[] args) throws Exception {
		Socket s=new Socket("localhost",8000);
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
