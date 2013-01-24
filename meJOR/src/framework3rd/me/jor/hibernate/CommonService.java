package me.jor.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.springframework.dao.DataAccessException;

public class CommonService {
	private HibernateBaseDao baseDao;
	
	public HibernateBaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(HibernateBaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public Serializable save(Object o){
		return baseDao.save(o);
	}
	public <T> T update(T o, String idname, UpdateFieldTag tag){
		return baseDao.updateById(o, idname, tag);
	}
	public <T> T saveOrUpdate(T o, String idname, UpdateFieldTag tag) throws DataAccessException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		return baseDao.saveOrUpdate(o, idname, tag);
	}
	public void saveOrUpdate(Object o){
		baseDao.saveOrUpdate(o);
	}
	public void delete(Object o){
		baseDao.delete(o);
	}
}
