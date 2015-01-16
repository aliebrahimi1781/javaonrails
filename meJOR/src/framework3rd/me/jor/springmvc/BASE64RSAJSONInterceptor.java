package me.jor.springmvc;

import java.util.Map;

import me.jor.common.CommonConstant;
import me.jor.common.GlobalObject;
import me.jor.util.Base64;
import me.jor.util.Help;
import me.jor.util.RSA;
import me.jor.util.RSAPrivateKeyGetter;

public class BASE64RSAJSONInterceptor extends AbstractBaseInterceptor{
	private RSAPrivateKeyGetter rsaPrivateKeyGetter;
	public RSAPrivateKeyGetter getRsaPrivateKeyGetter() {
		return rsaPrivateKeyGetter;
	}
	public void setRsaPrivateKeyGetter(RSAPrivateKeyGetter rsaPrivateKeyGetter) {
		this.rsaPrivateKeyGetter = rsaPrivateKeyGetter;
	}
	@Override
	protected Object beforeTransformParams(String params) throws Exception {
		return GlobalObject.getJsonMapper().readValue(
				RSA.decryptByPrivateKey(
					Base64.decode(params, getCharset()), 
					rsaPrivateKeyGetter.get()), Map.class);
	}

	@Override
	protected String generateLogText(Map logContent) throws Exception {
		return GlobalObject.getJsonMapper().writeValueAsString(logContent);
	}

}
