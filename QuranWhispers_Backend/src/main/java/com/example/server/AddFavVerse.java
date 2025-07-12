package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class AddFavVerse {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String SET(String email, int valueOfToken, String emotion, String theme, int ayah, String surah) {
        TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        if (tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {

                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT id FROM USERS WHERE email = ?");
                preparedStatement1.setString(1, email);
                ResultSet resultSet = preparedStatement1.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");

                    PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "INSERT INTO FAV_VERSE (user_id, emotion, theme, ayah, surah, timestamp) VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    preparedStatement2.setInt(1, userId);
                    preparedStatement2.setString(2, emotion);
                    preparedStatement2.setString(3, theme);
                    preparedStatement2.setInt(4, ayah);
                    preparedStatement2.setString(5, surah);
                    preparedStatement2.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

                    int checker = preparedStatement2.executeUpdate();
                    if (checker > 0) {
                        data.addProperty("status", "200");

                        PreparedStatement updateSaved = connection.prepareStatement(
                                "UPDATE USERS SET total_saved_verse = total_saved_verse + 1 WHERE id = ?"
                        );
                        updateSaved.setInt(1, userId);
                        updateSaved.executeUpdate();
                    } else {
                        data.addProperty("status", "500");
                    }
                } else {
                    data.addProperty("status", "401");
                }
            } catch (Exception e) {
                data.addProperty("status", "500");
                e.printStackTrace();
            }
        } else {
            data.addProperty("status", "404");
        }

        return gson.toJson(data);
    }
}
