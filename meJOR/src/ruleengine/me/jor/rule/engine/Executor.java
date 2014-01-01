package me.jor.rule.engine;


public class Executor<P,R>{
	private Task<P,R> meeting;
	private Task<P,R> notMeeting;
	private Rule rule;
	public Executor(Task<P,R> meeting,Task<P,R> notMeeting, Rule rule){
		this.meeting=meeting;
		this.notMeeting=notMeeting;
		this.rule=rule;
	}
	
	public R execute(P p,Object o){
		if(rule.charge(o)){
			return meeting!=null?meeting.execute(p):null;
		}else{
			return notMeeting!=null?notMeeting.execute(p):null;
		}
	}

	public Task<P, R> getMeeting() {
		return meeting;
	}

	public void setMeeting(Task<P, R> meeting) {
		this.meeting = meeting;
	}

	public Task<P, R> getNotMeeting() {
		return notMeeting;
	}

	public void setNotMeeting(Task<P, R> notMeeting) {
		this.notMeeting = notMeeting;
	}

	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}
}
