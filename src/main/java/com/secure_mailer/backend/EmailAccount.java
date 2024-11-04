package com.secure_mailer.backend;

import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

public class EmailAccount {
	
	private String address;
	private String password;
	private Properties properties;
	private Store store;
	private Session session;
	private ArrayList<Folder> folders = new ArrayList<Folder>();
	
	public EmailAccount(String address, String password) {
		this.address = address;
		this.password = password;
		properties = new Properties();
		
		properties.put("incomingHost", "peachy.in.net");
		properties.put("mail.store.protocol", "imaps");
		properties.put("mail.transport.protocol", "smtps");
		properties.put("mail.smtps.host", "peachy.in.net");
		properties.put("mail.smtps.auth", "true");
		properties.put("outgoingHost", "peachy.in.net");
		
		//properties.put("mail.imap.host", "peachy.in.net"); // Replace with your IMAP server
		properties.put("mail.imaps.port", "993"); // Common port for IMAP SSL
		properties.put("mail.imaps.ssl.enable", "true");
		properties.put("mail.imaps.auth", "true");
		
		//properties.put("mail.smtp.host", "peachy.in.net"); // Replace with your SMTP server
		properties.put("mail.smtps.port", "465"); // Common port for SMTP SSL; check with your server if different
		properties.put("mail.smtps.auth", "true");
		properties.put("mail.smtps.ssl.enable", "true");
		//properties.put("mail.smtp.starttls.enable", "true"); // Optional if the server supports STARTTLS
	}

	public String getAddress() {return address;}
	public String getPassword() {return password;}
	public Properties getProperties() {return properties;}
	public Store getStore() {return store;}
	public Session getSession() {return session;}
	public ArrayList<Folder> getFolders() {return folders;}
	public void setProperties(Properties properties) {this.properties = properties;}
	public void setStore(Store store) {this.store = store;}
	public void setSession(Session session) {this.session = session;}
	@Override public String toString() {return address;}
}