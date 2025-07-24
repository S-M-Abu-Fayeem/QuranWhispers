package com.example.server;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Login {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public synchronized String GET(String userName, String password) {
        HashMap<String, String> data = new HashMap<>();
        Gson gson = new Gson();
        data.put("email", userName);
        data.put("status", "404");   // default
        data.put("token", "-1");

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            // Fetch hashed password and current token
            PreparedStatement ps = connection.prepareStatement("SELECT username, password, token, is_admin FROM USERS WHERE email = ?");
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int storedHash = rs.getInt("password");
                int existingToken = rs.getInt("token");
                boolean isAdmin = rs.getBoolean("is_admin");

                HashingFunction hasher = new HashingFunction();
                int inputHash = hasher.getHash(password);

                if (storedHash == inputHash) {
                    // Auth success
                    data.put("status", "200");

                    if (existingToken != -1) {
                        // Already logged in
                        System.out.println("User " + userName + " already logged in with token " + existingToken);
                        data.put("token", String.valueOf(existingToken));
                        data.put("username", rs.getString("username"));
                    } else {
                        // Generate new token
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String rawToken = timestamp + userName;
                        int newToken = hasher.getHash(rawToken);

                        PreparedStatement updateToken = connection.prepareStatement("UPDATE USERS SET token = ? WHERE email = ?");
                        updateToken.setInt(1, newToken);
                        updateToken.setString(2, userName);
                        updateToken.executeUpdate();

                        System.out.println("New token generated for " + userName + ": " + newToken);
                        data.put("token", String.valueOf(newToken));
                        data.put("username", rs.getString("username"));
                    }

                    data.put("admin", isAdmin ? "true" : "false");
                }
            }

            return gson.toJson(data);
        } catch (Exception e) {
            e.printStackTrace();
            data.put("status", "500");
            return gson.toJson(data);
        }
    }
}
