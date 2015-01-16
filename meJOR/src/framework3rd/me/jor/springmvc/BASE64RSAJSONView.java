package me.jor.springmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.jor.common.GlobalObject;
import me.jor.util.Base64;
import me.jor.util.RSA;
import me.jor.util.RSAPrivateKeyGetter;

import org.springframework.web.servlet.view.AbstractView;
/**
 * <bean class="org.springframework.web.servlet.view.BeanNameViewResolver">  
 <property name="order" value="1" />  
</bean>  
<bean id="base64RSAJSONView" class="me.jor.springmvc.BASE64RSAJSONView" />  

在controller中把View的spring id作为属性值注入，然后return new ModelAndView(viewName).addAllObject(resultMap);
//viewName是controller的属性，值就是该类在Spring配置文件中的id
 * @author wujingrun
 *
 */
public class BASE64RSAJSONView extends AbstractView {

	private String charset;
	private RSAPrivateKeyGetter privateKeyGetter;
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public RSAPrivateKeyGetter getPrivateKeyGetter() {
		return privateKeyGetter;
	}
	public void setPrivateKeyGetter(RSAPrivateKeyGetter privateKeyGetter) {
		this.privateKeyGetter = privateKeyGetter;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.getOutputStream().write(
			Base64.encode(
				RSA.encryptByPrivateKey(
					GlobalObject.getJsonMapper().writeValueAsString(model), 
					privateKeyGetter.get())).getBytes(charset));
	}

}
