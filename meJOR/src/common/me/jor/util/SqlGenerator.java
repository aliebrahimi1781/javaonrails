package me.jor.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.jor.exception.SqlGeneratorException;

public class SqlGenerator {
	protected StringBuilder sql;
	protected List params=new ArrayList();
	protected SqlState state=SqlState.START;
	protected SqlState prevState;
	
	protected enum SqlState{
		START,OPEN_BRACKET,SELECT,DELETE,UPDATE,SET,FROM,WHERE
	};
	
	public SqlGenerator(){}
	public void insert(String entity, String[] fields,String [] values){
		sql.append("insert into ").append(entity).append('(');
		int l=fields.length;
		for(int i=0;i<l-1;i++){
			sql.append(fields[i]).append(',');
			params.add(values[i]);
		}
		sql.append(fields[l-1]);
		sql.append(")values(").append(Help.concat("?", l, ",")).append(')');
	}
	public void insert(String entity, Map<String,String> values){
		sql.append("insert into ").append(entity).append('(');
		for(Map.Entry<String, String> entry:values.entrySet()){
			sql.append(entry.getKey()).append(',');
			params.add(entry.getValue());
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")values(").append(Help.concat("?", values.size(), ",")).append(')');
	}
	public void select(String... select){
		switch(state){
		case START:case OPEN_BRACKET:
			sql.append("select ");
			for(int i=0,l=select.length;i<l;i++){
				sql.append(select).append(' ');
			}
			state=SqlState.SELECT;
			break;
		default:
			throw new SqlGeneratorException("SELECT clause could not follow DELETE, UPDATE, FROM OR WHERE");
		}
	}
	public void update(String entity){
		switch(state){
		case START:
			sql.append("update ").append(entity);
			state=SqlState.SET;
		default:
			throw new SqlGeneratorException("UPDATE clause could not follow any clause");
		}
	}
	public void delete(String entity){
		switch(state){
		case START:
			sql.append("delete from ").append(entity);
			state=SqlState.FROM;
		default:
			throw new SqlGeneratorException("DELETE clause could not follow any clause");
		}
	}
	public void set(String field, Object value){
		switch(state){
		case SET:
			sql.append("set ").append(field).append("=?,");
			params.add(value);
		default:
			throw new SqlGeneratorException("SET clause could follow UPDATE only");
		}
	}
	
	public void set(String field, SqlGenerator subsql){
		switch(state){
		case SET:
			sql.append("set ").append(field).append("=").append(subsql.getSql()).append(',');
		default:
			throw new SqlGeneratorException("SET clause could follow UPDATE only");
		}
	}
	private void appendKeyword(String keyword){
		if(sql.indexOf(keyword)>=0){
			sql.append(keyword);
		}
	}
	private void appendFrom(){
		appendKeyword("from ");
	}
	public void from(String... from){
		switch(state){
		case START:case SELECT:
			appendFrom();
			for(int i=0,l=from.length;i<l;i++){
				sql.append(from).append(' ');
			}
			state=SqlState.FROM;
			break;
		default:
			throw new SqlGeneratorException("from clause could not follow UPDATE, WHERE");
		}
	}
	public void from(SqlGenerator sql){
		switch(state){
		case START:case SELECT:case DELETE:
			appendFrom();
			this.sql.append(sql.getSql()).append(' ');
			state=SqlState.FROM;
			break;
		default:
			throw new SqlGeneratorException("FROM clause could not follow WHERE");
		}
	}
	public void from(SqlGenerator sql, String as){
		from(sql);
		this.as(as);
	}
	public void as(String as){
		this.sql.append(" as ").append(as).append(' ');
	}
	
