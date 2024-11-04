package com.secure_mailer.backend;

public class BaseController {
	
	private String fxml;
	private EmailManager emailManager;
	protected String emailID;

	public BaseController(String fxml, EmailManager emailManager) {
		this.fxml = fxml;
		this.emailManager = emailManager;
	}
	
	public String getEmailID() { return this.emailID; }
	
	public String getFXML() { return fxml; }
	public EmailManager getEmailManager() { return emailManager; }
}