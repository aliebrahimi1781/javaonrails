package me.jor.rule.engine;

public interface RuleChain extends Rule{
	public RuleChain next();
	public RuleChain next(RuleChain chain);
	public RuleChain before(RuleChain chain);
	public RuleChain before();
	public RuleChain insertAfterName(String name, RuleChain rule);
	public RuleChain remove(String name);
}
