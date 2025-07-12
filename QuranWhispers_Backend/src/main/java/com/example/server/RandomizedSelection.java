package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RandomizedSelection {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public String generateMoodBased(String email,int valueOfToken, String emotion){
        TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        if(tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM MOOD_VERSES WHERE emotion = ? ORDER BY RAND() LIMIT 1"
                );
                ps.setString(1, emotion);
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
                }
                else{
                    data.addProperty("status", "500");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                data.addProperty("status", "500");
            }
        }
        else{
            data.addProperty("status", "401");
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
                    int ayah = rs.getInt("ayah");
                    String surah = rs.getString("surah");
                    String emotion = rs.getString("emotion");
                    //String theme = rs.getString("theme");
                    data.addProperty("theme", theme);
                    data.addProperty("emotion", emotion);
                    data.addProperty("ayah", ayah);
                    data.addProperty("surah", surah);
                    data.addProperty("status", "200");
                }
                else{
                    data.addProperty("status", "500");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                data.addProperty("status", "500");
            }
        }
        else{
            data.addProperty("status", "401");
        }
        return gson.toJson(data);
    }
}
