package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.*;

public class RandomizedSelection {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    private int getUserIdByEmail(Connection conn, String email) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM USERS WHERE email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return -1;
    }

    public String generateMoodBased(String email, int valueOfToken, String emotion) {
        TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        if (tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {

                int userId = getUserIdByEmail(connection, email);
                if (userId == -1) {
                    data.addProperty("status", "404");
                    return gson.toJson(data);
                }

                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM MOOD_VERSES mv " +
                                "WHERE mv.emotion = ? " +
                                "AND NOT EXISTS (SELECT 1 FROM FAV_VERSE fv WHERE fv.user_id = ? AND fv.surah = mv.surah AND fv.ayah = mv.ayah) " +
                                "AND NOT EXISTS (SELECT 1 FROM REC_VERSE_DETAIL rd JOIN REC_VERSE rv ON rd.rec_verse_id = rv.id WHERE rv.user_id = ? AND rd.surah = mv.surah AND rd.ayah = mv.ayah) " +
                                "ORDER BY RAND() LIMIT 1"
                );
                ps.setString(1, emotion);
                ps.setInt(2, userId);
                ps.setInt(3, userId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int ayah = rs.getInt("ayah");
                    String surah = rs.getString("surah");
                    String theme = rs.getString("theme");
                    data.addProperty("theme", theme);
                    data.addProperty("ayah", ayah);
                    data.addProperty("surah", surah);
                    data.addProperty("status", "200");
                    data.addProperty("emotion", emotion);
                } else {
                    data.addProperty("status", "204"); // No content
                    data.addProperty("message", "No new verse found for this emotion.");
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

    public String generateThemeBased(String email, int valueOfToken, String theme) {
        TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        if (tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {

                int userId = getUserIdByEmail(connection, email);
                if (userId == -1) {
                    data.addProperty("status", "404");
                    return gson.toJson(data);
                }

                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM MOOD_VERSES mv " +
                                "WHERE mv.theme = ? " +
                                "AND NOT EXISTS (SELECT 1 FROM FAV_VERSE fv WHERE fv.user_id = ? AND fv.surah = mv.surah AND fv.ayah = mv.ayah) " +
                                "AND NOT EXISTS (SELECT 1 FROM REC_VERSE_DETAIL rd JOIN REC_VERSE rv ON rd.rec_verse_id = rv.id WHERE rv.user_id = ? AND rd.surah = mv.surah AND rd.ayah = mv.ayah) " +
                                "ORDER BY RAND() LIMIT 1"
                );
                ps.setString(1, theme);
                ps.setInt(2, userId);
                ps.setInt(3, userId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int ayah = rs.getInt("ayah");
                    String surah = rs.getString("surah");
                    String emotion = rs.getString("emotion");
                    data.addProperty("theme", theme);
                    data.addProperty("emotion", emotion);
                    data.addProperty("ayah", ayah);
                    data.addProperty("surah", surah);
                    data.addProperty("status", "200");
                } else {
                    data.addProperty("status", "204"); // No content
                    data.addProperty("message", "No new verse found for this theme.");
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
