package me.jor.roa.core.accessable;

/**
 * 用于标识一次访问的目的是要将响应数据作为文件保存到本地，还是要在页面上显示或运行。
 * 默认值是PAGE
 *
 */
public enum AccessPurpose {
	PAGE,SAVE;
}
