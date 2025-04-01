package kr.co.cs.common.publicutil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.http.HttpException;

import kr.co.cs.common.config.Const;
/**
 * file name   : MailUtil.java
 * desc        : SMS/FAX/EMAIL을 보낸다.
 * create date : 2009. 04. 15
 * @author     : buttle
 * @version    : 1.0.0
 */
public class MailUtil {
 
	/**
	 * method name : sendMail
	 * desc        : mail
	 * return	   : void
	 * create date : 2009. 04. 15
	 * @author     : buttle
	 * @version    : 1.0.0
	 */ 
	public void sendMail(String subject, String messageText, String[] toMailAddr, String[] ccMailAddr, String[] bccMailAddr, List fileList) {
 
		try {
			Message message;
			int cCnt = 0;
			int iCnt = 0;
			PopupAuthenticator pa = new PopupAuthenticator();
			pa.setPwd(Const.SMTP_ACCOUNT, Const.SMTP_PASSWORD);
			Authenticator auth = pa;			

			System.out.println("============================");
			System.out.println(Const.SMTP_HOST);
			System.out.println(Const.SMTP_FROM_MAIL);
			System.out.println("============================");
			
			
			// Create session
			Properties mailProps = new Properties();
			mailProps.put("mail.smtp.starttls.enable", "true");
			mailProps.put("mail.transport.protocol", "smtp");
			mailProps.put("mail.smtp.host", Const.SMTP_HOST);
			//mailProps.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			mailProps.put("mail.smtp.port", Const.SMTP_PORT);
			mailProps.put("mail.smtp.auth", "true");
			mailProps.put("mail.smtp.connectiontimeout", "1800000");
			mailProps.put("mail.smtp.timeout", "1800000");
 
			

			Session mailSession = Session.getInstance(mailProps, auth); 
			//sheisexy@buttle.co.kr
			InternetAddress fromAddr = new InternetAddress(Const.SMTP_FROM_MAIL, "GS CALTEX"); 
			message = new MimeMessage(mailSession);
			message.setFrom(fromAddr);			
 
			//InternetAddress[] toAddrs = {new InternetAddress(toMailAddr.trim())};
			for(int i=0; i<toMailAddr.length; i++) {
				if(!"".equals(toMailAddr[i].trim())) cCnt++;
			}
			InternetAddress[] toAddrs = new InternetAddress[cCnt];
			for(int i=0; i<toMailAddr.length; i++) {
				if(!"".equals(toMailAddr[i].trim()))
					toAddrs[iCnt++] = new InternetAddress(toMailAddr[i].trim());
			}
			message.setRecipients(Message.RecipientType.TO, toAddrs);
			
			if(ccMailAddr!=null && ccMailAddr.length>0) {
				cCnt = 0; iCnt = 0;
				//InternetAddress[] ccAddrs = {new InternetAddress(ccMailAddr.trim())};	
				for(int i=0; i<ccMailAddr.length; i++) {
					if(!"".equals(ccMailAddr[i].trim())) cCnt++;
				}
				InternetAddress[] ccAddrs = new InternetAddress[cCnt];
				for(int i=0; i<ccMailAddr.length; i++) {
					if(!"".equals(ccMailAddr[i].trim()))
						ccAddrs[iCnt++] = new InternetAddress(ccMailAddr[i].trim());
				}
				if(cCnt>0)
					message.setRecipients(Message.RecipientType.CC, ccAddrs);								
			}
			
			if(bccMailAddr!=null && bccMailAddr.length>0) {
				cCnt = 0; iCnt = 0;
				//InternetAddress[] bccAddrs = {new InternetAddress(bccMailAddr.trim())};
				for(int i=0; i<bccMailAddr.length; i++) {
					if(!"".equals(bccMailAddr[i].trim())) cCnt++;
				}
				InternetAddress[] bccAddrs = new InternetAddress[cCnt];
				for(int i=0; i<bccMailAddr.length; i++) {
					if(!"".equals(bccMailAddr[i].trim()))
						bccAddrs[iCnt++] = new InternetAddress(bccMailAddr[i].trim());
				}
				if(cCnt>0)
					message.setRecipients(Message.RecipientType.BCC, bccAddrs);
			}
			
			message.setSubject(MimeUtility.encodeText(subject, "euc-kr","B"));
					
			MimeBodyPart contentsBody = new MimeBodyPart();		
			//contentsBody.setContent(messageText.replaceAll(" ","&nbsp;"), "text/html;charset=euc-kr");
			contentsBody.setContent(messageText, "text/html;charset=euc-kr");
			
			Multipart multipart = new MimeMultipart();	    
			multipart.addBodyPart(contentsBody);  //내용
			
			MimeBodyPart fileBody;
			FileDataSource fds;
			String sendFileName;
			
			//첨부파일이 있다면			
			for (int i=0; i < fileList.size() ; i++){
				
				String[] fileArr = (String[])fileList.get(i);
				//fileArr[0] : view filename (abc.txt)
				//fileArr[1] : filepath (c:/)
				//fileArr[2] : server filename (adghhffff)
				if(fileArr.length!=3) continue;
				if("".equals(fileArr[0]) || "".equals(fileArr[1]) || "".equals(fileArr[2])) continue;
				
				fileBody = new MimeBodyPart();
			    fds = new FileDataSource(fileArr[1] + fileArr[2]);
			    fileBody.setDataHandler(new DataHandler(fds));
				sendFileName = MimeUtility.encodeText(fileArr[0], "EUC-KR", "B");
				fileBody.setFileName(sendFileName);
				multipart.addBodyPart(fileBody);
			}
			
			//문서에 포함된 첨부이미지파일이 있다면		
			/*
			for (int i=0; i < imgList.size() ; i++){
				
				String[] fileArr = (String[])imgList.get(i);
				//fileArr[0] : IMAGE ID (IMAGE_01)
				//fileArr[1] : filepath (c:/)
				//fileArr[2] : server filename (abc.gif)
				if(fileArr.length!=3) continue;
				if("".equals(fileArr[0]) || "".equals(fileArr[1]) || "".equals(fileArr[2])) continue;
				
				fileBody = new MimeBodyPart();
			    fds = new FileDataSource(fileArr[1] + fileArr[2]);
			    fileBody.setDataHandler(new DataHandler(fds));
				fileBody.setHeader("Content-ID","<"+fileArr[1]+">");
				multipart.addBodyPart(fileBody);
			}    
			*/
			
			message.setContent(multipart);
			Transport.send(message);
		} catch (IOException e) {
			//throw new MessagingException("MailUtil.sendMail : " + e);
			throw new RuntimeException("sendMail : " + e);			
		} catch (Exception e) {
			//throw new MessagingException("MailUtil.sendMail : " + e);
			throw new RuntimeException("sendMail : " + e);			
		}
	}
	
	/**
	 * file name   : MailUtil.java
	 * desc        : SMTP 계정인증한다.
	 * create date : 2009. 04. 15
	 * @author     : buttle
	 * @version    : 1.0.0
	 */
	private static class PopupAuthenticator extends Authenticator {
		String usrid = null;
		String pwd = null;

		public void setPwd(String usrid, String pwd) {
			this.usrid = usrid;
			this.pwd = pwd;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(usrid, pwd);
		}
	}

}
