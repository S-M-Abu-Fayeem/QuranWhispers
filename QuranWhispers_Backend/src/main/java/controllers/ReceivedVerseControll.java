package controllers;

import Validators.TokenValidator;
import Validators.UserExistanceChecker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.*;
import java.time.LocalDateTime;

import static com.example.server.HelloApplication.tokenValidator;

public class ReceivedVerseControll {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public synchronized String SEND(String email, int valueOfToken, String usernameOfFriend, String emotion, String theme, int ayah, String surah) {
        //TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("username", usernameOfFriend);

        if (tokenValidator.VALIDATE(email, valueOfToken)) {
            UserExistanceChecker obj = new UserExistanceChecker();
            if (!obj.doesExist(usernameOfFriend)) {
                data.addProperty("status", "404");
                data.addProperty("status_message", "This username does not exist. Please enter a valid username.");
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
                    insertVerse.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    insertVerse.executeUpdate();


                    ResultSet generatedKeys = insertVerse.getGeneratedKeys();
                    if (!generatedKeys.next()) {
                        data.addProperty("status", "500");
                        data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
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
                        data.addProperty("status_message", "The verse has been successfully sent.");
                        return gson.toJson(data);
                    } else {
                        data.addProperty("status", "500");
                        data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
                        return gson.toJson(data);
                    }
                } else {
                    data.addProperty("status", "500");
                    data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
                    return gson.toJson(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                data.addProperty("status", "500");
                data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
                return gson.toJson(data);
            }
        } else {
            data.addProperty("status", "401");
        }

        return gson.toJson(data);
    }
    public synchronized String DELETE(String email, int token, String senderUsername, String surah, int ayah, String theme, String emotion) {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        TokenValidator tokenValidator = new TokenValidator();

        if (!tokenValidator.VALIDATE(email, token)) {
            data.addProperty("status", "401");
            return gson.toJson(data);
        }

        try (Connection connection = DriverManager.getConnection(DB_URL)) {

            PreparedStatement psUser = connection.prepareStatement(
                        "SELECT id FROM USERS WHERE email = ?"
            );
            psUser.setString(1, email);
            ResultSet rsUser = psUser.executeQuery();

            if (!rsUser.next()) {
                data.addProperty("status", "404");
                return gson.toJson(data);
            }

            int userId = rsUser.getInt("id");

            PreparedStatement psFind = connection.prepareStatement(
                    "SELECT rv.id FROM REC_VERSE rv " +
                            "JOIN REC_VERSE_DETAIL rd ON rv.id = rd.rec_verse_id " +
                            "WHERE rv.user_id = ? AND rv.sender_username = ? AND rd.surah = ? AND rd.ayah = ? AND rd.theme = ? AND rd.emotion = ?"
            );
            psFind.setInt(1, userId);
            psFind.setString(2, senderUsername);
            psFind.setString(3, surah);
            psFind.setInt(4, ayah);
            psFind.setString(5, theme);
            psFind.setString(6, emotion);

            ResultSet rs = psFind.executeQuery();

            if (!rs.next()) {
                data.addProperty("status", "404");
                return gson.toJson(data);
            }

            int recId = rs.getInt("id");

            PreparedStatement psDelete = connection.prepareStatement(
                    "DELETE FROM REC_VERSE WHERE id = ?"
            );
            psDelete.setInt(1, recId);
            int rows = psDelete.executeUpdate();

            if (rows > 0) {
                PreparedStatement updateCount = connection.prepareStatement(
                        "UPDATE USERS SET total_received_verse = total_received_verse - 1 WHERE id = ?"
                );
                updateCount.setInt(1, userId);
                updateCount.executeUpdate();
                data.addProperty("status", "200");
            }
            else {
                data.addProperty("status", "500");
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "500");
        }
        return gson.toJson(data);
    }

}
