package com.secure_mailer.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    private static final String DB_URL = "jdbc:h2:~/email_client_db"; // URL for H2 database
    private static final String USER = "sa"; // Default user for H2
    private static final String PASSWORD = ""; // Default password for H2

    /**
     * Sets up the database and creates the SecretCodes table if it does not exist.
     */
    public static void setupDatabase() {
        // Using try-with-resources to ensure resources are closed properly
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {

            // SQL statement to create the SecretCodes table if it doesn't exist
            String createTable = "CREATE TABLE IF NOT EXISTS SecretCodes (" +
                                 "email VARCHAR(255) PRIMARY KEY, " +
                                 "secretCode VARCHAR(255) NOT NULL)";

            stmt.execute(createTable); // Execute the create table statement
            System.out.println("Database setup completed successfully.");

        } catch (SQLException e) {
            System.err.println("Database setup failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        setupDatabase(); // Call the setupDatabase method
    }
}