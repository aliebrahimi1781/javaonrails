package me.jor.pool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class ChannelInPool extends SelectableChannel implements ByteChannel,InterruptibleChannel{
	private long startTime;
	private SocketChannel channel;
	private String host;
	private int port;
	private Selector selector;
	
	public ChannelInPool(SocketChannel channel) throws IOException{
		this.startTime=System.currentTimeMillis();
		this.channel=channel;
		channel.configureBlocking(false);
	}
	public ChannelInPool(String host, int port) throws IOException {
		this(SocketChannel.open(new InetSocketAddress(host, port)));
		this.host=host;
		this.port=port;
	}
	
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}


	SocketChannel getChannel(){
		return channel;
	}
	boolean isTimeout(int timeout){
		return System.currentTimeMillis()-this.startTime>timeout;
	}
	boolean destroy(int timeout) throws IOException{
		synchronized(this){
			boolean toDestroy=isTimeout(timeout);
			if(toDestroy){
				try{
					cancelKey();
					close();
				}finally{
					channel=null;
				}
			}
			return toDestroy;
		}
	}
	public void cancelKey(){
		if(selector != null){
			SelectionKey key=channel.keyFor(selector);
			if(key!=null){
				if(key.isValid()){
					key.cancel();
				}
				Object attachment=key.attachment();
				if(attachment instanceof TransferActor){
					TransferActorPool.singleInstance().recycle((TransferActor)attachment);
				}
			}
		}
	}
	@Override
	public int read(ByteBuffer dst) throws IOException {
		return channel.read(dst);
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		return channel.write(src);
	}

	@Override
	public SelectorProvider provider() {
		return channel.provider();
	}

	@Override
	public int validOps() {
		return channel.validOps();
	}

	@Override
	public boolean isRegistered() {
		return channel.isRegistered();
	}

	@Override
	public SelectionKey keyFor(Selector sel) {
		return channel.keyFor(sel);
	}

	@Override
	public SelectionKey register(Selector sel, int ops, Object att) throws ClosedChannelException {
		this.selector=sel;
		return channel.register(sel, ops, att);
	}
	public SelectionKey register(int ops, Object att) throws IOException{
		SelectionKey key=register(ops);
		if(key!=null){
			key.attach(att);
		}
		return key;
	}
	public SelectionKey register(int ops) throws IOException{
		SelectionKey key=channel.keyFor(selector);
		if(selector!=null){
			if(key!=null){
				key.interestOps(ops);
			}else{
				channel.register(selector, ops);
			}
		}else{
			SelectorPool.register(channel, ops);
		}
		return key;
	}

	@Override
	public SelectableChannel configureBlocking(boolean block)throws IOException {
		return channel.configureBlocking(block);
	}

	@Override
	public boolean isBlocking() {
		return channel.isBlocking();
	}

	@Override
	public Object blockingLock() {
		return channel.blockingLock();
	}

	@Override
	protected void implCloseChannel() throws IOException {
		channel.close();
	}
	public void setSelector(Selector selector){
		this.selector=selector;
	}
	public void resetStartTime(){
		startTime=System.currentTimeMillis();
	}
}