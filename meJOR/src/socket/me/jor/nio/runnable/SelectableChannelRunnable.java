package me.jor.nio.runnable;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

import me.jor.common.GlobalObject;
import me.jor.pool.TransferActor;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;

public class SelectableChannelRunnable implements Runnable{
	private static final Log log=Log4jUtil.getLog(SelectableChannelRunnable.class);
	private SelectionKey key;
	
	public SelectableChannelRunnable(SelectionKey key){
		this.key=key;
	}
	
	@Override
	public void run() {
		try{
			if(key.isReadable()){
				if(cancelOp(SelectionKey.OP_READ)){
					handleRead();
				}
			}else if(key.isWritable()){
				if(cancelOp(SelectionKey.OP_WRITE)){
					handleWrite();
				}
			}else if(key.isConnectable()){
				if(cancelOp(SelectionKey.OP_CONNECT)){
					handleConnect();
					
				}
			}else if(key.isAcceptable()){
				if(cancelOp(SelectionKey.OP_ACCEPT)){
					handleAccept();
				}
			}
		}catch(IOException e){
			log.error("SelectableChannelRunnable.run() - ",e);
		}finally{
			try {
				key.channel().close();
			} catch (IOException e) {}
			key.cancel();
		}
	}
	private void handleRead() throws IOException{
		((TransferActor)key.attachment()).transfer((ReadableByteChannel)key.channel());
	}
	private void handleWrite() throws IOException{
		((TransferActor)key.attachment()).transfer((WritableByteChannel)key.channel());
	}
	private void handleConnect() throws IOException{
		SocketChannel channel=(SocketChannel)key.channel();
		if(!channel.finishConnect()){
			registOp(SelectionKey.OP_CONNECT);
		}else{
			channel.configureBlocking(false);
		}
	}
	private void handleAccept() throws IOException{
		//使用单独的类处理accept，不在此类中处理
		GlobalObject.getExecutorService().execute(new Runnable(){
			@Override
			public void run() {
				try{
					SocketChannel channel=((ServerSocketChannel)key.channel()).accept();
					if(channel!=null){
						channel.configureBlocking(false);
					}
					
				}catch(IOException e){
					log.error("SelectableChannelRunnable.handleAccept() - ", e);
				}
			}
		});
	}
	private boolean cancelOp(int op){
		if((key.interestOps() & op)>0){
			synchronized(key){
				if((key.interestOps() & op)>0){
					key.interestOps(key.interestOps()^op);
					return true;
				}
			}
		}
		return false;
	}
	private void registOp(int op){
		key.interestOps(key.interestOps()|op);
	}
}
