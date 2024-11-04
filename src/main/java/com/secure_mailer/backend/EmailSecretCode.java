package com.secure_mailer.backend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmailSecretCode {
    private final StringProperty email;
    private final StringProperty secretCode;

    public EmailSecretCode(String email, String secretCode) {
        this.email = new SimpleStringProperty(email);
        this.secretCode = new SimpleStringProperty(secretCode);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getSecretCode() {
        return secretCode.get();
    }

    public StringProperty secretCodeProperty() {
        return secretCode;
    }
}
