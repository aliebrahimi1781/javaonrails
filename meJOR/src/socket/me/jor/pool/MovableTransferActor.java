package me.jor.pool;

import java.nio.ByteBuffer;

public class MovableTransferActor extends TransferActor implements Comparable<MovableTransferActor>{
	private int position;
	private int capacity;
	MovableTransferActor(int position, ByteBuffer buffer) {
		super(buffer);
		this.position=position;
		this.capacity=buffer.capacity();
	}
	
	void move(int position, ByteBuffer buffer){
		synchronized(this){
			this.position=position;
			ByteBuffer origine=super.buffer;
			int originePosition=origine.position();
			byte[] datas=new byte[origine.limit()];
			origine.position(0);
			origine.get(datas);
			buffer.clear();
			buffer.put(datas);
			buffer.position(originePosition);
			super.buffer=buffer;
		}
	}
	int position(){
		return this.position;
	}
	
	@Override
	void reinstate(){
		synchronized(this){
			super.reinstate();
			super.buffer=null;
		}
	}
	
	boolean isGabage(){
		return super.buffer==null;
	}
	
	@Override
	public int compareTo(MovableTransferActor o) {
		return this.position-o.position;
	}
	@Override
	public boolean equals(Object o){
		if(o!=null && o instanceof MovableTransferActor){
			return this.position==((MovableTransferActor)o).position;
		}else{
			return false;
		}
	}
	@Override
	public int hashCode(){
		return position;
	}
}
