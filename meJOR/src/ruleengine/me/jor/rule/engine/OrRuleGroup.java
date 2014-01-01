package me.jor.rule.engine;

public class OrRuleGroup extends RuleGroup{
	@Override
	public boolean charge(Object o){
		boolean result=false;
		RuleChain rule=null;
		if(!(result=super.charge(o))){
			rule=super.next();
			while(rule!=null && !(result=rule.charge(o))){
				rule=super.next();
			}
		}
		return result;
	}
}
