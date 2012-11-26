package me.jor.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jor.exception.SqlGeneratorException;

import org.hibernate.Query;
import org.hibernate.Session;

public class HqlAndSqlGenerator extends SqlGenerator{
	protected Map<String,Object> namedParams=new HashMap<String,Object>();
	public void set(String field, String paramName, Object value){
		switch(state){
		case SET:
			sql.append(" set ").append(field).append("=:").append(paramName).append(',');
			namedParams.put(paramName, value);
		default:
			throw new SqlGeneratorException("SET clause could follow UPDATE only");
		}
	}
	private void op(String field,String paramName, Object value, boolean notEmpty, String op){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			sql.append(field).append(op).append(':').append(paramName).append(' ');
			namedParams.put(paramName,value);
		}
	}
	public void eq(String field,String paramName, Object value, boolean notEmpty){
		op(field,paramName,value,notEmpty,"=");
	}
	public void lt(String field,String paramName, Object value, boolean notEmpty){
		op(field,paramName,value,notEmpty,"<");
	}
	public void gt(String field,String paramName, Object value, boolean notEmpty){
		op(field,paramName,value,notEmpty,">");
	}
	public void le(String field,String paramName, Object value, boolean notEmpty){
		op(field,paramName,value,notEmpty,"<=");
	}
	public void ge(String field,String paramName, Object value, boolean notEmpty){
		op(field,paramName,value,notEmpty,">=");
	}
	public void ne(String field,String paramName, Object value, boolean notEmpty){
		op(field,paramName,value,notEmpty,"<>");
	}
	public void in(String field, String paramName, Object[] value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append("in(:").append(paramName).append(')');
			namedParams.put(paramName,value);
		}
	}
	public void in(String field, String paramName, Collection value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append("in(:").append(paramName).append(')');
			namedParams.put(paramName,value);
		}
	}
	public void like(String field, String paramName, String value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append(" like '%'||:").append(paramName).append("||'%' ");
			namedParams.put(paramName,value);
		}
	}
	public void leftLike(String field, String paramName, String value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append(" like '%'||:").append(paramName);
			namedParams.put(paramName,value);
		}
	}
	public void rightLike(String field, String paramName, String value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append(" like :").append(paramName).append("||'%' ");
			namedParams.put(paramName,value);
		}
	}
	public Map<String,Object> getNamedParmas(){
		return namedParams;
	}
	private Query populateQuery(Query query, int... pageParams){
		for(int i=0,l=params.size();i<l;i++){
			Object p=params.get(i);
			if(p instanceof String){
				query.setString(i, (String)p);
			}else if(p instanceof Integer){
				query.setInteger(i, (Integer)p);
			}else if(p instanceof Long){
				query.setLong(i, (Long)p);
			}else if(p instanceof Float){
				query.setFloat(i, (Float)p);
			}else if(p instanceof Double){
				query.setDouble(i, (Double)p);
			}else if(p instanceof BigDecimal){
				query.setBigDecimal(i, (BigDecimal)p);
			}else if(p instanceof BigInteger){
				query.setBigInteger(i, (BigInteger)p);
			}else if(p instanceof Date){
				query.setDate(i, (Date)p);
			}
		}
		for(Map.Entry<String, Object> entry:namedParams.entrySet()){
			String n=entry.getKey();
			Object v=entry.getValue();
			if(v instanceof String){
				query.setString(n, (String)v);
			}else if(v instanceof Integer){
				query.setInteger(n, (Integer)v);
			}else if(v instanceof Long){
				query.setLong(n, (Long)v);
			}else if(v instanceof Float){
				query.setFloat(n, (Float)v);
			}else if(v instanceof Double){
				query.setDouble(n, (Double)v);
			}else if(v instanceof BigDecimal){
				query.setBigDecimal(n, (BigDecimal)v);
			}else if(v instanceof BigInteger){
				query.setBigInteger(n, (BigInteger)v);
			}else if(v instanceof Date){
				query.setDate(n, (Date)v);
			}else if(v.getClass().isArray()){
				query.setParameterList(n, (Object[])v);
			}else if(v instanceof Collection){
				query.setParameterList(n, (Collection)v);
			}
		}
		if(pageParams.length>0){
			int pageSize=pageParams[1];
			query.setFirstResult((pageParams[0]-1)*pageSize).setMaxResults(pageSize);
		}
		return query;
	}
	public int executeUpdateSqlQuery(Session session){
		return populateQuery(session.createSQLQuery(getSql())).executeUpdate();
	}
	public int executeUpdateHqlQuery(Session session){
		return populateQuery(session.createQuery(getSql())).executeUpdate();
	}
	public <T> List<T> listSqlQuery(Session session){
		return populateQuery(session.createSQLQuery(getSql())).list();
	}
	public <T> List<T> listSqlQuery(Session session, int page, int pageSize){
		return populateQuery(session.createSQLQuery(getSql()),page,pageSize).list();
	}
	public <T> T uniqueSqlQuery(Session session){
		return (T)populateQuery(session.createSQLQuery(getSql())).uniqueResult();
	}
	public <T> List<T> listHqlQuery(Session session){
		return populateQuery(session.createQuery(getSql())).list();
	}
	public <T> List<T> listHqlQuery(Session session,int page,int pageSize){
		return populateQuery(session.createQuery(getSql()),page,pageSize).list();
	}
	public <T> T uniqueHqlQuery(Session session){
		return (T)populateQuery(session.createQuery(getSql())).uniqueResult();
	}
	
}
