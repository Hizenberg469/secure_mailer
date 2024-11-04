package com.secure_mailer;

import com.secure_mailer.backend.EmailManager;
import com.secure_mailer.frontend.ViewGenerator;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
	
	public static void main(String... args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {			
		EmailManager em = new EmailManager();	
		ViewGenerator.initialize(em);
	}
}
