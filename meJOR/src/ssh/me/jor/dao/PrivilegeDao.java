package me.jor.dao;

public interface PrivilegeDao {
	public boolean hasGranted(int userid, String namespace, String name);
}
