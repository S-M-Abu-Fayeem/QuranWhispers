package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RemoveVerse {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public String DELETE(String email,int valueOfToken, String mood, int ayat, String surah) {
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
                    int UserId = resultSet.getInt("id");
                    PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM FAV_VERSE WHERE user_id = ? AND mood = ? AND ayat = ? AND surah = ?");

                    preparedStatement2.setInt(1, UserId);
                    preparedStatement2.setString(2, mood);
                    preparedStatement2.setInt(3, ayat);
                    preparedStatement2.setString(4, surah);

                    int checker = preparedStatement2.executeUpdate();
                    if (checker > 0) data.addProperty("status", "success");
                    else data.addProperty("status", "failed");
                } else {
                    data.addProperty("Status", "failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            data.addProperty("Status", "failed");

        }
        return gson.toJson(data);
    }
}
