package com.secure_mailer.frontend;

import java.awt.Desktop;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.secure_mailer.backend.service.EmailSendingService;
import com.secure_mailer.backend.service.MessageRendererService;

import com.secure_mailer.backend.BaseController;
import com.secure_mailer.backend.DatabaseSetup;
import com.secure_mailer.backend.EmailManager;
import com.secure_mailer.backend.SecretCodeDAO;
import com.secure_mailer.backend.ValidatorClient;
import com.secure_mailer.backend.EmailAccount;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;
import javafx.stage.FileChooser;

import javafx.animation.PauseTransition;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import javafx.util.Duration;
//import javafx.application.Platform;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

//import java.util.concurrent.atomic.AtomicBoolean;


public class MainWindowController extends BaseController implements Initializable {
	
	 @FXML private TreeView<String> folderSelection;
	 @FXML private ListView<EmailMessage> emailSelection;
	 @FXML private ListView<Attachment> attachmentList;
	 @FXML private AnchorPane messagePane;
	 @FXML private Label messageTitle;
	 @FXML private Label messageSenderName;
	 @FXML private Label messageAttachmentLabel;
	 @FXML private Label messageDate;
	 @FXML private WebView messageViewShort;
	 @FXML private WebView messageViewLong;
	 @FXML private ImageView defaultMessageViewBG;
	 @FXML private Label userNameLabel; 
	 @FXML private AnchorPane composePane;
	 @FXML private TextField composeTo;
	 @FXML private TextField composeTitle;
	 @FXML private HTMLEditor htmlEditor;
	 @FXML private Line footerDiv;
	 @FXML private ImageView composeAttachIcon;
	 private static SimpleDateFormat dateFormat = new SimpleDateFormat("M'/'d'/'YYYY   H:mm");
	 private ArrayList<File> attachments = new ArrayList<File>();
	 private MessageRendererService mrs;
	 private EmailManager emailManager;
	 private StringBuffer stringBuffer;
	 private String htmlBackup;
	 private SecretKey sKey;
	 private String fromEmailID;
	 
	 public MainWindowController(EmailManager emailManager, String fromEmailID) {
		 super("/fxml/main.fxml", emailManager);
		 this.emailManager = emailManager;
		 this.stringBuffer = new StringBuffer();
		 this.fromEmailID = fromEmailID;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		emailSelection.setCellFactory(new Callback<ListView<EmailMessage>, ListCell<EmailMessage>>(){
			@Override
		    public ListCell<EmailMessage> call(ListView<EmailMessage> param) { return new EmailCell(); }
		});
		
		attachmentList.setCellFactory(new Callback<ListView<Attachment>, ListCell<Attachment>>(){
			@Override
			public ListCell<Attachment> call(ListView<Attachment> param) { return new AttachmentCell(); }	
		});

		htmlBackup = htmlEditor.getHtmlText();
		messageAttachmentLabel.setGraphic(IconResolver.getIcon("paper-clip"));
		userNameLabel.setText(emailManager.getEmailAccount().getAddress());
		userNameLabel.setText("email.account@gmail.com");
		setUpMRS();
		setUpFolderSelection();
		setUpMessageSelection();
		setUpAttachmentSelection();
		
		DatabaseSetup.setupDatabase();
	}
	
	private void setUpMRS() { mrs = new MessageRendererService(stringBuffer); }
	
