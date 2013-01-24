package me.jor.dao.impl;


import me.jor.dao.PrivilegeDao;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class PrivilegeDaoImpl extends HibernateTemplate implements PrivilegeDao{

	@Override
	public boolean hasGranted(int userid, String namespace, String name) {
		System.out.println("#########################");
		return true;
	}
}
