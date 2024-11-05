package com.secure_mailer.backend.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import com.secure_mailer.backend.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.crypto.SecretKey;
import java.util.Base64;

public class EmailSendingService extends Service<Void> {

	private EmailAccount emailAccount;
	private String subject;
	private String recipient;
	private String content; 
	private List<File> attachments = new ArrayList<File>();
	//private SecretKey sKey;
	
	class SecretKeyToString {
	    public static String secretKeyToString(SecretKey secretKey) {
	        // Convert SecretKey to byte array and then to Base64 string
	        byte[] encodedKey = secretKey.getEncoded();
	        return Base64.getEncoder().encodeToString(encodedKey);
	    }
	}
	
	public EmailSendingService(EmailAccount emailAccount, String subject, String recipient, String content, List<File> list
			,SecretKey sKey
			) {
		this.emailAccount = emailAccount;
		this.subject = subject;
		this.recipient = recipient;
		this.content = content;
		this.attachments = list;
		//this.sKey = sKey;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					
					System.out.println("From email id : "+emailAccount.getAddress());
					MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
					mimeMessage.setFrom(new InternetAddress(emailAccount.getAddress()+"@peachy.in.net"));
					mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
					mimeMessage.setSubject(subject);
					
					//Embedding Secret Key.
//					MimeBodyPart customDataPart = new MimeBodyPart();
//				    customDataPart.setText(SecretKeyToString.secretKeyToString(sKey), "UTF-8");
//				    customDataPart.setHeader("Secret-Key", "<custom-data>");
					
					Multipart multipart = new MimeMultipart();
					BodyPart messageBodyPart = new MimeBodyPart();	
					messageBodyPart.setContent(content, "text/html");
					multipart.addBodyPart(messageBodyPart);
					
					
					//Adding Secret Key to message.
					//multipart.addBodyPart(customDataPart);
					
					mimeMessage.setContent(multipart);
					
					if(attachments.size()>0) {
						for (File file: attachments) {
							MimeBodyPart mimeBodyPart = new MimeBodyPart();
							DataSource source = new FileDataSource(file.getAbsolutePath());
							mimeBodyPart.setDataHandler(new DataHandler(source));
							mimeBodyPart.setFileName(file.getName());
							multipart.addBodyPart(mimeBodyPart);
						}
					}
					
					Transport transport = emailAccount.getSession().getTransport();
					transport.connect(
							emailAccount.getProperties().getProperty("outgoingHost"),
							emailAccount.getAddress(),
							emailAccount.getPassword());
					transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
					transport.close();
				} catch(MessagingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} return null; 
			}
		};
	}
	
}