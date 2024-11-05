package com.secure_mailer.backend;

import javafx.fxml.FXML;
//import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class SecretKeyController {

//    @FXML
//    private Label keyLabel;
    
    @FXML
    private TextField keyTextField;

//    // Method to set the secret key text in the label
//    public void setSecretKey(String secretKey) {
//        keyLabel.setText("Secret Key: " + secretKey);
//    }

    // Method to set the secret key text in the TextField
    public void setSecretKey(String secretKey) {
        keyTextField.setText(secretKey);
    }
    
 // Copy the secret key to the clipboard
    @FXML
    private void handleCopy() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(keyTextField.getText());
        clipboard.setContent(content);
    }

    // Close the window when the "Close" button is clicked
    @FXML
    private void handleClose() {
        Stage stage = (Stage) keyTextField.getScene().getWindow();
        stage.close();
    }
}