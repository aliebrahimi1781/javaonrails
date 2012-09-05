package me.jor.nio.client;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import me.jor.common.GlobalObject;
import me.jor.nio.socket.constant.NIOSocketConstant;
import me.jor.pool.ChannelInPool;
import me.jor.pool.SocketChannelPool;

/**
 * configure file path: /conf/SocketChannelPool.properties
 * format host.0=192.168.1.1,1234
 *        host.1=192.168.1.2,5678
 * @author running
 *
 */
public class SocketChannelGetter {
	private static class HostEntry{
		public String host;
		public int port;
		public SocketChannelPool pool;
		public HostEntry(String host, int port, int timeout){
			this.host=host;
			this.port=port;
			pool=new SocketChannelPool(timeout,false);
		}
		public SelectableChannel getChannel() throws IOException{
			SelectableChannel channel=pool.getChannel();
			if(channel==null){
				return new ChannelInPool(host, port);
			}else{
				return channel;
			}
		}
		public void recycle(ChannelInPool inpool) throws IOException{
			pool.recycle(inpool);
		}
	}
	private static Queue<HostEntry> hosts;
	private static void init(){
		String[] hostGroup=NIOSocketConstant.getSERVERHOST().split(",");
		int l=hostGroup.length;
		Queue<HostEntry> hes=SocketChannelGetter.hosts=new ArrayBlockingQueue<HostEntry>(l);
		final int timeout=NIOSocketConstant.getSOCKETCHANNEL_TIMEOUT();
		for(int i=0;i<l;i++){
			String[] he=hostGroup[i].split(":");
			hes.offer(new HostEntry(he[0],Integer.parseInt(he[1]),timeout));
		}
		if(NIOSocketConstant.getRECYCLE_IN_CHANNEL_GETTER()){
			GlobalObject.getExecutorService().execute(new Runnable(){
				@Override
				public void run() {
					int wait=timeout<60000?60000:timeout;
					for(;;){
						synchronized(this){
							try {
								this.wait(wait);
								for(HostEntry entry:SocketChannelGetter.hosts){
									entry.pool.recycle();
								}
							} catch (InterruptedException e) {}
						}
					}
				}});
		}
	}
	static{
		init();
	}
	private static SelectableChannel getChannelPure() throws IOException{
		HostEntry entry=hosts.poll();
		hosts.offer(entry);
		SelectableChannel channel= entry.getChannel();
		if(channel instanceof ChannelInPool){
			((ChannelInPool)channel).cancelKey();
		}
		return channel;
	}
	public static SelectableChannel getChannel() throws IOException{
		SelectableChannel channel=getChannelPure();
		if(channel instanceof ChannelInPool){
			((ChannelInPool)channel).cancelKey();
		}
		return channel;
	}
	public static SelectableChannel getChannel(int ops) throws IOException{
		SelectableChannel channel=getChannelPure();
		if(channel instanceof ChannelInPool){
			((ChannelInPool)channel).register(ops);
		}
		return channel;
	}
	public static SelectableChannel getChannel(int ops, Object att) throws IOException{
		SelectableChannel channel=getChannelPure();
		if(channel instanceof ChannelInPool){
			((ChannelInPool)channel).register(ops, att);
		}
		return channel;
	}
	public static void recycle(Channel channel) throws IOException{
		if(channel instanceof ChannelInPool){
			ChannelInPool cip=(ChannelInPool) channel;
			synchronized(cip){
				cip.cancelKey();
				String host=cip.getHost();
				int port=((ChannelInPool) channel).getPort();
				for(HostEntry entry:hosts){
					if(entry.host.equals(host) && entry.port==port){
						entry.recycle((ChannelInPool)channel);
						break;
					}
				}
			}
		}
	}
}