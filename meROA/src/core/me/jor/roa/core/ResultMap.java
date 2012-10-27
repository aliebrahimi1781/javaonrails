package me.jor.roa.core;

import java.util.HashMap;
import java.util.Map;

import me.jor.common.GlobalObject;
import me.jor.roa.core.accessable.Result;
import me.jor.roa.exception.ResultExistingException;
/**
 * ""为key的是通用Result
 * the key which is empty string(""), is common Result
 * @author wujingrun
 *
 */
public class ResultMap implements Result{
	private Map<String, Result> map=new HashMap<String, Result>();
	
	ResultMap(){}
	
	ResultMap(String uri, Result result){
		addResult(uri, result);
	}
	
	void addResult(String uri, Result result){
		uri=uri!=null?uri:"";
		if(!map.containsKey(uri)){
			map.put(uri, result);
		}else{
			throw new ResultExistingException(uri, result, map.get(uri));  
		}
	}
	
	@Override
	public Object generate(ResourceAccessContext context) {
		Result result=map.get(context.getUri());
		return (result!=null?result:map.get("")).generate(context);
	}
	
	Result getIfSingle(){
		if(map.size()==1){
			return map.get("");
		}else{
			return null;
		}
	}

	@Override
	public Object getDescription() {
		return map;
	}
}
