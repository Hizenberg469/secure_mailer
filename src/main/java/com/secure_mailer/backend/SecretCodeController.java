package com.secure_mailer.backend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public class SecretCodeController {

    @FXML private TextField emailField;
    @FXML private TextField secretCodeField;
    @FXML private TableView<EmailSecretCode> secretCodesTable;
    @FXML private TableColumn<EmailSecretCode, String> emailColumn;
    @FXML private TableColumn<EmailSecretCode, String> secretCodeColumn;

    private ObservableList<EmailSecretCode> dataList;

    public void initialize() {
        dataList = FXCollections.observableArrayList();
        secretCodesTable.setItems(dataList);
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        secretCodeColumn.setCellValueFactory(cellData -> cellData.getValue().secretCodeProperty());
        loadAllEntries();
    }

    @FXML
    private void handleAdd() {
        String email = emailField.getText();
        String secretCode = secretCodeField.getText();
        try {
            SecretCodeDAO.addSecretCode(email, secretCode);
            //dataList.add(new EmailSecretCode(email, secretCode));
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Could not add secret code.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        String email = emailField.getText();
        String secretCode = secretCodeField.getText();
        try {
            SecretCodeDAO.updateSecretCode(email, secretCode);
            //loadAllEntries();
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Could not update secret code.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        String email = emailField.getText();
        try {
            SecretCodeDAO.deleteSecretCode(email);
            //loadAllEntries();
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Could not delete secret code.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAll() {
        loadAllEntries();
    }

    private void loadAllEntries() {
        try {
            dataList.clear();
            for (EmailSecretCode entry : SecretCodeDAO.getAllEntries()) {
                dataList.add(entry);
            }
        } catch (SQLException e) {
            showAlert("Error", "Could not load entries.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        emailField.clear();
        secretCodeField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}