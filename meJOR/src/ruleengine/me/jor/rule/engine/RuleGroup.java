package me.jor.rule.engine;

public class RuleGroup implements RuleChain{
	private String name;
	private Rule rule;
	private RuleChain next;
	private RuleChain before;
	
	public RuleGroup(){}
	public RuleGroup(Rule rule){
		this.rule=rule;
	}
	@Override 
	public boolean charge(Object o){
		return rule.charge(o);
	}
	@Override
	public void setName(String name) {
		this.name=name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public RuleChain next() {
		return next;
	}
	@Override
	public RuleChain next(RuleChain chain){
		chain.next(next);
		next.before(chain);
		next=chain;
		chain.before(this);
		return this;
	}
	@Override
	public RuleChain before(){
		return before;
	}
	@Override
	public RuleChain before(RuleChain chain){
		chain.before(before);
		chain.next(this);
		before.next(chain);
		before=chain;
		return this;
	}

	@Override
	public RuleChain insertAfterName(String name, RuleChain rule) {
		if(this.name.equals(name)){
			next(rule);
		}
		return this;
	}


	@Override
	public RuleChain remove(String name) {
		if(this.name.equals(name)){
			before.next(next);
			next.before(before);
			next=before=null;
		}
		return this;
	}
}
