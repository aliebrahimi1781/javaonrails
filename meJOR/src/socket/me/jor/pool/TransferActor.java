package me.jor.pool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;

import me.jor.common.GlobalObject;
import me.jor.exception.JsonGenerationException;
import me.jor.nio.runnable.ExecutionAfterReading;
import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;

public class TransferActor{
	private static final Log log=Log4jUtil.getLog(TransferActor.class);
	
	protected ByteBuffer buffer;
	private Object data;
	private long dataIdx;
	private long length;
	private Class dstType;
	private ExecutionAfterReading task;
	private boolean toWriteAfterTask;
	
	TransferActor(ByteBuffer buffer){
		this.buffer=buffer;
	}
	public int capacity(){
		return buffer.capacity();
	}
	public long dataSize(){
		return length;
	}
	private boolean hasDataOutOfBuffer(){
		return data!=null && (dataIdx<length || data instanceof ReadableByteChannel);
	}
	public boolean started(){
		return data!=null;
	}
	boolean hasRemaining(){
		return hasDataOutOfBuffer() || buffer.hasRemaining();
	}
	public boolean finished(){
		return !hasRemaining();
	}
	private void register(Channel channel, int ops) throws IOException{
		if(channel instanceof ChannelInPool){
			ChannelInPool cip=((ChannelInPool)channel);
			cip.register(ops, this);
		}else if(channel instanceof SelectableChannel && channel.isOpen()){
			SelectorPool.register((SelectableChannel)channel, ops,this);
		}
	}
	private boolean refreshBuffer(){
		if(!buffer.hasRemaining()){
			buffer.clear();
			buffer.mark();
			return true;
		}else if(hasDataOutOfBuffer() || data instanceof ByteArrayOutputStream || data instanceof WritableByteChannel){
			int capacity=buffer.capacity();
			int limit=buffer.limit();
			if(capacity>limit){
				buffer.mark();
				buffer.limit(capacity);
				buffer.position(limit);
				return true;
			}
		}
		return false;
	}
	private boolean loadByteBufferBeforeWriting() throws IOException{
		if(hasDataOutOfBuffer() && refreshBuffer()){
			if(dataIdx<length){
				int position=0;
				int limit=0;
				Buffer buf=null;
				buf=buffer;
				position=buf.position();
				limit=buf.limit();
				buffer.put((byte[])data,(int)dataIdx,limit);
				dataIdx+=buf.position()-position;
			}else if(data!=null && data instanceof ReadableByteChannel){
				int read=((ReadableByteChannel)data).read(buffer);
				if(read>0){
					dataIdx+=read;
					if(length>0){
						if(dataIdx==length){
							data=null;
						}else if(dataIdx>length){
							dataIdx-=length;
							if(dataIdx>=8){
								refreshLength(buffer.position()-(int)dataIdx);
							}
						}
					}else if(length==0 && read>=8){
						refreshLength(0);
					}
				}
			}
			buffer.limit(buffer.position());
			buffer.reset();
		}
		return hasRemaining();
	}
	private void refreshLength(int startPos){
		int current=buffer.position();
		buffer.position(startPos);
		length=buffer.asLongBuffer().get(0);
		buffer.position(current);
	}
	public TransferActor startTransfer(byte... data){
		this.dataIdx=0;
		this.length=data.length;
		buffer.clear();
		buffer.putLong(data.length);
		this.data=data;
		return this;
	}
	public TransferActor startTransfer(InputStream data){
		return startTransfer(Channels.newChannel(data));
	}
	public TransferActor startTransfer(ReadableByteChannel data){
		return startTransfer((Channel)data);
	}
	public TransferActor startTransfer(WritableByteChannel data){
		return startTransfer((Channel)data);
	}
	private TransferActor startTransfer(Channel data){
		this.data=data;
		this.dataIdx=0;
		return this;
	}
	/**
	 * 把data转成json串，再转成utf8字节
	 * @param dst
	 * @param data
	 * @throws JsonGenerationException 
	 */
	public TransferActor startTransfer(Object data){
		try{
			return startTransfer(GlobalObject.getJsonMapper().writeValueAsBytes(data));
		}catch(Exception e){
			throw new JsonGenerationException(e);
		}
	}
	public <E> TransferActor startTransfer(Class<E> dstType){
		this.dstType=dstType;
		this.data=new ByteArrayOutputStream();
		return this;
	}
	public TransferActor startTransfer(ExecutionAfterReading task, Object accessData, Class dstType, boolean toWriteAfterTask){
		this.dstType=dstType;
		this.task=task;
		this.toWriteAfterTask=toWriteAfterTask;
		try {
			if(accessData instanceof String){
				this.data=((String)accessData).getBytes("utf8");
			}else{
				this.data=GlobalObject.getJsonMapper().writeValueAsBytes(accessData);
			}
		} catch (Exception e) {
			throw new JsonGenerationException(e);
		}
		this.dataIdx=0;
		return this;
	}
	public TransferActor startTransfer(ExecutionAfterReading task, Class dstType, boolean toWriteAfterTask) {
		this.dstType=dstType;
		this.task=task;
		this.toWriteAfterTask=toWriteAfterTask;
		this.data=new ByteArrayOutputStream();
		return this;
	}
	public <E> E transfer(ReadableByteChannel src) throws IOException{
		int read=0;
		while(refreshBuffer()){
			read=src.read(buffer);
			if(read>0){
				dataIdx+=read;
				if(length>0 && dataIdx>=length){
					break;
				}else if(length==0 && dataIdx>=8){
					dataIdx-=8;
					refreshLength(0);
				}
				if(buffer.hasRemaining()){
					this.register(src, SelectionKey.OP_READ);
					return null;
				}else{
					buffer.limit(buffer.position());
					buffer.reset();
					populate();
				}
			}
		}
		
		if(data instanceof ByteArrayOutputStream){
			byte[] buf=((ByteArrayOutputStream)data).toByteArray();
			E result=null;
			try{
				if(dstType!=null){
					result=(E)GlobalObject.getJsonMapper().readValue(buf, 8, (int)length, dstType);
				}else{
					result=(E)buf;
				}
				return result;
			}finally{
				this.reinstate();
				if(task!=null){
					try {
						result=this.task.setAccessData(result).execute();
					} catch(Throwable t){
						log.error("TransferActor.transfer(ReadableByteChannel) - this.task.execute()",t);
					}
				}
				if(this.toWriteAfterTask){
					try{
						if(result!=null){
							data=GlobalObject.getJsonMapper().writeValueAsBytes(result);
							register(src, SelectionKey.OP_WRITE);
						}
					}catch(Exception e){
						log.error("TransferActor.transfer(ReadableByteChannel) - GlobalObject.getJsonMapper().writeValueAsBytes()",e);
					}
				}else{
					buffer.put(buf,(int)length,buf.length);
					dataIdx=buffer.position();
					data=new ByteArrayOutputStream();
				}
			}
		}
		register(src, SelectionKey.OP_READ);
		return null;
	}
	private void populate() throws IOException{
		if(data instanceof ByteArrayOutputStream){
			byte[] buf=new byte[buffer.limit()-buffer.position()];
			buffer.get(buf);
			((ByteArrayOutputStream)data).write(buf);
		}else if(data instanceof WritableByteChannel){
			while(buffer.hasRemaining() && ((WritableByteChannel)data).write(buffer)>0);
		}
	}
	public boolean transfer(WritableByteChannel dst) throws IOException{
		while(loadByteBufferBeforeWriting()){
			int remaining=buffer.remaining();
			if(remaining>0){
				dst.write(buffer);
				if(buffer.remaining()==remaining){
					break;
				}
			}
		}
		if(hasRemaining()){
			register(dst, SelectionKey.OP_WRITE);
			return false;
		}else{
			register(dst, SelectionKey.OP_READ);
			return true;
		}
	}
	public static void transfer(ReadableByteChannel src, File dst) throws IOException{
		transfer(src, new FileInputStream(dst).getChannel(),dst.length());
	}
	public static void transfer(ReadableByteChannel src, FileChannel dst, long count) throws IOException{
		dst.transferFrom(src, 0, count);
	}
	public static void transfer(WritableByteChannel dst, File data) throws IOException{
		transfer(dst, new FileInputStream(data).getChannel(), data.length());
	}
	public static void transfer(WritableByteChannel dst, FileChannel data, long count) throws IOException{
		data.transferTo(0, count, dst);
	}
	void reinstate(){
		this.data=null;
		this.dataIdx=-1;
		this.length=0;
		this.dstType=null;
		this.buffer.clear();
		this.task=null;
		this.toWriteAfterTask=false;
	}
	public Object getData() {
		return data;
	}
}