	public void where(){
		switch(state){
		case START:case FROM:case SET:
			int l=sql.length()-1;
			if(sql.lastIndexOf(",")==l){
				sql.deleteCharAt(l);
			}
			appendKeyword(" where ");
			break;
		default:
			throw new SqlGeneratorException("WHERE clause could follow FROM OR SET only");
		}
	}
	private void op(String field,Object value, boolean notEmpty, String op){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			sql.append(field).append(op).append("? ");
			params.add(value);
		}
	}
	
	public void eq(String field,Object value,boolean notEmpty){
		op(field,value,notEmpty,"=");
	}
	
	public void lt(String field,Object value,boolean notEmpty){
		op(field,value,notEmpty,"<");
	}
	
	public void gt(String field,Object value,boolean notEmpty){
		op(field,value,notEmpty,">");
	}
	
	public void le(String field,Object value,boolean notEmpty){
		op(field,value,notEmpty,"<=");
	}
	
	public void ge(String field,Object value,boolean notEmpty){
		op(field,value,notEmpty,">=");
	}
	
	public void ne(String field,Object value,boolean notEmpty){
		op(field,value,notEmpty,"<>");
	}
	
	public void isNull(String field){
		sql.append(field).append(" is null ");
	}
	public void isNotNull(String field){
		sql.append(field).append(" is not null ");
	}
	
	private void op(String field,SqlGenerator sql,String op){
		this.sql.append(field).append(op).append('(').append(sql.getSql()).append(')');
		
	}
	public void eq(String field,SqlGenerator sql){
		op(field,sql,"=");
	}
	public void lt(String field,SqlGenerator sql){
		op(field,sql,"<");
	}
	public void gt(String field,SqlGenerator sql){
		op(field,sql,">");
	}
	public void le(String field,SqlGenerator sql){
		op(field,sql,"<=");
	}
	public void ge(String field,SqlGenerator sql){
		op(field,sql,">=");
	}
	
	public void in(String field, Object[] value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append("in(").append(Help.concat("?", value.length, ",")).append(')');
			params.addAll(Arrays.asList(value));
		}
	}
	public void in(String field, Collection value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append("in(").append(Help.concat("?", value.size(), ",")).append(')');
			params.addAll(value);
		}
	}
	
	public void like(String field, String value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append(" like '%'||?||'%' ");
			params.add(value);
		}
	}
	
	public void leftLike(String field, String value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append(" like '%'||? ");
			params.add(value);
		}
	}
	
	public void rightLike(String field, String value, boolean notEmpty){
		boolean empty=Help.isEmpty(value);
		if((notEmpty && !empty) || (!notEmpty && empty)){
			this.sql.append(field).append(" like ?||'%' ");
			params.add(value);
		}
	}
	
	public void and(){
		sql.append(" and ");
	}
	public void or(){
		sql.append(" or ");
	}
	public void openBracket(){
		sql.append('(');
		prevState=state;
		state=SqlState.OPEN_BRACKET;
	}
	public void closeBracket(){
		sql.append(')');
		state=prevState;
		prevState=null;
	}
	public void comma(){
		sql.append(',');
	}
	public String getSql(){
		return sql.toString();
	}
	public List getParams(){
		return params;
	}
	
	public Object execute(Connection connection) throws SQLException{
		String sql=getSql();
		PreparedStatement statement=connection.prepareStatement(sql);
		for(int i=0,l=params.size();i<l;i++){
			Object p=params.get(i);
			if(p instanceof String){
				statement.setString(i+1, (String)p);
			}else if(p instanceof Integer){
				statement.setInt(i+1, (Integer)p);
			}else if(p instanceof Long){
				statement.setLong(i+1, (Long)p);
			}else if(p instanceof Float){
				statement.setFloat(i+1, (Float)p);
			}else if(p instanceof Double){
				statement.setDouble(i+1, (Double)p);
			}else if(p instanceof BigDecimal){
				statement.setBigDecimal(i+1, (BigDecimal)p);
			}else if(p instanceof Date){
				statement.setDate(i+1, new java.sql.Date(((Date)p).getTime()));
			}
		}
		if(sql.startsWith("select")){
			return statement.executeQuery();
		}else{
			return statement.executeUpdate();
		}
	}
	public ResultSet executeQuery(Connection connection) throws SQLException{
		return (ResultSet)execute(connection);
	}
	public int executeUpdate(Connection connection) throws SQLException{
		return (Integer)execute(connection);
	}
}
