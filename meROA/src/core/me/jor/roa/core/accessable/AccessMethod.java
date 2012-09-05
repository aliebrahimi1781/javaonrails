package me.jor.roa.core.accessable;

import me.jor.roa.exception.NoSuchEnumValueException;

public enum AccessMethod {
	C(0),R(1),U(2),D(3);
	private int type;
	private AccessMethod(int type){
		this.type=type;
	}
	public int getType(){
		return type;
	}

	public static AccessMethod get(int type){
		AccessMethod[] ats=AccessMethod.values();
		for(int i=0,l=ats.length;i<l;i++){
			AccessMethod at=ats[i];
			if(at.type==type){
				return at;
			}
		}
		throw new NoSuchEnumValueException(type);
	}
}
