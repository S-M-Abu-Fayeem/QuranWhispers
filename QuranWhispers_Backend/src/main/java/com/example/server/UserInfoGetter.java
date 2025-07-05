package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserInfoGetter {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String GET(String email, int token) {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        TokenValidator tokenValidator = new TokenValidator();

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            data.addProperty("email", email);

            if (tokenValidator.VALIDATE(email, token)) {
                PreparedStatement psForInfo = connection.prepareStatement(
                        "SELECT id, username, total_saved_verse, total_received_verse FROM USERS WHERE email = ?");
                psForInfo.setString(1, email);
                ResultSet queryResult = psForInfo.executeQuery();

                if (queryResult.next()) {
                    int idOfUser = queryResult.getInt("id");
                    data.addProperty("username", queryResult.getString("username"));
                    data.addProperty("total_saved_verse", queryResult.getInt("total_saved_verse"));
                    data.addProperty("total_received_verse", queryResult.getInt("total_received_verse"));
                    data.addProperty("status", "Found");

                    // Favorite verses
                    PreparedStatement favouriteVersesPs = connection.prepareStatement(
                            "SELECT mood, ayat, surah FROM FAV_VERSE WHERE user_id = ?");
                    favouriteVersesPs.setInt(1, idOfUser);
                    ResultSet favResult = favouriteVersesPs.executeQuery();
                    JsonArray favArray = new JsonArray();
                    while (favResult.next()) {
                        JsonObject verse = new JsonObject();
                        verse.addProperty("ayat", favResult.getInt("ayat"));
                        verse.addProperty("surah", favResult.getString("surah"));
                        verse.addProperty("mood", favResult.getString("mood"));
                        favArray.add(verse);
                    }
                    data.add("favourite_verses", favArray);

                    // Received verses
                    PreparedStatement recVersePs = connection.prepareStatement(
                            "SELECT R.sender_username, R.timestamp, D.mood, D.ayat, D.surah " +
                                    "FROM REC_VERSE R JOIN REC_VERSE_DETAIL D ON R.id = D.rec_verse_id " +
                                    "WHERE R.user_id = ?");
                    recVersePs.setInt(1, idOfUser);
                    ResultSet recResult = recVersePs.executeQuery();
                    JsonArray recArray = new JsonArray();
                    while (recResult.next()) {
                        JsonObject verse = new JsonObject();
                        verse.addProperty("sender_username", recResult.getString("sender_username"));
                        verse.addProperty("timestamp", recResult.getString("timestamp"));
                        verse.addProperty("mood", recResult.getString("mood"));
                        verse.addProperty("ayat", recResult.getInt("ayat"));
                        verse.addProperty("surah", recResult.getString("surah"));
                        recArray.add(verse);
                    }
                    data.add("received_verses", recArray);

                } else {
                    data.addProperty("status", "User does not exist");
                }

            } else {
                data.addProperty("status", "Invalid token");
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "Server Error");
        }

        return gson.toJson(data);
    }
}
