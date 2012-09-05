package me.jor.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import me.jor.nio.Starter;
import me.jor.nio.runnable.ExecutionAfterReading;

public class SocketChannelAccepter {
	public static void start(int port, int backlog, ExecutionAfterReading task, Class dstType) throws IOException{
		ServerSocketChannel server=ServerSocketChannel.open();
		server.socket().bind(new InetSocketAddress(port), backlog);
		for(SocketChannel channel=null;(channel=server.accept())!=null;){
			Starter.transfer(channel,task, dstType);
		}
	}
	public static void start(String port, String backlog, String taskClass, String dstType) throws NumberFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		start(Integer.parseInt(port), Integer.parseInt(backlog), 
				(ExecutionAfterReading)Class.forName(taskClass).newInstance(), Class.forName(dstType));
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		start(args[0],args[1],args[2],args[3]);
	}
}
