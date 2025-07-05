package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RandomizedSelection {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public String generateMoodBased(String email,int valueOfToken, String mood){
        TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        if(tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM MOOD_VERSES WHERE mood = ? ORDER BY RAND() LIMIT 1"
                );
                ps.setString(1, mood);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int ayat = rs.getInt("ayat");
                    String surah = rs.getString("surah");
                    //String theme = rs.getString("theme");
                    data.addProperty("ayat", ayat);
                    data.addProperty("surah", surah);
                }
                else{
                    data.addProperty("status", "failed");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                data.addProperty("status", "failed");
            }
        }
        else{
            data.addProperty("status", "failed");
        }
        return gson.toJson(data);
    }
    public String generateThemeBased(String email,int valueOfToken, String theme){
        TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        if(tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM MOOD_VERSES WHERE theme = ? ORDER BY RAND() LIMIT 1"
                );
                ps.setString(1, theme);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int ayat = rs.getInt("ayat");
                    String surah = rs.getString("surah");
                    //String theme = rs.getString("theme");
                    data.addProperty("ayat", ayat);
                    data.addProperty("surah", surah);
                }
                else{
                    data.addProperty("status", "failed");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                data.addProperty("status", "failed");
            }
        }
        else{
            data.addProperty("status", "failed");
        }
        return gson.toJson(data);
    }
}
