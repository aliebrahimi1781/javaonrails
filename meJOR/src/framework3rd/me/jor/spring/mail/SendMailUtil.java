package me.jor.spring.mail;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import me.jor.exception.MailSendingException;
import me.jor.util.Help;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SendMailUtil implements BeanNameAware,Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 347272490534837163L;
	
	private static final Log logger=LogFactory.getLog(SendMailUtil.class);
	
	private static final Pattern MAIL_CONTENT_KEY=Pattern.compile("#\\w+#");
	
	private JavaMailSender mailSender;
    private MimeMessageHelper msgHelper;
    private String from;
    private String mailto;
    private String contentTemplate;
    private String subject;
    private String beanId;
    
	public SendMailUtil(){}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	public void setMsgHelper(MimeMessageHelper msgHelper) {
		this.msgHelper = msgHelper;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public void setContentTemplate(String contentTemplate) {
		this.contentTemplate = contentTemplate;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContentTemplate() {
		return contentTemplate;
	}


    private static final ThreadLocal<String> mailContent=new ThreadLocal<String>();
    
	public void send(){
    	try {
    		msgHelper.setFrom(this.from);
    		msgHelper.setTo(mailto.split("\\s*;\\s*"));
    		msgHelper.setSubject(subject);
    		msgHelper.setText(MAIL_CONTENT_KEY.matcher(Help.convert(mailContent.get(), "")).replaceAll(""),true);
    		msgHelper.setSentDate(new Date());

    		mailSender.send(msgHelper.getMimeMessage());
		} catch (Exception e){
			logger.error("SendMailUtil.send()",e);
			throw new MailSendingException(e.getMessage(),e);
		}
    }
	
	public SendMailUtil setContent(String key, CharSequence text){
		String txt=mailContent.get();
		if(Help.isEmpty(txt)){
			txt=this.contentTemplate;
		}
		mailContent.set(txt.replace('#'+key+'#', text));
		return this;
	}
	public SendMailUtil setContent(Map<String,CharSequence> content){
		for(Map.Entry<String, CharSequence> entry:content.entrySet()){
			setContent(entry.getKey(),entry.getValue());
		}
		return this;
	}
	public SendMailUtil setContent(Object content){
		Method[] ms=content.getClass().getMethods();
		for(int i=0,l=ms.length;i<l;i++){
			Method m=ms[i];
			String mn=m.getName();
			if(mn.startsWith("get")){
				try{
					Object v=m.invoke(content);
					setContent(mn.substring(3),Help.convert(v, "").toString());
				}catch(Exception e){}
			}
		}
		return this;
	}
	public SendMailUtil setContent(String content){
		mailContent.set(content);
		return this;
	}
	public String getMailto() {
		return mailto;
	}

	public void setMailto(String mailto) {
		this.mailto = mailto;
	}

	@Override
	public void setBeanName(String beanId) {
		this.beanId=beanId;
	}
}
