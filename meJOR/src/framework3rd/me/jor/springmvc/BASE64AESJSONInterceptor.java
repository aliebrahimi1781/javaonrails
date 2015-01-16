package me.jor.springmvc;

import java.util.Map;

import me.jor.common.GlobalObject;
import me.jor.util.AES;
import me.jor.util.AESParams;
import me.jor.util.AESParamsGetter;

public class BASE64AESJSONInterceptor extends AbstractBaseInterceptor{
	
	private AESParamsGetter aesParamsGetter;
	public AESParamsGetter getAesParamsGetter() {
		return aesParamsGetter;
	}
	public void setAesParamsGetter(AESParamsGetter aesParamsGetter) {
		this.aesParamsGetter = aesParamsGetter;
	}
	@Override
	protected Object beforeTransformParams(String params) throws Exception {
		AESParams aesParams=aesParamsGetter.get();
		return GlobalObject.getJsonMapper().readValue(
				AES.getAES(aesParams.getKey(), aesParams.getSalt(), aesParams.getIv(), getCharset())
				   .decryptFromBase64(params), Map.class);
	}

	@Override
	protected String generateLogText(Map logContent) throws Exception {
		return GlobalObject.getJsonMapper().writeValueAsString(logContent);
	}

}
