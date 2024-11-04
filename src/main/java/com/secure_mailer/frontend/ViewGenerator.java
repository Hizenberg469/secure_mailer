package com.secure_mailer.frontend;

import java.io.IOException;
import java.io.InputStream;
import com.secure_mailer.backend.BaseController;
import com.secure_mailer.backend.LoginController;
import com.secure_mailer.backend.EmailManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewGenerator {
	
	private static String theme = "/css/themeDefault.css";
	private static String fontFile1 = "/font/Myriad_Pro_Regular.ttf";
	private static String fontFile2 = "/font/Myriad_Pro_Light_SemiCondensed.otf";
	private static Stage stage;
	private static BaseController logincontroller;
	
	public static void initialize(EmailManager em) {	
		loadFont();
		logincontroller = new LoginController(em);
		stage = new Stage();
		stage.getIcons().add(new Image("/img/default/email-client.png"));
        stage.setTitle("EmailClient");
		stage.setScene(initializeScene(logincontroller));
		stage.show();
	}
	
	public static void showMainWindow(EmailManager em) {
		stage.close();
		BaseController controller = new MainWindowController(em, logincontroller.getEmailID());
		stage = new Stage();
		stage.getIcons().add(new Image("/img/default/email-client.png"));
        stage.setTitle("EmailClient");
		stage.setScene(initializeScene(controller));
		stage.show();
	}
	
	public static void closeStage(Stage stage) { stage.close(); }
	
	private static Scene initializeScene(BaseController controller) {
		FXMLLoader fxmlLoader = new FXMLLoader(ViewGenerator.class.getResource(controller.getFXML()));
		fxmlLoader.setController(controller);
		Parent parent;
		try { parent = fxmlLoader.load(); } 
		catch (IOException e) { e.printStackTrace(); return null; }
		Scene scene = new Scene(parent);
		updateStyles(scene);
		return scene;	
	}
	
	private static void updateStyles(Scene scene) {
		scene.getStylesheets().clear();
		scene.getStylesheets().add(ViewGenerator.class.getResource(theme).toExternalForm());
	}
	
	private static void loadFont() {
		try (InputStream in = ViewGenerator.class.getResourceAsStream(fontFile1);
			 InputStream in2 = ViewGenerator.class.getResourceAsStream(fontFile2)) {
			if (in != null) {
				Font.loadFont(in, 1);
				Font.loadFont(in2, 1);
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
}
