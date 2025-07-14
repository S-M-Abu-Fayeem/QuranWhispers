package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;

public class GetRecieveInfo {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String GET(String email, int token) {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        TokenValidator tokenValidator = new TokenValidator();

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            data.addProperty("email", email);

            if (!tokenValidator.VALIDATE(email, token)) {
                data.addProperty("status", "404");
                return gson.toJson(data);
            }


            PreparedStatement psInfo = connection.prepareStatement(
                    "SELECT id, username, total_saved_verse, total_received_verse FROM USERS WHERE email = ?");
            psInfo.setString(1, email);
            ResultSet userRs = psInfo.executeQuery();

            if (!userRs.next()) {
                data.addProperty("status", "404");
                return gson.toJson(data);
            }

            int userId = userRs.getInt("id");
            PreparedStatement psRec = connection.prepareStatement(
                    "SELECT R.sender_username, R.timestamp, D.emotion, D.theme, D.ayah, D.surah " +
                            "FROM REC_VERSE R JOIN REC_VERSE_DETAIL D ON R.id = D.rec_verse_id " +
                            "WHERE R.user_id = ? " +
                            "ORDER BY R.timestamp DESC");
            psRec.setInt(1, userId);
            ResultSet recRs = psRec.executeQuery();

            JsonArray recArray = new JsonArray();
            while (recRs.next()) {
                JsonObject verse = new JsonObject();
                verse.addProperty("sender_username", recRs.getString("sender_username"));
                verse.addProperty("timestamp", recRs.getString("timestamp"));
                verse.addProperty("emotion", recRs.getString("emotion"));
                verse.addProperty("theme", recRs.getString("theme"));
                verse.addProperty("ayah", recRs.getInt("ayah"));
                verse.addProperty("surah", recRs.getString("surah"));
                recArray.add(verse);
            }
            data.add("received_verses", recArray);


            /*JsonArray emotions = new JsonArray();
            JsonArray themes = new JsonArray();
            PreparedStatement psMood = connection.prepareStatement(
                    "SELECT DISTINCT emotion, theme FROM MOOD_VERSES");
            ResultSet moodRs = psMood.executeQuery();
            while (moodRs.next()) {
                emotions.add(moodRs.getString("emotion"));
                themes.add(moodRs.getString("theme"));
            }
            data.add("emotions", emotions);
            data.add("themes", themes);*/

            data.addProperty("status", "200");

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "500");
        }

        return gson.toJson(data);
    }
}
