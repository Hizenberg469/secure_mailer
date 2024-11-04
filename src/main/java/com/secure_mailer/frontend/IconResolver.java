package com.secure_mailer.frontend;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class IconResolver {

	public static Node getIcon(String name) {	
		name = name.toLowerCase();	
		ImageView imageView = new ImageView();
		imageView.setFitWidth(16);
		imageView.setFitHeight(16);
		
		try {
			if(name.contains("gmail"))imageView.setId("gmail-folder");			
			else if (name.contains("inbox"))imageView.setId("inbox-folder");
			else if (name.contains("all"))imageView.setId("all-folder");
			else if (name.contains("sent"))imageView.setId("sent-folder");
			else if (name.contains("spam"))imageView.setId("spam-folder");
			else if (name.contains("important"))imageView.setId("important-folder");
			else if (name.contains("draft"))imageView.setId("draft-folder");
			else if (name.contains("star"))imageView.setId("star-folder");
			else if (name.contains("trash"))imageView.setId("trash-folder");
			else if (name.contains("paper-clip")) {
				imageView.setId("paper-clip");
				imageView.setFitWidth(23);
				imageView.setFitHeight(23);
			} else imageView.setId("mail-folder");			
		} catch (Exception e) { e.printStackTrace(); return null; }		
		return imageView;
	}		
}