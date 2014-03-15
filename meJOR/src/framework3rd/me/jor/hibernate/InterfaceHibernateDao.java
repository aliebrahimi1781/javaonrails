package me.jor.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jor.hibernate.Page;

import org.hibernate.Query;
import org.springframework.dao.DataAccessException;

/**
 * 所有方法都被me.jor.hibernate.AbstractHibernateBaseDao及其父类实现
 *
 */
public interface InterfaceHibernateDao {
	public Serializable save(Object def);

	public void update(Object def);

	public abstract <T> List<T> find(String hql);

	public <T> List<T> findByParameters(final String hql,
			final Object... params);

	public <T> List<T> findByParameters(final String hql, final String[] name,
			final Object[] params);

	public <T> List<T> findByParameters(final String hql, final String name,
			final Object params);

	public abstract <T> Page<T> findPage(Query countQuery, Query findQuery,
			int page, int pageSize);

	public abstract <T> Page<T> findPage(Query countQuery, Query findQuery,
			int page, int pageSize, Map<String, Object> param);

	public abstract <T> Page<T> findPage(Query countQuery, Query findQuery,
			int page, int pageSize, Object... param);

	public abstract <T> Page<T> findPage(Query countQuery, Query findQuery,
			int page, int pageSize, Object param);

	public abstract <T> Page<T> findPage(Query countQuery, Query findQuery,
			int page, int pageSize, String name, Object value);

	public abstract <T> Page<T> findPage(Query countQuery, Query findQuery,
			int page, int pageSize, String[] names, Object[] values);

	public abstract <T> Page<T> findPage(final String countHql,
			final String findHql, final int page, final int pageSize,
			final Map<String, Object> param);

	public abstract <T> Page<T> findPage(final String countHql,
			final String findHql, final int page, final int pageSize,
			final Object param);

	public abstract <T> Page<T> findPage(final String countHql,
			final String findHql, final int page, final int pageSize,
			final Object... param);

	public abstract <T> Page<T> findPage(final String countHql,
			final String findHql, final int page, final int pageSize,
			final String[] names, final String[] values);

	public abstract <T> Page<T> findPage(final String countHql,
			final String findHql, final int page, final int pageSize,
			final String name, final Object value);

	public abstract Page<Object[]> findPageBySql(final String countSql,
			final String findSql, final int page, final int pageSize,
			final Object... param);

	public abstract Page<Object[]> findPageBySql(final String countSql,
			final String findSql, final int page, final int pageSize,
			final String name, final Object value);

	public abstract Page<Object[]> findPageBySql(final String countSql,
			final String findSql, final int page, final int pageSize,
			final String[] name, final Object[] value);

	public abstract Page<Object[]> findPageBySql(final String countSql,
			final String findSql, final int page, final int pageSize,
			final Map<String, Object> param);

	public abstract Page<Object[]> findPageBySql(final String countSql,
			final String findSql, final int page, final int pageSize,
			final Object param);

	public abstract <T> List<T> find(final String hql,
			final Map<String, Object> params);
	public abstract <T> List<T> findByParameters(final String hql,
			final Object params);

	public abstract <T> T findById(String entityName, String idname,
			Serializable id);

	@SuppressWarnings("rawtypes")
	public abstract <T> T findById(Class entityClass, String idname,
			Serializable id);

	public abstract <T> T uniqResult(final String hql);

	public abstract <T> T uniqResult(final String hql, final Object params);

	public abstract <T> T uniqResult(final String hql, final Object... params);

	public abstract <T> T uniqResult(final String hql, final String name,
			final Object value);

	public abstract <T> T uniqResult(final String hql, final String[] name,
			final Object[] value);

	public abstract <T> T uniqResult(final String hql,
			final Map<String, Object> params);

	public abstract <T> T findFirstResult(final String hql);

	public abstract <T> T findFirstResult(final String hql,
			final Object... params);

	public abstract <T> T findFirstResult(final String hql, final Object params);

	public abstract <T> T findFirstResult(final String hql, final String name,
			final Object value);

	public abstract <T> T findFirstResult(final String hql,
			final String[] name, final Object[] value);

	public abstract <T> T findFirstResult(final String hql,
			final Map<String, Object> params);

	public abstract <T> List<T> saveOrUpdate(List<T> tList, String idname,
			UpdateFieldTag tag) throws DataAccessException,
			IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException;

	public abstract <T> T saveOrUpdate(T entity, String idname,
			UpdateFieldTag tag) throws DataAccessException,
			IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException;

	public abstract <T> T saveOrUpdate(Class<T> entityClass,
			HashMap<String, Object> values, String idname, UpdateFieldTag tag)
			throws InstantiationException, IllegalAccessException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException;

	public abstract <T> T updateById(T entity, String idname, UpdateFieldTag tag, String... ignoredFields);

	public abstract int updateByIds(Object entity, Serializable[] ids, String idname, UpdateFieldTag tag, String... ignoredFields);

	@SuppressWarnings("rawtypes")
	public abstract int updateByIds(Class entityClass, Serializable[] ids,
			String idname, HashMap<String, Object> newValues);

	public abstract int updateByIds(final String entityName,
			final Serializable[] ids, final String idname,
			final HashMap<String, Object> newValues);

	@SuppressWarnings("rawtypes")
	public abstract int updateById(Class entityClass, Serializable id,
			String idname, HashMap<String, Object> newValues);

	public abstract int updateById(final String entityName,
			final Serializable id, final String idname,
			final HashMap<String, Object> newValues);

	@SuppressWarnings("rawtypes")
	public abstract int updateByIds(Class entityClass, Serializable[] ids,
			String idname, String fieldName, Object value);

	public abstract int updateByIds(final String entityName,
			final Serializable[] ids, final String idname,
			final String filedName, final Object value);

	@SuppressWarnings("rawtypes")
	public abstract int updateById(Class entityClass, Serializable id,
			String idname, String fieldName, Object value);

	public abstract int updateById(final String entityName,
			final Serializable id, final String idname, final String filedName,
			final Object value);

	@SuppressWarnings("rawtypes")
	public abstract int deleteByIds(Class entityClass,String idname, Serializable... ids);

	@SuppressWarnings("rawtypes")
	public int deleteByIds(Class entityClass, Serializable... ids);

	public abstract int deleteByIds(final String entityName, final String idname,final Serializable... ids);

	public abstract int deleteByNotIds(final String entityName,final String idname,final String param,final Long value,final Serializable... ids);
	
	public abstract int deleteByIds(final String entityName,final Serializable... ids);

	@SuppressWarnings("rawtypes")
	public abstract int deleteById(Class entityClass,String idname, Serializable id);

	public abstract int deleteById(final String entityName, final String idname,final Serializable id);

	public abstract int bulkUpdate(final String hql, final Map<String, Object> params);

	public abstract int bulkUpdate(final String hql, final String[] name,
			final Object[] value);

	public abstract int bulkUpdate(final String hql, final String name,
			final Object value);

	public abstract int bulkUpdate(final String hql);

	public abstract int bulkUpdate(final String hql, final Object value);

	public abstract int bulkUpdate(final String hql, final Object... values);

	public abstract void flush();

	public abstract <T> List<T> find(String hql, Object... param);
}