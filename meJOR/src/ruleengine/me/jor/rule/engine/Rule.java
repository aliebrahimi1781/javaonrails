package me.jor.rule.engine;

public interface Rule {
	public boolean charge(Object o);
	public void setName(String name);
	public String getName();
}
