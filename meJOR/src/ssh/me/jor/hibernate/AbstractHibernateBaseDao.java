package me.jor.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.jor.exception.CUDException;
import me.jor.exception.EmptyIDArrayException;
import me.jor.util.Help;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 基于hibernate实现的数据访问对象的公共超类。
 * 方法中涉及到字段的的，如果采用的是领域类、领域对象、hql操作，主键名、字段名、属性名指的是与主键字段对应领域类的属性；
 * 如果采用sql操作，则指的是主键字段名
 */
@SuppressWarnings("unchecked")
public abstract class AbstractHibernateBaseDao extends HibernateTemplate implements InterfaceHibernateDao {
	/**
	 * 分页查询
	 * @param countQuery 查询符合查询条件的结果集数量
	 * @param findQuery  查询结果集
	 * @param page       要查询的页码
	 * @param pageSize   分页大小
	 * @return Page      分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(org.hibernate.Query, org.hibernate.Query, int, int)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(Query countQuery, Query findQuery, int page, int pageSize) {
		int count = ((Number)countQuery.uniqueResult()).intValue();
		if(count>0){
			if(pageSize<=0){
				pageSize=count;
			}
			int totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
			if(page>totalPage){
				page=totalPage;
			}
			return new Page<T>(findQuery.setFirstResult((page - 1) * pageSize)
				.setMaxResults(pageSize).list(), page, count, totalPage);
		}else{
			return new Page<T>();
		}
	}
	/**
	 * 分页查询。Query对象必须由具名参数化hql/sql创建。
	 * @param countQuery 查询符合查询条件的结果集数量
	 * @param findQuery  查询结果集
	 * @param page       要查询的页码
	 * @param pageSize   分页大小
	 * @param param      查询参数
	 * @return Page      分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(org.hibernate.Query, org.hibernate.Query, int, int)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(Query countQuery, Query findQuery, int page, int pageSize, Map<String, Object> param){
		return findPage(countQuery.setProperties(param),findQuery.setProperties(param),page,pageSize);
	}
	/**
	 * 分页查询。Query对象必须由不具名参数化hql/sql创建。
	 * @param countQuery 查询符合查询条件的结果集数量
	 * @param findQuery  查询结果集
	 * @param page       要查询的页码
	 * @param pageSize   分页大小
	 * @param param      查询参数
	 * @return Page      分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(org.hibernate.Query, org.hibernate.Query, int, int)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(Query countQuery, Query findQuery, int page, int pageSize, Object... param){
		if(param!=null){
			for(int i=0,l=param.length;i<l;i++){
				Object para=param[i];
				countQuery.setParameter(i, para);
				findQuery.setParameter(i, para);
			}
		}
		return findPage(countQuery,findQuery,page,pageSize);
	}
	/**
	 * 分页查询。Query对象必须由具名参数化hql/sql创建。
	 * 如果param是Number Boolean Character String Date对象，Query对象就是不具名参数化的。
	 * 如果是复合对象，就是具名参数化的
	 * @param countQuery 查询符合查询条件的结果集数量
	 * @param findQuery  查询结果集
	 * @param page       要查询的页码
	 * @param pageSize   分页大小
	 * @param param      查询参数
	 * @return Page      分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(org.hibernate.Query, org.hibernate.Query, int, int)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(Query countQuery, Query findQuery, int page, int pageSize, Object param){
		if (param instanceof Number || param instanceof Boolean || param instanceof Character || param instanceof String || param instanceof Date) {
			return findPage(countQuery, findQuery, page, pageSize,new Object[] { param });
		} else {
			return findPage(countQuery.setProperties(param), findQuery.setProperties(param), page, pageSize);
		}
	}
	/**
	 * 分页查询。Query对象必须由具名参数化hql/sql创建。
	 * @param countQuery 查询符合查询条件的结果集数量
	 * @param findQuery  查询结果集
	 * @param page       要查询的页码
	 * @param pageSize   分页大小
	 * @param name       查询参数名称
	 * @param value      查询参数值
	 * @return Page      分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(org.hibernate.Query, org.hibernate.Query, int, int)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(Query countQuery, Query findQuery, int page, int pageSize, String name, Object value){
		setParameter(countQuery,findQuery,name,value);
		return findPage(countQuery,findQuery,page, pageSize);
	}
	/**
	 * 分页查询。Query对象必须由具名参数化hql/sql创建。
	 * @param  countQuery 查询符合查询条件的结果集数量
	 * @param  findQuery  查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  names      查询参数名称
	 * @param  values     查询参数值
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(org.hibernate.Query, org.hibernate.Query, int, int, java.lang.String[], java.lang.Object[])<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(Query countQuery, Query findQuery, int page, int pageSize, String[] names, Object[] values){
		for (int i = 0, l = names.length; i < l; i++) {
			setParameter(countQuery, findQuery, names[i], values[i]);
		}
		return findPage(countQuery,findQuery,page,pageSize);
	}
	
	/**
	 * 分页查询。必须是具名参数化hql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  param      查询参数
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(final String countHql, final String findHql,
			final int page, final int pageSize, final Map<String, Object> param) {
		return super.execute(new HibernateCallback<Page<T>>() {
			@Override
			public Page<T> doInHibernate(Session session) throws HibernateException, SQLException {
				return findPage(session.createQuery(countHql),session.createQuery(findHql),page,pageSize,param);
			}
		});
	}

	/**
	 * 分页查询。必须是具名参数化hql。
	 * 如果param是Boolean String Number Character Date对象，两个hql就是不具名参数化的。
	 * 如果是复合对象，就是具名参数化的
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  param      查询参数
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(final String countHql, final String findHql,
			final int page, final int pageSize, final Object param) {
		return super.execute(new HibernateCallback<Page<T>>() {
			@Override
			public Page<T> doInHibernate(Session session)
					throws HibernateException, SQLException {
				return findPage(session.createQuery(countHql), session.createQuery(findHql), page, pageSize,param);
			}
		});
	}

	/**
	 * 分页查询。必须是参数化hql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  param      查询参数
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(final String countHql, final String findHql,
			final int page, final int pageSize, final Object... param) {
		return super.execute(new HibernateCallback<Page<T>>() {
			@Override
			public Page<T> doInHibernate(Session session)throws HibernateException, SQLException {
				return findPage(session.createQuery(countHql), session.createQuery(findHql), page, pageSize,param);
			}
		});
	}

	/**
	 * 分页查询。必须是具名参数化hql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  names      查询参数名
	 * @param  values     查询参数值
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(final String countHql, final String findHql,
			final int page, final int pageSize, final String[] names, final String[] values) {
		return super.execute(new HibernateCallback<Page<T>>() {
			@Override
			public Page<T> doInHibernate(Session session)throws HibernateException, SQLException {
				return findPage(session.createQuery(countHql), session.createQuery(findHql), page, pageSize,names,values);
			}
		});
	}

	/**
	 * 分页查询。必须是具名参数化hql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  names      查询参数名
	 * @param  values     查询参数值
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public <T> Page<T> findPage(final String countHql, final String findHql,
			final int page, final int pageSize, final String name, final Object value) {
		return super.execute(new HibernateCallback<Page<T>>() {
			@Override
			public Page<T> doInHibernate(Session session)throws HibernateException, SQLException {
				return findPage(session.createQuery(countHql), session.createQuery(findHql), page, pageSize,name,value);
			}
		});
	}

	/**
	 * 分页查询。必须是参数化sql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  param      查询参数
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public Page<Object[]> findPageBySql(final String countSql, final String findSql,
			final int page, final int pageSize, final Object... param){
		return super.execute(new HibernateCallback<Page<Object[]>>(){
			@Override
			public Page<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return findPage(session.createSQLQuery(countSql), session.createSQLQuery(findSql), page, pageSize,param);
			}
		});
	}
	/**
	 * 分页查询。必须是具名参数化sql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  name      查询参数名
	 * @param  value     查询参数值
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public Page<Object[]> findPageBySql(final String countSql, final String findSql,
			final int page, final int pageSize, final String name, final Object value){
		return super.execute(new HibernateCallback<Page<Object[]>>(){
			@Override
			public Page<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return findPage(session.createSQLQuery(countSql), session.createSQLQuery(findSql), page, pageSize,name,value);
			}
		});
	}
	/**
	 * 分页查询。必须是具名参数化sql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  names      查询参数名
	 * @param  values     查询参数值
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public Page<Object[]> findPageBySql(final String countSql, final String findSql,
			final int page, final int pageSize, final String[] name, final Object[] value){
		return super.execute(new HibernateCallback<Page<Object[]>>(){
			@Override
			public Page<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return findPage(session.createSQLQuery(countSql), session.createSQLQuery(findSql), page, pageSize,name,value);
			}
		});
	}
	/**
	 * 分页查询。必须是具名参数化sql。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  param      查询参数
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public Page<Object[]> findPageBySql(final String countSql, final String findSql,
			final int page, final int pageSize, final Map<String,Object> param){
		return super.execute(new HibernateCallback<Page<Object[]>>(){
			@Override
			public Page<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return findPage(session.createSQLQuery(countSql), session.createSQLQuery(findSql), page, pageSize,param);
			}
		});
	}
	/**
	 * 分页查询。必须是具名参数化sql。
	 * 如果param是Boolean Number Character String Date对象，两个sql就是不具名参数化的；
	 * 如果是复合对象，就是具名参数化的。
	 * @param  countHql   查询符合查询条件的结果集数量
	 * @param  findHql    查询结果集
	 * @param  page       要查询的页码
	 * @param  pageSize   分页大小
	 * @param  param      查询参数
	 * @return Page       分页结果集
	 * @see me.jor.hibernate.InterfaceHibernateDao#findPage(java.lang.String, java.lang.String, int, int, java.util.Map)<br/>
	 *      me.jor.hibernate.Page
	 */
	@Override
	public Page<Object[]> findPageBySql(final String countSql, final String findSql,
			final int page, final int pageSize, final Object param){
		return super.execute(new HibernateCallback<Page<Object[]>>(){
			@Override
			public Page<Object[]> doInHibernate(Session session) throws HibernateException, SQLException {
				return findPage(session.createSQLQuery(countSql), session.createSQLQuery(findSql), page, pageSize,param);
			}
		});
	}
	

