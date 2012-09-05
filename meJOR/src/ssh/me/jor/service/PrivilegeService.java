package me.jor.service;

public interface PrivilegeService {
	public boolean hasGranted(int userid, String namespace, String name);
}
