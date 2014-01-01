package me.jor.mongo.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AbstractBdApAccounterCmcc entity provides the base persistence definition of
 * the BdApAccounterCmcc entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class Account implements java.io.Serializable {
	public static class AccountLocation{
		private String province;
		private String city;
		public AccountLocation() {}
		public AccountLocation(String province){
			this.province=province;
		}
		public AccountLocation(String province, String city) {
			this.province = province;
			this.city = city;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		
	}
	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3775629363359415975L;
	@JsonProperty("_id")
	private ObjectId id;
	private int wifiApId;
	private String code;
	private String name;
	private String passWord;
	private Integer accountType;
	private Integer available;
	private Integer multiCount;
	private Integer curCount;
	private AtomicInteger wlanMinutes;
	private String priority;
	private String validPeriod;
	private String packageType;
	private AtomicInteger consumedMinutes;
	private Integer delFlg;
	private AtomicInteger distributedCount;
	private int failed;
	private int accountCategory;
	private AccountLocation[] permitted;
	private AccountLocation[] forbidden;
	
	@JsonIgnore
	private int hash;
	@JsonIgnore
	private boolean hashed;

	// Constructors

	/** default constructor */
	public Account() {
		wlanMinutes=new AtomicInteger(0);
		consumedMinutes=new AtomicInteger(0);
		distributedCount=new AtomicInteger(0);
	}

	/** full constructor */
	public Account(int wifiApId, String code,
			String name, String passWord,Integer accountType,
			Integer available,Integer multiCount,Integer curCount, Integer wlanMinutes, Integer delFlg) {
		this.wifiApId = wifiApId;
		this.code = code;
		this.name = name;
		this.passWord = passWord;
		this.accountType = accountType;
		this.available = available;
		this.multiCount = multiCount;
		this.curCount = curCount;
		this.wlanMinutes = new AtomicInteger(wlanMinutes);
		this.delFlg = delFlg;
		
		consumedMinutes=new AtomicInteger(0);
		distributedCount=new AtomicInteger(0);
	}
	
	public int increaseDistributedCount(){
		return distributedCount.incrementAndGet();
	}
	public int increaseConsumedMinutes(){
		return consumedMinutes.incrementAndGet();
	}
	public void increaseFailed(){
		this.failed++;
	}
	public int decreaseWlanMinutes(){
		return wlanMinutes.decrementAndGet();
	}
	
	@Override
	public int hashCode(){
		if(hashed){
			return hash;
		}else{
			hashed=true;
			return (hash=31*(31*this.name.hashCode()+this.wifiApId));
		}
	}
	@Override
	public boolean equals(Object o){
		if(o!=null && o instanceof Account){
			Account account=(Account)o;
			return account.wifiApId==wifiApId && account.name.equals(name);
		}else{
			return false;
		}
	}

	// Property accessors

	public int getWlanMinutes() {
		return wlanMinutes.get();
	}

	public void setWlanMinutes(int wlanMinutes) {
		this.wlanMinutes.set(wlanMinutes);
	}

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public int getWifiApId() {
		return wifiApId;
	}

	public void setWifiApId(int wifiApId) {
		this.wifiApId = wifiApId;
	}

	public Integer getCurCount() {
		return curCount;
	}

	public void setCurCount(Integer curCount) {
		this.curCount = curCount;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassWord() {
		return this.passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}


	public Integer getDelFlg() {
		return this.delFlg;
	}

	public void setDelFlg(Integer delFlg) {
		this.delFlg = delFlg;
	}
	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public Integer getMultiCount() {
		return multiCount;
	}

	public void setMultiCount(Integer multiCount) {
		this.multiCount = multiCount;
	}

	public Integer getAvailable() {
		return available;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getValidPeriod() {
		return validPeriod;
	}

	public void setValidPeriod(String validPeriod) {
		this.validPeriod = validPeriod;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public int getDistributedCount() {
		return distributedCount.get();
	}

	public void setDistributedCount(int distributedCount) {
		this.distributedCount.set(distributedCount);
	}

	public int getConsumedMinutes() {
		return consumedMinutes.get();
	}

	public void setConsumedMinutes(int consumedMinutes) {
		this.consumedMinutes.set(consumedMinutes);
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getAccountCategory() {
		return accountCategory;
	}

	public void setAccountCategory(int accountCategory) {
		this.accountCategory = accountCategory;
	}

	public AccountLocation[] getPermitted() {
		return permitted;
	}

	public void setPermitted(AccountLocation[] permitted) {
		this.permitted = permitted;
	}

	public AccountLocation[] getForbidden() {
		return forbidden;
	}

	public void setForbidden(AccountLocation[] forbidden) {
		this.forbidden = forbidden;
	}
	
}