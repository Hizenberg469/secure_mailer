package com.secure_mailer.frontend;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AttachmentCell extends ListCell<Attachment> {

	  @FXML private Label attachmentName;
	  @FXML private ImageView attachmentImg;
	  private Image thumbnail;
	  
	  public AttachmentCell() { loadFXML(); }
	  
	  private void loadFXML() {
		try {
		        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/attachment_cell.fxml"));
		        loader.setController(this);
		        loader.setRoot(this);
		        loader.load();
		    }
		    catch (IOException e) {
		    	e.getMessage();
		        e.printStackTrace();
		    }		
	}

	  @Override
	    protected void updateItem(Attachment item, boolean empty) {
	        super.updateItem(item, empty);
	        if(empty) {
	            setText(null);
	            setContentDisplay(ContentDisplay.TEXT_ONLY);
	        } else {  	
	            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	            Attachment attachment = this.getItem();
	            String name = attachment.getName();
	            attachmentName.setText(name);
	            this.setTooltip( new Tooltip(name));
	            
	            if (attachment.getImage() == null) {
		            MimeBodyPart mbp = attachment.getMBP(); 
		            try {
		    			if (mbp.getContentType().contains("IMAGE")) {
		    				Service<Void> addImgService = new Service<Void>() {
		    					@Override
		    					protected Task<Void> createTask() {
		    						return new Task<Void>() {
		    							@Override
		    							protected Void call() throws Exception {
		    								thumbnail = new Image(mbp.getInputStream());
		    								attachment.setImage(thumbnail);
		    								return null;
		    							}};
		    					}
		    				};
		    				addImgService.setOnSucceeded(e -> { if (thumbnail != null)attachmentImg.setImage(thumbnail); });
		    				addImgService.start();	
		    			}
		    		} catch (MessagingException e) { e.printStackTrace(); }
	            } else { attachmentImg.setImage(attachment.getImage()); }
	        }
	    }
}