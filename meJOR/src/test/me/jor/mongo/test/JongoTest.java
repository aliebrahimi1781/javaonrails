package me.jor.mongo.test;

import java.net.UnknownHostException;

import me.jor.common.GlobalObject;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DB;
import com.mongodb.Mongo;


public class JongoTest {
	public static void main(String[] args) throws UnknownHostException, JsonProcessingException {
		DB db=new Mongo("localhost",27017).getDB("wifiin");
		db.authenticate("wifiin", new char[]{'w','i','f','i','i','n'});
		Jongo jongo=new Jongo(db);
		MongoCollection accounts=jongo.getCollection("accounts");
		accounts.remove(new ObjectId("5145249df6453ac47158eedb"));
		Account account=new Account();
		account.setAccountType(1);
		account.setAvailable(1);
		account.setCode("test0000");
		account.setName("18611518126");
		account.setPassWord("axdfaq13");
		account.setPermitted(new Account.AccountLocation[]{new Account.AccountLocation("北京","北京")});
		account.setForbidden(new Account.AccountLocation[]{new Account.AccountLocation("广东")});
		accounts.save(account);
		account=accounts.findOne().as(Account.class);
//		System.out.println(GlobalObject.getJsonMapper().writeValueAsString(account));
	}
}
