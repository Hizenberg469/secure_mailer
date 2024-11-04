package com.secure_mailer.frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import javax.mail.BodyPart;

import javax.mail.Multipart;

public class EmailMessage {
	private SimpleStringProperty sender;
    private SimpleObjectProperty<Date> date;
    private SimpleStringProperty title;
    private boolean selected = false;
    private Message message;
    private String demoMessage;
    private List<Attachment> attachmentList = new ArrayList<Attachment>();
	private boolean attachmentLoaded = false;
	private boolean isRead;
	private SecretKey sKey;
	private boolean isAuthenticated;
	
	class StringToSecretKey {
	    public static SecretKey stringToSecretKey(String encodedKey) {
	        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
	        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); // Replace "AES" with the appropriate algorithm
	    }
	}

	public String extractCustomDataFromEmail(Message message) throws MessagingException, IOException {
	    if (message.isMimeType("multipart/*")) {
	        Multipart multipart = (Multipart) message.getContent();

	        for (int i = 0; i < multipart.getCount(); i++) {
	            BodyPart part = multipart.getBodyPart(i);

	            if ("<custom-data>".equals(part.getHeader("Secret-Key")[0])) {
	                // Extract custom data
	                String customData = (String) part.getContent();
	                return customData;
	            }
	        }
	    }
	    return null; // Return null if no custom data found
	}
    
	public EmailMessage(String sender, Date date, String title, Message msg, boolean isRead) {
		this.sender = new SimpleStringProperty(sender);
		this.date = new SimpleObjectProperty<Date>(date);
		this.title = new SimpleStringProperty(title);
		
		this.message = msg;
		this.isRead = isRead;
		
		this.isAuthenticated = false;
	}

	public String getFromEmailID() {
		Address[] senderAddresses;
		try {
			senderAddresses = this.message.getFrom();
			String senderEmail = "";
			if (senderAddresses != null && senderAddresses.length > 0) {
			    InternetAddress senderAddress = (InternetAddress) senderAddresses[0];
			    senderEmail = senderAddress.getAddress();
//			    System.out.println("Sender Email: " + senderEmail);
			}
			
			return senderEmail;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} // or message.getSender()

		
	}
	
	public void setIsAuthenticated(boolean isAuth) { this.isAuthenticated = isAuth; }
	public boolean getIsAuthenticated() { return this.isAuthenticated; }
	public SecretKey getSecretKey() { return sKey; }
	public String getSender() { return sender.get(); }
	public Date getDate() { return date.get(); }
	public String getTitle() { return title.get(); }
	public Message getMessage() { return message; }
	public String getDemoMessage() { return demoMessage; }
	public List<Attachment> getAttachments() { return attachmentList; }
	public boolean isAttachmentLoaded() { return attachmentLoaded; }
	public void setAttachmentLoaded() { attachmentLoaded = true; }
	public void setDemoMessage(String demo) { this.demoMessage = demo; }
	public void setSelected(boolean b) { selected = b; }
	public void setRead(boolean b) { isRead = b; }
	public boolean isRead() { return isRead; }
	public boolean isSelected() { return selected; }
	public boolean hasAttachment() { return !attachmentList.isEmpty(); }
	
	public synchronized void addAttachment(MimeBodyPart mbp) {
		Attachment attachment = new Attachment(mbp);
		if (attachment.getName() != null) {
			if (!attachmentList.isEmpty()) {
				for (Attachment a : attachmentList) {
					if (a == null || !a.getName().equals(attachment.getName()))continue;
					else {return;}
				}
			}
			attachmentList.add(attachment);
		}
	}
}