	private void setUpFolderSelection() {
		folderSelection.setRoot(emailManager.getFolderRoot());
		folderSelection.setShowRoot(false);
		folderSelection.setOnMouseClicked(e -> {
			FolderTreeItem item = (FolderTreeItem)folderSelection.getSelectionModel().getSelectedItem();	
			if(item != null) {
				emailManager.setSelectedFolder(item);
				emailSelection.setItems(item.getEmailMessages());	
			}
		});
	}
	
//	private void setUpMessageSelection() { 
//	    emailSelection.setOnMouseClicked(event -> {    
//	        attachmentList.getItems().clear();
//	        EmailMessage emailMessage = emailSelection.getSelectionModel().getSelectedItem();
//	        
//	        if (emailMessage != null) {
//	            // Start a new thread for TCP communication
//	        	
//	            new Thread(() -> {
//	            	
//	        	AtomicBoolean isAuthenticated = new AtomicBoolean(true);
//	            	
//	            	
//	            	if( emailMessage.getFromEmailID().equals(this.fromEmailID + "@peachy.in.net") == false) {
//		            	String fromEmailId = emailMessage.getSender();
//		            	String toEmailId = emailManager.getEmailAccount().getAddress();
//		            	String emlHash = generateEmlHash(emailMessage.getMessage(), emailMessage.getSecretKey());
//		            	
//		            	ValidatorClient receiver = new ValidatorClient("peachy.in.net",2001);
//		                isAuthenticated.set(receiver.bringCode(fromEmailId, toEmailId, emlHash));
//	            	}
//	                // Check if the details are authenticated
////	                if (isAuthenticated) {
//	                    // Update the email manager and message view
//	                    emailManager.setSelectedMessage(emailMessage);
//	                    
//	                    Platform.runLater(() -> {
//	                        if (!composePane.isVisible()) setMessageViewVisible(true);                        
//	                        mrs.setEmailMessage(emailMessage);
//	                        mrs.setOnSucceeded(e -> {
//	                            if (emailMessage.hasAttachment()) {
//	                                messageViewShort.getEngine().loadContent(stringBuffer.toString());
//	                                setAttachmentView(true);
//	                                emailMessage.setAttachmentLoaded();
//	                                
//	                                if (isAuthenticated.get()) {
//	                                
//	                                	loadAttachments();
//	                                
//	                                } else {
//	            	                    // Show authentication failure message
//	            	                    Platform.runLater(() -> showAlert("Authentication Failed", "Unable to authenticate details from the server."));
//	            	                }
//	                                
//	                            } else {
//	                            	
//	                            	if (isAuthenticated.get()) {
//	                                messageViewLong.getEngine().loadContent(stringBuffer.toString());
//	                                setAttachmentView(false);
//	                                
//	                            	 } else {
//	             	                    // Show authentication failure message
//	             	                    Platform.runLater(() -> showAlert("Authentication Failed", "Unable to authenticate details from the server."));
//	             	                }
//	                            }
//	                        });
//	                        mrs.restart();
//	                        messageSenderName.setText(emailMessage.getSender().replaceAll("lis.email.ttest@gmail.com", "email.account@gmail.com"));
//	                        messageTitle.setText(emailMessage.getTitle());
//	                        messageDate.setText(dateFormat.format(emailMessage.getDate())); 
//	                    
//	                    });
////	                } else {
////	                    // Show authentication failure message
////	                    Platform.runLater(() -> showAlert("Authentication Failed", "Unable to authenticate details from the server."));
////	                }
//	            }).start();
//	        }
//	    });
//	}
	
	private void setUpMessageSelection() { 
		emailSelection.setOnMouseClicked(event -> {	
			attachmentList.getItems().clear();
			EmailMessage emailMessage = emailSelection.getSelectionModel().getSelectedItem();
			
			if(emailMessage != null) {
				emailManager.setSelectedMessage(emailMessage);
				if (!composePane.isVisible())setMessageViewVisible(true);						
				mrs.setEmailMessage(emailMessage);
				mrs.setOnSucceeded(e -> {
					if(emailMessage.hasAttachment()) {
						messageViewShort.getEngine().loadContent(stringBuffer.toString());
						setAttachmentView(true);
						emailMessage.setAttachmentLoaded();
						loadAttachments();
					} else {
						messageViewLong.getEngine().loadContent(stringBuffer.toString());
						setAttachmentView(false);
					} 
				});
				mrs.restart();
				messageSenderName.setText(emailMessage.getSender().replaceAll("lis.email.ttest@gmail.com", "email.account@gmail.com"));
				messageTitle.setText(emailMessage.getTitle());
				messageDate.setText(dateFormat.format(emailMessage.getDate()));			
			}
		});
	}
	