	/**
	 * 
	 * @param hql    待执行的具名参数化hql
	 * @param params 查询参数
	 * @return List  查询结果集
	 * @see me.jor.ssh.InterfaceHibernateDao#find(java.lang.String, java.util.Map)
	 */
	@Override
	public <T> List<T> find(final String hql, final Map<String, Object> params) {
		return super.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(hql).setProperties(params).list();
			}
		});
	}

	/**
	 * 
	 * @param hql    待执行的具名参数化hql
	 * 如果param是Boolean Number Character String Date对象，两个sql就是不具名参数化的；
	 * 如果是复合对象，就是具名参数化的。
	 * @param params 查询参数
	 * @return List  查询结果集
	 * @see me.jor.ssh.InterfaceHibernateDao#find(java.lang.String, java.util.Map)
	 */
	@Override
	public <T> List<T> findByParameters(final String hql,final Object params) {
		return super.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session)throws HibernateException, SQLException {
				return setParameter(session.createQuery(hql),params).list();
			}
		});
	}
	/**
	 * 返回符合条件的全集
	 * @param hql    待执行的具名参数化hql
	 * @param name   查询参数名
	 * @param params 查询参数值
	 * @return List  查询结果集
	 * @see me.jor.ssh.InterfaceHibernateDao#find(java.lang.String, java.util.Map)
	 */
	@Override
	public <T> List<T> findByParameters(final String hql,final String name,final Object params) {
		return super.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session)throws HibernateException, SQLException {
				return setParameter(session.createQuery(hql),name,params).list();
			}
		});
	}
	/**
	 * 返回符合条件的全集
	 * @param hql    待执行的具名参数化hql
	 * @param name   查询参数名
	 * @param params 查询参数值
	 * @return List  查询结果集
	 * @see me.jor.ssh.InterfaceHibernateDao#find(java.lang.String, java.util.Map)
	 */
	@Override
	public <T> List<T> findByParameters(final String hql,final String[] name,final Object[] params) {
		return super.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session)throws HibernateException, SQLException {
				return setParameter(session.createQuery(hql),name,params).list();
			}
		});
	}
	/**
	 * 返回符合条件的全集
	 * @param hql    待执行的具名参数化hql
	 * @param params   查询参数
	 * @return List  查询结果集
	 * @see me.jor.ssh.InterfaceHibernateDao#find(java.lang.String, java.util.Map)
	 */
	@Override
	public <T> List<T> findByParameters(final String hql,final Object... params) {
		return super.execute(new HibernateCallback<List<T>>() {
			@Override
			public List<T> doInHibernate(Session session)throws HibernateException, SQLException {
				return setParameter(session.createQuery(hql),params).list();
			}
		});
	}
	
	/**
	 * 根据id查询单条记录
	 * @param entityName 待查询的领域类名
	 * @param idname     作为主键的领域类属性名。如果传入null或空字符串，就采用默认值"id"
	 * @param id         待查询的主键值
	 * @return           查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#findById(java.lang.String, java.lang.String, java.io.Serializable)
	 */
	@Override
	public <T> T findById(String entityName, String idname, Serializable id){
		if(Help.isEmpty(idname)){
			idname="id";
		}
		return (T)uniqResult(new StringBuilder("from ").append(entityName).append(" where ").append(idname).append("=?").toString(),id);
	}
	/**
	 * 根据id查询单条记录
	 * @param entityClass 待查询的领域类对象
	 * @param idname      作为主键的领域类属性名。如果传入null或空字符串，就采用默认值"id"
	 * @param id          待查询的主键值
	 * @return            查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#findById(java.lang.String, java.lang.String, java.io.Serializable)
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public <T> T findById(Class entityClass, String idname, Serializable id){
		return (T)findById(entityClass.getSimpleName(),idname, id);
	}
	
	/**
	 * 返回惟一结果，如果结果集不只一条记录就会抛出异常
	 * @param hql 待执行的hql
	 * @return    查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#uniqResult(java.lang.String)
	 */
	@Override
	public <T> T uniqResult(final String hql) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException, SQLException {
				return (T) session.createQuery(hql).uniqueResult();
			}
		});
	}

	/**
	 * 返回惟一结果，如果结果集不只一条记录就会抛出异常
	 * @param hql 
	 *        待执行的hql。
	 *        如果params是Boolean Number String Date Character对象，就是不具名参数的hql，
	 *        如果是复合对象就是具名参数
	 * @param params 查询参数
	 * @return    查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#uniqResult(java.lang.String)
	 */
	@Override
	public <T> T uniqResult(final String hql, final Object params) {
		return super.execute(new HibernateCallback<T>(){
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)setParameter(session.createQuery(hql),params).uniqueResult();
			}
		});
	}

	/**
	 * 返回惟一结果，如果结果集不只一条记录就会抛出异常
	 * @param hql    待执行的不具名参数化hql
	 * @param params 查询参数
	 * @return       查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#uniqResult(java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T uniqResult(final String hql, final Object... params) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException,
					SQLException {
				return (T)setParameter(session.createQuery(hql),params).uniqueResult();
			}
		});
	}

	/**
	 * 返回惟一结果，如果结果集不只一条记录就会抛出异常
	 * @param hql   待执行的具名参数化hql
	 * @param name  查询参数名
	 * @param value 查询参数值
	 * @return      查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#uniqResult(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T uniqResult(final String hql, final String name, final Object value) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)setParameter(session.createQuery(hql),name,value).uniqueResult();
			}
		});
	}

	/**
	 * 返回惟一结果，如果结果集不只一条记录就会抛出异常
	 * @param hql   待执行的具名参数化hql
	 * @param name  查询参数名
	 * @param value 查询参数值
	 * @return      查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#uniqResult(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T uniqResult(final String hql, final String[] name, final Object[] value) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)setParameter(session.createQuery(hql),name,value).uniqueResult();
			}
		});
	}
	/**
	 * 返回惟一结果，如果结果集不只一条记录就会抛出异常
	 * @param hql   待执行的具名参数化hql
	 * @param params  查询参数
	 * @return      查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#uniqResult(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T uniqResult(final String hql, final Map<String, Object> params) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T) setParameter(session.createQuery(hql),params).uniqueResult();
			}
		});
	}
	
	/**
	 * 返回符合条件的第一条结果
	 * @param hql   待执行的hql
	 * @return      查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#uniqResult(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> T findFirstResult(final String hql){
		return super.execute(new HibernateCallback<T>(){
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)session.createQuery(hql).list().get(0);
			}
		});
	}
	private <T> T getFirstResult(Query query){
		List<T> list=query.list();
		if(Help.isNotEmpty(list)){
			return list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 返回符合条件的第一条结果
	 * @param hql    待执行的不具名参数化hql
	 * @param params 查询参数
	 * @return       查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#findFirstResult(java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T findFirstResult(final String hql, final Object... params){
		return super.execute(new HibernateCallback<T>(){
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)getFirstResult(setParameter(session.createQuery(hql),params));
			}
		});
	}
	/**
	 * 返回符合条件的第一条结果
	 * @param hql
	 *        待执行的hql。
	 *        如果params是Boolean Number String Date Character对象，就是不具名参数的hql，
	 *        如果是复合对象就是具名参数
	 * @param params 查询参数
	 * @return       查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#findFirstResult(java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T findFirstResult(final String hql, final Object params){
		return super.execute(new HibernateCallback<T>(){
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)getFirstResult(setParameter(session.createQuery(hql),params));
			}
			
		});
	}
	/**
	 * 返回符合条件的第一条结果
	 * @param hql
	 *        待执行的具名参数化hql。
	 * @param name  查询参数名
	 * @param value 查询参数值
	 * @return       查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#findFirstResult(java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T findFirstResult(final String hql, final String name, final Object value) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)getFirstResult(setParameter(session.createQuery(hql),name,value));
			}
		});
	}
	/**
	 * 返回符合条件的第一条结果
	 * @param hql
	 *        待执行的具名参数化hql。
	 * @param name  查询参数名
	 * @param value 查询参数值
	 * @return       查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#findFirstResult(java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T findFirstResult(final String hql, final String[] name, final Object[] value) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)getFirstResult(setParameter(session.createQuery(hql),name,value));
			}
		});
	}
	/**
	 * 返回符合条件的第一条结果
	 * @param hql
	 *        待执行的具名参数化hql。
	 * @param params  查询参数名
	 * @return       查询结果领域对象
	 * @see me.jor.ssh.InterfaceHibernateDao#findFirstResult(java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T findFirstResult(final String hql, final Map<String, Object> params) {
		return super.execute(new HibernateCallback<T>() {
			@Override
			public T doInHibernate(Session session) throws HibernateException,SQLException {
				return (T)getFirstResult(setParameter(session.createQuery(hql),params));
			}
		});
	}
	@SuppressWarnings("rawtypes")
	private Serializable getId(Object entity, String idname) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Class cls = entity.getClass();
		if (Help.isEmpty(idname)) {
			idname = "id";
		}
		String idgetter = "get" + idname.substring(0, 1).toUpperCase()
				+ idname.substring(1);
		return (Serializable) cls.getMethod(idgetter).invoke(
				entity);
	}
	/**
	 * 创建或更新tList保存的数据集。不存在则创建，存在则更新。
	 * @param tList   待创建或更新的数据集
	 * @param idname  主键名，如果传入null或空字符串，采用默认值"utf8"
	 * @param tag     此枚举决定保存行为
	 * @return        tList对象
	 * @throws DataAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @see me.jor.ssh.InterfaceHibernateDao#saveOrUpdate(java.util.List, java.lang.String, me.jor.ssh.UpdateFieldTag)
	 *      me.jor.hibernate.UpdateFieldTag
	 */
	@Override
	public <T> List<T> saveOrUpdate(List<T> tList, String idname, UpdateFieldTag tag) throws DataAccessException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		for(int i=0,l=tList.size();i<l;i++){
			saveOrUpdate(tList.get(i),idname,tag);
		}
		return tList;
	}
	/**
	 * 创建或更新entity保存的数据集。不存在则创建，存在则更新。
	 * @param entity   待创建或更新的数据集
	 * @param idname  主键名，如果传入null或空字符串，采用默认值"utf8"
	 * @param tag     此枚举决定保存行为
	 * @return        tList对象
	 * @throws DataAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @see me.jor.ssh.InterfaceHibernateDao#saveOrUpdate(java.util.List, java.lang.String, me.jor.ssh.UpdateFieldTag)
	 *      me.jor.hibernate.UpdateFieldTag
	 */
	@Override
	public <T> T saveOrUpdate(T entity, String idname, UpdateFieldTag tag) throws DataAccessException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Object id=getId(entity,idname);
		if(Help.isEmpty(id)){
			super.save(entity);
			return entity;
		}else{
			T t=(T)this.uniqResult(new StringBuilder("from ").append(entity.getClass().getSimpleName())
					.append(" where ").append(idname).append("=?").toString(),id);
			if(t==null){
				super.save(entity);
				return entity;
			}else{
				return updateById(entity, idname, tag);
			}
		}
	}
	/**
	 * 创建或保存values表示的记录
	 * @param entityClass      待操作的实体类对象
	 * @param values           待操作的数据
	 * @param idname           主键名，如果传入null或空字符串，采用默认值"utf8"
	 * @param tag              此枚举决定保存行为
	 * @return                 由entityClass和values创建的领域对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @see me.jor.ssh.InterfaceHibernateDao#saveOrUpdate(java.lang.Class, java.util.HashMap, java.lang.String, me.jor.ssh.UpdateFieldTag)
	 *      me.jor.hibernate.UpdateFieldTag
	 */
	@Override
	public <T> T saveOrUpdate(Class<T> entityClass, HashMap<String, Object> values, String idname, UpdateFieldTag tag) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
		T entity=entityClass.newInstance();
		for(Map.Entry<String, Object> entry:values.entrySet()){
			String fn=entry.getKey();
			Object val=entry.getValue();
			fn=fn.substring(0, 1).toUpperCase()+ fn.substring(1);
			entityClass.getMethod("set" + fn, entityClass.getMethod("get"+fn).getReturnType()).invoke(entity, val);
		}
		return saveOrUpdate(entity, idname, tag);
	}

	/**
	 * 根据主键更新
	 * @param entity   待更新的领域对象
	 * @param idname   主键名，如果传入null或空字符串，采用默认值"utf8"
	 * @param tag      此枚举决定保存行为
	 * @return         entity
	 * @see me.jor.ssh.InterfaceHibernateDao#updateById(java.lang.Object, java.lang.String, me.jor.ssh.UpdateFieldTag)
	 *      me.jor.hibernate.UpdateFieldTag
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public <T> T updateById(T entity, String idname, UpdateFieldTag tag) {
		try {
			Class cls = entity.getClass();
			if (Help.isEmpty(idname)) {
				idname = "id";
			}
			String idgetter = "get" + idname.substring(0, 1).toUpperCase()
					+ idname.substring(1);
			Serializable id = (Serializable) cls.getMethod(idgetter).invoke(
					entity);
			Object persisted = super.get(cls.getName(), id);
			if(persisted!=null){
				Method[] ms = cls.getMethods();
				boolean updated = false;
				for (int i = 0; i < ms.length; i++) {
					Method m = ms[i];
					String mn = m.getName();
					if (mn.startsWith("get") && !mn.equals("getClass")
							&& !mn.equals(idgetter) && !mn.endsWith("Id")) {
						Object v = m.invoke(entity);
						if (((v!=null && !"".equals(v) && tag.equals(UpdateFieldTag.UPDATE_NOT_EMPTY))) || 
							((v!=null && tag.equals(UpdateFieldTag.UPDATE_NOT_NULL))) || 
							  tag.equals(UpdateFieldTag.UPDATE_ALL)) {
							cls.getMethod("set" + mn.substring(3),
									m.getReturnType()).invoke(persisted, v);
							updated = true;
						}
					}
				}
				if (updated) {
					super.update(persisted);
				}
			}
			return (T)persisted;
		} catch (Exception e) {
			throw new CUDException(e);
		}
	}

	/**
	 * 更新主键是ids的那些记录为entity内保存的值
	 * @param entity  待保存的数据
	 * @param ids     待操作的记录主键，如果传入空数组会抛出EmptyIDArrayException
	 * @param idname  主键名，如果传入null或空字符串，采用默认值"utf8"
	 * @param tag     此枚举决定保存行为
	 * @return        实际更新的记录数
	 * @throws EmptyIDArrayException
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.Object, java.io.Serializable[], java.lang.String, me.jor.ssh.UpdateFieldTag)
	 *      me.jor.hibernate.UpdateFieldTag
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int updateByIds(Object entity, Serializable[] ids, String idname, UpdateFieldTag tag) {
		try {
			Class cls = entity.getClass();
			Method[] ms = cls.getMethods();
			HashMap<String,Object> params = new HashMap<String,Object>();
			if(Help.isEmpty(idname)){
				idname="id";
			}
			for (int i = 0; i < ms.length; i++) {
				Method m = ms[i];
				String mn = m.getName();
				if (mn.startsWith("get") && !mn.equals("getClass")
						&& !mn.substring(3).toLowerCase().equals(idname)) {
					Object v = m.invoke(entity);
					if (v != null || tag.equals(UpdateFieldTag.UPDATE_ALL)) {
						String fn=Character.toLowerCase(mn.charAt(3))+mn.substring(4);
						params.put(fn, v);
					}
				}
			}
			return updateByIds(entity.getClass().getSimpleName(),ids,idname,params);
		} catch (Exception e) {
			throw new CUDException(e);
		}
	}
	/**
	 * 更新与entityClass存在映射关系的表
	 * @param entityClass 待更新的领域类对象
	 * @param ids         待更新的记录主键，如果传入空数组会抛出EmptyIDArrayException
	 * @param idname      主键名，如果传入null或空字符串，默认采用"id"
	 * @param newValues   待更新的值
	 * @return            实际更新的记录数
	 * @throws EmptyIDArrayExcdeption
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.Class, java.io.Serializable[], java.lang.String, java.util.HashMap)
	 *      me.jor.hibernate.UpdateFieldTag
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int updateByIds(Class entityClass, Serializable[] ids, String idname, HashMap<String,Object> newValues){
		return updateByIds(entityClass.getSimpleName(),ids,idname,newValues);
	}
	/**
	 * 更新与entityName存在映射关系的表
	 * @param entityName 待更新的领域类名
	 * @param ids        待更新的记录主键，如果传入空数组会抛出EmptyIDArrayException
	 * @param idname     主键名，如果传入null或空字符串，默认采用"id"
	 * @param newValues  待更新的值
	 * @return           实际更新的记录数
	 * @throws EmptyIDArrayException
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.String, java.io.Serializable[], java.lang.String, java.util.HashMap)
	 */
	@Override
	public int updateByIds(final String entityName, final Serializable[] ids,
			final String idname, final HashMap<String, Object> newValues) {
		if (Help.isNotEmpty(entityName) && Help.isNotEmpty(ids)
				&& Help.isNotEmpty(newValues)) {
			return super.execute(new HibernateCallback<Integer>() {
				@Override
				public Integer doInHibernate(Session session)
						throws HibernateException, SQLException {
					StringBuilder strb = new StringBuilder("update ").append(
							entityName).append(" set ");
					Set<String> ks = newValues.keySet();
					for (String k : ks) {
						strb.append(k).append("=:").append(k).append(',');
					}
					strb.delete(strb.lastIndexOf(","), strb.length())
						.append(" where ")
						.append(Help.isEmpty(idname) ? "id" : idname);
					Query query=null;
					if(ids.length>1){
						query = session.createQuery(strb.append(" in (:IDS)").toString()).setParameterList("IDS", ids);
					}else if(ids.length==1){
						query = session.createQuery(strb.append("=:ID").toString()).setParameter("ID", ids[0]);
					}else{
						throw new EmptyIDArrayException();
					}
					for (String k : ks) {
						query.setParameter(k, newValues.get(k));
					}
					return query.executeUpdate();
				}
			});
		} else {
			return 0;
		}
	}
	/**
	 * 更新与entityName存在映射关系的表
	 * @param entityName 待更新的领域类名
	 * @param ids        待更新的记录主键
	 * @param idname     主键名，如果传入null或空字符串，默认采用"id"
	 * @param newValues  待更新的值
	 * @return           实际更新的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.String, java.io.Serializable[], java.lang.String, java.util.HashMap)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int updateById(Class entityClass, Serializable id, String idname, HashMap<String,Object> newValues){
		return updateById(entityClass.getSimpleName(),id,idname,newValues);
	}
	/**
	 * 更新与entityName存在映射关系的表
	 * @param entityName 待更新的领域类名
	 * @param ids        待更新的记录主键
	 * @param idname     主键名，如果传入null或空字符串，默认采用"id"
	 * @param newValues  待更新的值
	 * @return           实际更新的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.String, java.io.Serializable[], java.lang.String, java.util.HashMap)
	 */
	@Override
	public int updateById(final String entityName, final Serializable id,
			final String idname, final HashMap<String, Object> newValues) {
		return updateByIds(entityName, new Serializable[] { id }, idname,
				newValues);
	}
	/**
	 * 更新与entityClass存在映射关系的表
	 * @param entityClass   待更新的领域类对象
	 * @param ids           待更新的记录主键
	 * @param idname        主键名，如果传入null或空字符串，默认采用"id"
	 * @param fieldName     待更新的属性字段
	 * @param value         待更新的属性值
	 * @return              实际更新的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.Class, java.io.Serializable[], java.lang.String, java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int updateByIds(Class entityClass, Serializable[] ids, String idname, String fieldName, Object value){
		return updateByIds(entityClass.getSimpleName(),ids,idname,fieldName,value);
	}
	/**
	 * 更新与entityName存在映射关系的表
	 * @param entityName   待更新的领域类对象
	 * @param ids           待更新的记录主键，如果传入空数组会抛出EmptyIDArrayException
	 * @param idname        主键名，如果传入null或空字符串，默认采用"id"
	 * @param fieldName     待更新的属性字段
	 * @param value         待更新的属性值
	 * @return              实际更新的记录数
	 * @throws EmptyIDArrayException
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.Class, java.io.Serializable[], java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public int updateByIds(final String entityName, final Serializable[] ids,
			final String idname, final String filedName, final Object value) {
		return super.execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuilder hql=new StringBuilder("update ")
				.append(entityName).append(" set ").append(filedName).append("=:FIELDNAME")
				.append(" where ").append(Help.isEmpty(idname) ? "id" : idname);
				Query query=null;
				if(ids.length>1){
					query=session.createQuery(hql.append(" in (:IDS)").toString()).setParameterList("IDS", ids);
				}else if(ids.length==1){
					query=session.createQuery(hql.append("=:ID").toString()).setParameter("ID", ids[0]);
				}else{
					throw new EmptyIDArrayException();
				}
				return query.setParameter("FIELDNAME", value).executeUpdate();
			}
		});
	}
	/**
	 * 更新与entityClass存在映射关系的表
	 * @param entityClass   待更新的领域类对象
	 * @param id            待更新的记录主键
	 * @param idname        主键名，如果传入null或空字符串，默认采用"id"
	 * @param fieldName     待更新的属性字段
	 * @param value         待更新的属性值
	 * @return              实际更新的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.Class, java.io.Serializable[], java.lang.String, java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int updateById(Class entityClass, Serializable id, String idname, String fieldName, Object value){
		return updateById(entityClass.getSimpleName(),id, idname, fieldName, value);
	}
	/**
	 * 更新与entityName存在映射关系的表
	 * @param entityName    待更新的领域类名
	 * @param id            待更新的记录主键
	 * @param idname        主键名，如果传入null或空字符串，默认采用"id"
	 * @param fieldName     待更新的属性字段
	 * @param value         待更新的属性值
	 * @return              实际更新的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#updateByIds(java.lang.Class, java.io.Serializable[], java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public int updateById(final String entityName, final Serializable id,
			final String idname, final String filedName, final Object value) {
		return updateByIds(entityName, new Serializable[] { id }, idname,
				filedName, value);
	}
	/**
	 * 删除与entityClass存在映射关系的表
	 * @param entityClass 待更新的领域类对象
	 * @param ids         待删除的主键值，默认主键名"id"
	 * @return            实际删除的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#deleteByIds(java.lang.Class, java.io.Serializable[])
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int deleteByIds(Class entityClass, Serializable... ids){
		return deleteByIds(entityClass, null, ids);
	}
	/**
	 * 删除与entityClass存在映射关系的表
	 * @param entityClass 待更新的领域类对象
	 * @param idname      主键名，如果传入null或空字符串，默认采用"id"
	 * @param ids         待删除的主键值
	 * @return            实际删除的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#deleteByIds(java.lang.Class, java.lang.String, java.io.Serializable[])
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int deleteByIds(Class entityClass, String idname, Serializable... ids){
		return deleteByIds(entityClass.getSimpleName(), idname, ids);
	}
	/**
	 * 删除与entityClass 存在映射关系的表
	 * @param entityName 待更新的领域类对象
	 * @param ids        待删除的主键值，默认主键名"id"
	 * @return           实际删除的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#deleteByIds(java.lang.String, java.io.Serializable[])
	 */
	@Override
	public int deleteByIds(final String entityName, final Serializable... ids){
		return deleteByIds(entityName, null, ids);
	}
	/**
	 * 删除与entityClass存在映射关系的表
	 * @param entityName 待更新的领域类名
	 * @param idname     主键名，如果传入null或空字符串，默认采用"id"
	 * @param ids        待删除的主键值，默认主键名"id"， 如果传入空数组或不传此参数，会抛出EmptyIDArrayException
	 * @return           实际删除的记录数
	 * @throws EmptyIDArrayException
	 * @see me.jor.ssh.InterfaceHibernateDao#deleteByIds(java.lang.String, java.lang.String, java.io.Serializable[])
	 */
	@Override
	public int deleteByIds(final String entityName, final String idname, final Serializable... ids) {
		return super.execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuilder hql=new StringBuilder("delete from ")
				.append(entityName)
				.append(" where ")
				.append(Help.isEmpty(idname) ? "id" : idname);
				Query query=null;
				if(ids.length>1){
					query=session.createQuery(hql.append(" in (:IDS)").toString()).setParameterList("IDS", ids);
				}else if(ids.length==1){
					query=session.createQuery(hql.append("=:ID").toString()).setParameter("ID", ids[0]);
				}else{
					throw new EmptyIDArrayException();
				}
				return query.executeUpdate();
			}
		});
	}
	/**
	 * 删除与entityClass存在映射关系的表
	 * @param entityName 待更新的领域类名
	 * @param idname     主键名，如果传入null或空字符串，默认采用"id"
	 * @param param      过滤待删除记录的属性名
	 * @param value      过滤待删除记录的参数值，与param配合使用
	 * @param ids        这些主键值的记录不被删除，如果传入了空数组，或不传此参数会抛出EmptyIDArrayException
	 * @return           被删除的记录数
	 * @throws EmptyIDArrayException
	 * @see me.jor.ssh.InterfaceHibernateDao#deleteByNotIds(java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.io.Serializable[])
	 */
	public int deleteByNotIds(final String entityName, final String idname,final String param,final Long value, final Serializable... ids) {
		return super.execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuilder hql=new StringBuilder("delete from ")
				.append(entityName)
				.append(" where ").append(param).append("=:value").append( " and ")
				.append(Help.isEmpty(idname) ? "id": idname);
				Query query=null;
				if(ids.length>1){
					query=session.createQuery(hql.append(" not in (:IDS)").toString()).setParameterList("IDS", ids);
				}else if(ids.length==1){
					query=session.createQuery(hql.append(" <>:ID").toString()).setParameter("ID", ids[0]);
				}else{
					throw new EmptyIDArrayException();
				}
				return query.setParameter("value", value).executeUpdate();
			}
		});
	}
	
	
	/**
	 * 删除与entityClass存在映射关系的表
	 * @param entityClass 待更新的领域类对象
	 * @param idname      主键名，如果传入null或空字符串，默认采用"id"
	 * @param id          要删除的主键值
	 * @return            被删除的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#deleteById(java.lang.Class, java.lang.String, java.io.Serializable)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int deleteById(Class entityClass, String idname, Serializable id){
		return deleteById(entityClass.getSimpleName(), idname, id);
	}
	/**
	 * 删除与entityName存在映射关系的表
	 * @param entityName 待更新的领域类名
	 * @param idname     主键名，如果传入null或空字符串，默认采用"id"
	 * @param id         要删除的主键值
	 * @return           被删除的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#deleteById(java.lang.String, java.lang.String, java.io.Serializable)
	 */
	@Override
	public int deleteById(final String entityName, final String idname, final Serializable id) {
		return deleteByIds(entityName, idname, new Serializable[]{id});
	}
	/**
	 * 可执行delete或update hql。
	 * @param hql    待执行的具名参数化hql
	 * @param name   hql参数名
	 * @param value  hql参数值
	 * @return       被影响的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#bulkUpdate(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public int bulkUpdate(final String hql, final String name, final Object value){
		return super.execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException,SQLException {
				return setParameter(session.createQuery(hql),name,value).executeUpdate();
			}
		});
	}
	/**
	 * 可执行delete或update hql。
	 * @param hql    待执行的具名参数化hql
	 * @param name   hql参数名
	 * @param value  hql参数值
	 * @return       被影响的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#bulkUpdate(java.lang.String, java.lang.String[], java.lang.Object[])
	 */
	@Override
	public int bulkUpdate(final String hql, final String[] name, final Object[] value){
		return super.execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException, SQLException {
				return setParameter(session.createQuery(hql),name,value).executeUpdate();
			}
		});
	}
	/**
	 * 可执行delete或update hql。
	 * @param hql    待执行的具名参数化hql
	 * @param params hql参数名值对
	 * @return       被影响的记录数
	 * @see me.jor.ssh.InterfaceHibernateDao#bulkUpdate(java.lang.String, java.util.Map)
	 */
	@Override
	public int bulkUpdate(final String hql, final Map<String, Object> params) {
		return super.execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException,SQLException {
				return (Integer) setParameter(session.createQuery(hql),params).executeUpdate();
			}
		});
	}
	
		@SuppressWarnings("rawtypes")
	private void setParameter(Query countQuery, Query findQuery, String name, Object value) {
		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			countQuery.setParameterList(name, collection);
			findQuery.setParameterList(name, collection);
		} else if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			countQuery.setParameterList(name, array);
			findQuery.setParameterList(name, array);
		} else {
			countQuery.setParameter(name, value);
			findQuery.setParameter(name, value);
		}
	}
	private Query setParameter(Query query,Map<String,Object> params){
		return query.setProperties(params);
	}
	@SuppressWarnings("rawtypes")
	private Query setParameter(Query query, final String[] name, final Object[] value){
		for (int i = 0, l = name.length; i < l; i++) {
			Object v = value[i];
			if (v instanceof Collection) {
				query.setParameterList(name[i], (Collection) v);
			} else if (v.getClass().isArray()) {
				query.setParameterList(name[i], (Object[]) v);
			} else {
				query.setParameter(name[i], v);
			}
		}
		return query;
	}
	@SuppressWarnings("rawtypes")
	private Query setParameter(Query query,final String name, final Object value){
		if (value instanceof Collection) {
			query.setParameterList(name, (Collection) value);
		}else if (value.getClass().isArray()) {
			query.setParameterList(name, (Object[]) value);
		} else {
			query.setParameter(name, value);
		}
		return query;
	}
	private Query setParameter(Query query, Object params){
		if (params instanceof Number || params instanceof Boolean
				|| params instanceof Character || params instanceof String || params instanceof Date) {
			query.setParameter(0, params);
		}else{
			query.setProperties(params);
		}
		return query;
	}
	private Query setParameter(Query query, Object... params) {
		if(params!=null){
			for(int i=0,l=params.length;i<l;i++){
				query.setParameter(i, params[i]);
			}
		}
		return query;
	}
}