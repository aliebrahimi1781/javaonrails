package me.jor.pool;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import me.jor.nio.runnable.SelectableChannelRunnable;
import me.jor.nio.socket.constant.NIOSocketConstant;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;

public class SelectorPool {
	private static final Log log=Log4jUtil.getLog(SelectorPool.class);
	private static SelectorPool pool;
	
	private Queue<Selector> selectors;
	
	private SelectorPool(){}

	public static void createPool(final ExecutorService executorService, int poolSize) throws IOException{
		if(pool==null){
			synchronized(SelectorPool.class){
				SelectorPool pool=new SelectorPool();
				pool.selectors=new ArrayBlockingQueue<Selector>(poolSize);
				for(int i=0;i<poolSize;i++){
					final Selector selector=Selector.open();
					executorService.execute(new Runnable(){
						@Override
						public void run() {
							final int timeout=NIOSocketConstant.getSOCKETCHANNEL_TIMEOUT();
							for(;;){
								try {
									if(selector.select()>0){
										for(SelectionKey key:selector.selectedKeys()){
											new SelectableChannelRunnable(key).run();
										}
									}
								} catch (IOException e) {
									log.warn("Selector.select()",e);
								}
								executorService.execute(new Runnable(){
									public void run(){
										for(SelectionKey key:selector.keys()){
											Channel channel =key.channel();
											try{
												if(channel instanceof ChannelInPool){
													destroy((ChannelInPool)channel,timeout);
												}else if(!key.isValid()){
													channel.close();
													Object attachment=key.attachment();
													if(attachment instanceof TransferActor){
														TransferActorPool.singleInstance().recycle((TransferActor)attachment);
													}
													key.cancel();
												}
											}catch(IOException e){
												log.warn("SelectionKey.channel().close()",e);
											}
										}
									}
								});
							}
						}
						private boolean destroy(ChannelInPool channel, int timeout) throws IOException{
							return ((ChannelInPool)channel).destroy(timeout);
						}
					});
					pool.selectors.offer(selector);
				}
			}
		}
	}
	private static Selector getSelector(){
		Queue<Selector> queue=pool.selectors;
		Selector sel=queue.poll();
		queue.offer(sel);
		return sel;
	}
	public static void register(SelectableChannel channel, int ops) throws IOException{
		wrapChannel(channel).register(getSelector(), ops);
	}
	public static void register(SelectableChannel channel, int ops, Object att) throws IOException{
		wrapChannel(channel).register(getSelector(), ops, att);
	}
	private static SelectableChannel wrapChannel(SelectableChannel channel) throws IOException{
		if(!(channel instanceof ChannelInPool)){
			return new ChannelInPool((SocketChannel)channel);
		}else{
			return channel;
		}
	}
	
	public void destroy(){
		for(;!selectors.isEmpty();){
			try {
				selectors.poll().close();
			} catch (IOException e) {}
		}
	}
}
