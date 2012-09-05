package me.jor.service.impl;

import me.jor.dao.PrivilegeDao;
import me.jor.service.PrivilegeService;

public class PrivilegeServiceImpl implements PrivilegeService {

	private PrivilegeDao privilegeDao;
	public boolean hasGranted(int userid, String namespace, String name) {
		if("/".equals(namespace) && "login".equals(name)){
			return true;
		}
		return privilegeDao.hasGranted(userid, namespace, name);
	}
	public PrivilegeDao getPrivilegeDao() {
		return privilegeDao;
	}
	public void setPrivilegeDao(PrivilegeDao privilegeDao) {
		this.privilegeDao = privilegeDao;
	}
}