	@FXML
	void decryptEmail() {
		
		attachmentList.getItems().clear();
		EmailMessage emailMessage = emailSelection.getSelectionModel().getSelectedItem();
		
		boolean isAuthenticated = false;
		if( emailMessage.getFromEmailID().equals(this.fromEmailID + "@peachy.in.net") == false) {
        	String fromEmailId = emailMessage.getSender();
        	String toEmailId = emailManager.getEmailAccount().getAddress();
        	String emlHash = generateEmlHash(emailMessage.getMessage(), emailMessage.getSecretKey());
        	
        	ValidatorClient receiver = new ValidatorClient("peachy.in.net",2001);
            isAuthenticated = receiver.bringCode(fromEmailId, toEmailId, emlHash);
    	}
		else {
			isAuthenticated = true;
		}
		
		emailMessage.setIsAuthenticated(isAuthenticated);
		
		if(emailMessage != null) {
			if (isAuthenticated) {
				emailManager.setSelectedMessage(emailMessage);
				if (!composePane.isVisible())setMessageViewVisible(true);						
				mrs.setEmailMessage(emailMessage);
				mrs.setOnSucceeded(e -> {
					if(emailMessage.hasAttachment()) {
						messageViewShort.getEngine().loadContent(stringBuffer.toString());
						setAttachmentView(true);
						emailMessage.setAttachmentLoaded();
						loadAttachments();
					} else {
						messageViewLong.getEngine().loadContent(stringBuffer.toString());
						setAttachmentView(false);
					} 
				});
				mrs.restart();
				messageSenderName.setText(emailMessage.getSender().replaceAll("lis.email.ttest@gmail.com", "email.account@gmail.com"));
				messageTitle.setText(emailMessage.getTitle());
				messageDate.setText(dateFormat.format(emailMessage.getDate()));
			}
			else {
				//Show authentication failure message
				Platform.runLater(() -> showAlert("Authentication Failed", "Unable to authenticate details from the server."));
			}
		}
		
		emailMessage.setIsAuthenticated(false);
	}
	
	private void showAlert(String title, String content) {
	    Alert alert = new Alert(AlertType.INFORMATION);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(content);
	    alert.show();

	    // Create a PauseTransition for 10 seconds
	    PauseTransition delay = new PauseTransition(Duration.seconds(10));
	    delay.setOnFinished(event -> alert.close()); // Close alert after 10 seconds
	    delay.play(); // Start the timer
	}
	
	private void setUpAttachmentSelection() {
		attachmentList.setOnMouseClicked(e -> {
			Attachment selected = attachmentList.getSelectionModel().getSelectedItem();
			attachmentList.getSelectionModel().clearSelection();
			if (selected != null) {
				File attachment = new File(selected.getDownloadPath());
				if (!attachment.exists())selected.downloadAttachment();	
				else {
					Desktop desktop = Desktop.getDesktop();
					try { desktop.open(attachment); } 
					catch (Exception exp) { exp.printStackTrace(); }
				}
			}
		});
	}

	@FXML 
	void composeCancel() {
		setComposeViewVisible(false);
		if (!messageSenderName.getText().isBlank())setMessageViewVisible(true);
    }

    @FXML 
    void composeKeyPressed() {
    	setMessageViewVisible(false);
    	setComposeViewVisible(true);
    }
    
