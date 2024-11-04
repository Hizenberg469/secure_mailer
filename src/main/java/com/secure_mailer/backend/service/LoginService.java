package com.secure_mailer.backend.service;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import com.secure_mailer.backend.EmailAccount;

public class LoginService {
	
	private EmailAccount emailAccount;
	
	public LoginService(EmailAccount emailAccount) {
		this.emailAccount = emailAccount;
		login();
	}
	
	private void login() {		
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailAccount.getAddress(), emailAccount.getPassword());
			}
		};
		
		Session session = Session.getInstance(emailAccount.getProperties(), authenticator);
		emailAccount.setSession(session);
		
		try {
			Store store = session.getStore("imaps");
			store.connect(emailAccount.getProperties().getProperty("incomingHost"), 
						  emailAccount.getAddress(), 
						  emailAccount.getPassword());	
			emailAccount.setStore(store);
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}	
	}
}