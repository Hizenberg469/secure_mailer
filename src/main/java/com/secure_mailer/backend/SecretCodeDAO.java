package com.secure_mailer.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SecretCodeDAO {
    private static final String DB_URL = "jdbc:h2:~/email_client_db";

    public static void addSecretCode(String email, String secretCode) throws SQLException {
        String sql = "INSERT INTO SecretCodes (email, secretCode) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, "sa", "");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, secretCode);
            stmt.executeUpdate();
            System.out.println("Secret code added.");
        }
    }

    public static String getSecretCode(String email) throws SQLException {
        String sql = "SELECT secretCode FROM SecretCodes WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, "sa", "");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("secretCode") : null;
        }
    }

    public static void updateSecretCode(String email, String newSecretCode) throws SQLException {
        String sql = "UPDATE SecretCodes SET secretCode = ? WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, "sa", "");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newSecretCode);
            stmt.setString(2, email);
            stmt.executeUpdate();
            System.out.println("Secret code updated.");
        }
    }

    public static void deleteSecretCode(String email) throws SQLException {
        String sql = "DELETE FROM SecretCodes WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, "sa", "");
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
            System.out.println("Secret code deleted.");
        }
    }
    
    public static List<EmailSecretCode> getAllEntries() throws SQLException {
        String sql = "SELECT * FROM SecretCodes";
        List<EmailSecretCode> list = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, "sa", "");
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String email = rs.getString("email");
                String secretCode = rs.getString("secretCode");
                list.add(new EmailSecretCode(email, secretCode));
            }
        }
        return list;
    }
}