    @FXML 
    void composeSendPressed() {
    	
    	String toId = composeTo.getText();
    	String secretKey = "";
    	
    	try {
			secretKey = SecretCodeDAO.getSecretCode(toId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
	        KeyGenerator keyGen;
	        SecretKey stKey = null;
	        
			try {
				keyGen = KeyGenerator.getInstance("AES");
				keyGen.init(128); // You can also use 192 or 256-bit key size
				stKey = keyGen.generateKey();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
			secretKey = SecretKeyToString.secretKeyToString(stKey);
			e.printStackTrace();
		}
    	
    	String emlHash = generateEmlHash(
    			emailManager.getEmailAccount(),
    			composeTitle.getText(),
    			toId,
    			htmlEditor.getHtmlText(),
    			attachments,
    			secretKey);
    	
    	EmailSendingService emailSenderService = new EmailSendingService(
				emailManager.getEmailAccount(),
				composeTitle.getText(),
				toId,
				htmlEditor.getHtmlText(),
				attachments,
				this.sKey);
    	
    	
    	emailSenderService.setOnSucceeded(e -> {
    		
    		String fromEmailId = emailManager.getEmailAccount().getAddress();
    		String toEmailId = toId;
  
    		ValidatorClient sender = new ValidatorClient("peachy.in.net", 2000);
    		
    		try {
				sender.sendRecord(fromEmailId, toEmailId, emlHash);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    	});
    	
    	
    	emailSenderService.start();
    	setComposeViewVisible(false);
    	composeAttachIcon.setVisible(false);
		composeTitle.clear();
		composeTo.clear();
		htmlEditor.setHtmlText(htmlBackup);
    	if (!messageSenderName.getText().isBlank())setMessageViewVisible(true);
    }
       
    @FXML
    void addAttachment() {
    	FileChooser fileChooser = new FileChooser();
    	File selectedFile = fileChooser.showOpenDialog(null);
    	if(selectedFile != null){
    		attachments.add(selectedFile);
    		composeAttachIcon.setVisible(true);
    	}
    }
    
    @FXML
    private void openCRUDWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crud_window.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Manage Secret Codes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadAttachments() {
		EmailMessage emailMessage = emailManager.getSelectedMessage();
		if (emailMessage != null && emailMessage.hasAttachment()) {
			ObservableList<Attachment> attachments = FXCollections.observableArrayList(emailMessage.getAttachments());
			attachmentList.getItems().addAll(attachments);
			attachmentList.setVisible(true);
		} else { attachmentList.setVisible(false); }
	}
	
	private void setAttachmentView(boolean b) {
		messageViewLong.setVisible(!b);
		messageViewShort.setVisible(b);
		footerDiv.setVisible(b);
		messageAttachmentLabel.setVisible(b);
		attachmentList.setVisible(b);
	}
	
	private void setMessageViewVisible(boolean b) {
		messagePane.setVisible(b);
		defaultMessageViewBG.setVisible(!b);
	}
	
	private void setComposeViewVisible(boolean b) {
		composePane.setVisible(b);
		defaultMessageViewBG.setVisible(!b);
	}
	
    @FXML
    void starKeyAction() {}

    @FXML
    void trashKeyAction() {}
    
    public String MessageToString(Message Message) throws MessagingException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Message.writeTo(outputStream);
        return outputStream.toString("UTF-8"); // Specify encoding if needed
    }
    
    private String generateEmlHash(Message message, SecretKey key) {
    	try {
    		
    		String msg = MessageToString(message);
			String encryptMessage = encryptMessage(msg, key);
			
			return encryptMessage;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
			return "";
		}
    }
    
    
    private String generateEmlHash(EmailAccount emailAccount, String subject, 
    		String recipient, 
    		String content,
    		List<File> attachments,
    		String secretKey){
    	
		String message;
		try {
			
			MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
			mimeMessage.setFrom(emailAccount.getAddress());
			mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
			mimeMessage.setSubject(subject);
			
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();	
			messageBodyPart.setContent(content, "text/html");
			multipart.addBodyPart(messageBodyPart);
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
			message = MessageToString(mimeMessage);
			String encryptMessage = encryptMessage(message,StringToSecretKey.stringToSecretKey(secretKey));
			return encryptMessage;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
    }
    
    
//    private String encryptMessage(String message) throws Exception{
//    	// Generate AES key
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//        keyGen.init(128); // You can also use 192 or 256-bit key size
//        SecretKey secretKey = keyGen.generateKey();
//        
//        
//        this.sKey = secretKey;
//
//        // Initialize Cipher for encryption
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
//
//        // Convert encrypted bytes to a readable format
//        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
//        
//        return encryptedText;
//        // Initialize Cipher for decryption
////        cipher.init(Cipher.DECRYPT_MODE, secretKey);
////        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
////        String decryptedText = new String(decryptedBytes);
////        System.out.println("Decrypted Text: " + decryptedText);
//       
//    }
    
    private String encryptMessage(String message, SecretKey secretKey) throws Exception{
    	// Generate AES key
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//        keyGen.init(128); // You can also use 192 or 256-bit key size
//        SecretKey secretKey = keyGen.generateKey();

        // Initialize Cipher for encryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        // Convert encrypted bytes to a readable format
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        
        return encryptedText;
        // Initialize Cipher for decryption
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
//        String decryptedText = new String(decryptedBytes);
//        System.out.println("Decrypted Text: " + decryptedText);
       
    }
    
    class StringToSecretKey {
	    public static SecretKey stringToSecretKey(String encodedKey) {
	        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
	        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); // Replace "AES" with the appropriate algorithm
	    }
	}

    class SecretKeyToString {
	    public static String secretKeyToString(SecretKey secretKey) {
	        // Convert SecretKey to byte array and then to Base64 string
	        byte[] encodedKey = secretKey.getEncoded();
	        return Base64.getEncoder().encodeToString(encodedKey);
	    }
	}
}