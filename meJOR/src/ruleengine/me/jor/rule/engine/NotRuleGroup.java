package me.jor.rule.engine;

public class NotRuleGroup extends RuleGroup{

	@Override
	public boolean charge(Object o) {
		return !super.charge(o);
	}

}
