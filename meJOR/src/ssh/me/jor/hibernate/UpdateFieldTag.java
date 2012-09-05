package me.jor.hibernate;

/**
 * 与me.jor.hibernate.AbstractHibernateBaseDao一起使用。<br/>
 * 用于决定me.jor.hibernate.AbstractHibernateBaseDao更新操作的行为。<br/>
 * UPDATE_NOT_NULL:领域对象属性值是null的属性不被更新。<br/>
 * UPDATE_NOT_EMPTY:领域对象内属性值是null或空字符串的属性不被更新。<br/>
 * UPDATE_ALL:领域对象的所有属性，只要是出现在映射文件的，一律被更新。
 */
public enum UpdateFieldTag {
	UPDATE_NOT_NULL,UPDATE_NOT_EMPTY,UPDATE_ALL;
}
