package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SendVerseToFriend {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String SEND(String email, int valueOfToken, String usernameOfFriend, String emotion, String theme, int ayah, String surah) {
        TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("username", usernameOfFriend);

        if (tokenValidator.VALIDATE(email, valueOfToken)) {
            UserExistanceChecker obj = new UserExistanceChecker();
            if (!obj.doesExist(usernameOfFriend)) {
                data.addProperty("status", "friend not found");
                return gson.toJson(data);
            }

            try (Connection connection = DriverManager.getConnection(DB_URL)) {


                PreparedStatement preparedStatementforsender = connection.prepareStatement("SELECT username FROM USERS WHERE email = ?");
                preparedStatementforsender.setString(1, email);
                ResultSet resultSet = preparedStatementforsender.executeQuery();
                resultSet.next();
                String usernameOfSender = resultSet.getString("username");


                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT id FROM USERS WHERE username = ?");
                preparedStatement1.setString(1, usernameOfFriend);
                ResultSet resultSet2 = preparedStatement1.executeQuery();

                if (resultSet2.next()) {
                    int userId = resultSet2.getInt("id");


                    PreparedStatement insertVerse = connection.prepareStatement(
                            "INSERT INTO REC_VERSE (user_id, sender_username, timestamp) VALUES (?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );
                    insertVerse.setInt(1, userId);
                    insertVerse.setString(2, usernameOfSender);
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                    insertVerse.setString(3, timestamp);
                    insertVerse.executeUpdate();


                    ResultSet generatedKeys = insertVerse.getGeneratedKeys();
                    if (!generatedKeys.next()) {
                        data.addProperty("status", "fail");
                        return gson.toJson(data);
                    }
                    int recId = generatedKeys.getInt(1);


                    PreparedStatement insertDetail = connection.prepareStatement(
                            "INSERT INTO REC_VERSE_DETAIL (rec_verse_id, emotion, theme, ayah, surah) VALUES (?, ?, ?, ?, ?)"
                    );
                    insertDetail.setInt(1, recId);
                    insertDetail.setString(2, emotion);
                    insertDetail.setString(3, theme);
                    insertDetail.setInt(4, ayah);
                    insertDetail.setString(5, surah);
                    int rows = insertDetail.executeUpdate();

                    if (rows > 0) {

                        PreparedStatement update = connection.prepareStatement(
                                "UPDATE USERS SET total_received_verse = total_received_verse + 1 WHERE id = ?"
                        );
                        update.setInt(1, userId);
                        update.executeUpdate();

                        data.addProperty("status", "200");
                    } else {
                        data.addProperty("status", "404");
                    }
                } else {
                    data.addProperty("status", "404");
                }
            } catch (Exception e) {
                e.printStackTrace();
                data.addProperty("status", "500");
            }
        } else {
            data.addProperty("status", "401");
        }

        return gson.toJson(data);
    }
}
