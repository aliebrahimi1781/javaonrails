package me.jor.springmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.jor.common.GlobalObject;
import me.jor.util.AES;
import me.jor.util.AESParams;
import me.jor.util.AESParamsGetter;
import me.jor.util.Base64;
import me.jor.util.Help;

import org.springframework.web.servlet.view.AbstractView;

public class BASE64AESJSONView extends AbstractView{
	private String charset;
	private AESParamsGetter aesParamsGetter;
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public AESParamsGetter getAesParamsGetter() {
		return aesParamsGetter;
	}
	public void setAesParamsGetter(AESParamsGetter aesParamsGetter) {
		this.aesParamsGetter = aesParamsGetter;
	}
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		AESParams aesParams=aesParamsGetter.get();
		String charset=Help.isEmpty(this.charset)?aesParams.getCharset():this.charset;
		response.getOutputStream().write(
			Base64.encode(AES.getAES(
					aesParams.getKey(), aesParams.getSalt(), aesParams.getIv(), charset)
				   .encryptToBase64(
						   GlobalObject.getJsonMapper().writeValueAsString(model)))
				   .getBytes(charset));
	}
	
}
