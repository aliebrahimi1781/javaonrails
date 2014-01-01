package me.jor.rule.engine;

public class AndRuleGroup extends RuleGroup{
	@Override
	public boolean charge(Object o){
		boolean result=true;
		RuleChain rule=null;
		if((result=super.charge(o))){
			rule=super.next();
			while(rule!=null && (result=rule.charge(o))){
				rule=super.next();
			}
		}
		return result;
	}
}
