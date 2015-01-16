package me.jor.mq;

public enum Exchange {
	FANOUT("fanout"),TOPIC("topic"),DIRECT("direct"),HEADERS("headers");
	
	private String name;
	private Exchange(String name){
		this.name=name;
	}
	public String getName(){
		return name;
	}
}
