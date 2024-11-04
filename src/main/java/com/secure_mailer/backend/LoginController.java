package com.secure_mailer.backend;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController{

	@FXML private TextField emailField;
    @FXML private PasswordField passwordField;
	private EmailManager em;

	public LoginController(EmailManager emailManager) {
		super("/fxml/login.fxml", emailManager);
		em = emailManager;
	}
	
	@FXML void loginAction() {
		String email = emailField.getText();
		String password = passwordField.getText();
		
		saveEmailID(email);
		
		em.login(email, password);
		em.fetchFolders();
    }
	
	private void saveEmailID(String username) {	
		this.emailID = username;
	}
}