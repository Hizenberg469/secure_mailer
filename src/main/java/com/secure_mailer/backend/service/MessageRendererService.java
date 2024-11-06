package com.secure_mailer.backend.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import com.secure_mailer.frontend.EmailMessage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.mail.Part;


public class MessageRendererService extends Service<Object>{
	private EmailMessage emailMessage;
	private StringBuffer stringBuffer;
//	private boolean isAuthenticated;

	public MessageRendererService(StringBuffer stringBuffer) { 
		this.stringBuffer = stringBuffer; 
//		this.isAuthenticated = false;
	}
	
	public MessageRendererService(EmailMessage email) {
		this.emailMessage = email;
		this.stringBuffer = new StringBuffer();
//		this.isAuthenticated = false;
	}

	@Override
	protected Task<Object> createTask() {
		try { loadMessage(); } 
		catch (Exception e) { e.printStackTrace(); }
		return new Task<Object>() {
			@Override
			protected Object call() throws Exception { return null; }
		};
	}
	
	private void loadMessage() throws MessagingException, IOException {
		if (emailMessage.getMessage().isExpunged()) {this.cancel();}
		stringBuffer.setLength(0); 
		Message message = emailMessage.getMessage();
		String contentType = message.getContentType();
		if(isSimpleType(contentType)){
			
			if( emailMessage.getIsAuthenticated() ) {
				stringBuffer.append(message.getContent().toString());
			}
			else {
				stringBuffer.append(encryptMessage(message.getContent().toString()));
			}
			
		} else if (isMultipartType(contentType)) {
			Multipart multipart = (Multipart) message.getContent();
			loadMultipart(multipart, stringBuffer);
		} emailMessage.setDemoMessage(getDemoMessage());
	}
	
	private void loadMultipart(Multipart multipart, StringBuffer stringBuffer) throws MessagingException, IOException {
		for (int i = multipart.getCount() - 1; i >=0; i--) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			String contentType = bodyPart.getContentType();
			
			 // Check if it is an attachment
            String disposition = bodyPart.getDisposition();
            if (disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT) ) {
				MimeBodyPart mbp = (MimeBodyPart) bodyPart;
				if (!emailMessage.isAttachmentLoaded())emailMessage.addAttachment(mbp);
			} else if (isSimpleType(contentType)) {
				
				if( emailMessage.getIsAuthenticated() ) {
					stringBuffer.append(bodyPart.getContent().toString());
				}
				else {
					stringBuffer.append(encryptMessage(bodyPart.getContent().toString()));
				}
				
			} else if (isMultipartType(contentType)){
				Multipart multipart2 = (Multipart) bodyPart.getContent();
				loadMultipart(multipart2,stringBuffer); 
//			} else if (!isTextPlain(contentType)) {
			}
		}
	}
	
	private boolean isTextPlain(String contentType) {
		return contentType.contains("TEXT/PLAIN");
	}

	private boolean isSimpleType(String contentType) {
		if(contentType.contains("TEXT/HTML") ||   
		   contentType.contains("text")) {
			return true;
		} else return false;
		
//		contentType.contains("mixed") ||
	}
	
	private boolean isMultipartType(String contentType) {
		if (contentType.contains("multipart"))return true;
		else return false;
	}
	
	private String getDemoMessage() {
		String content = stringBuffer.toString();
		content = content.replaceAll("\\<.*?\\>", "");
		content = content.replaceAll("[.].*[{].*[}]", "");
		content = content.replaceAll("\\&nbsp;", " ");
		content = content.replaceAll("[/]", "");
		content = content.replaceAll("\s+|\\v+|\\h+", " ");
		content = content.replaceAll("[<][!][-][-].*[-][-][>]", "");
		content = content.replaceAll("[@].*[{].*[}]", "");
		content = content.replaceAll("[a-z]+[.][a-zA-Z]+\\h*[{].*[}]", "");
		content = content.replaceAll("[a-z]+[{].*[}]","");
		content = content.replaceAll("[*]\\h*[{].*[}]","");
		content = content.strip();
		if (content.length() > 100)content = content.substring(0, 100);
		if (content == null)content = "";
		return content;
	}
	
	public StringBuffer getStringBuffer() { return stringBuffer; }
	
	public void setEmailMessage(EmailMessage emailMessage) { this.emailMessage = emailMessage; }
	
//	public void setIsAuthenticated(boolean isAuthenticated) { this.isAuthenticated = isAuthenticated; }
	
    private String encryptMessage(String message) {
    	// Generate AES key
        KeyGenerator keyGen;
        String encryptedText = "";
		try {
			keyGen = KeyGenerator.getInstance("AES");
			
			 keyGen.init(128); // You can also use 192 or 256-bit key size
	        SecretKey secretKey = keyGen.generateKey();

	        // Initialize Cipher for encryption
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

	        byte[] encryptedBytes;
			
			encryptedBytes = cipher.doFinal(message.getBytes());	

	        // Convert encrypted bytes to a readable format
	        encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return encryptedText;
        // Initialize Cipher for decryption
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
//        String decryptedText = new String(decryptedBytes);
//        System.out.println("Decrypted Text: " + decryptedText);
       
    }
    
}
