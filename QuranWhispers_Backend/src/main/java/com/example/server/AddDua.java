package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class AddDua {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String SET_DUA(String email, int valueOfToken, String title, String arabicBody, String englishBody, String banglaBody) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                System.out.println("Validation done");
                PreparedStatement qs = connection.prepareStatement("INSERT INTO DUA (title , body_english, body_bangla, body_arabic) VALUES (?, ?, ?, ?)");
                qs.setString(1, title);
                qs.setString(2, englishBody);
                qs.setString(3, banglaBody);
                qs.setString(4, arabicBody);
                int check = qs.executeUpdate();
                if(check > 0) {
                    data.addProperty("status", "success");
                }
                else{
                    data.addProperty("status", "failed");
                }
            }
            else{
                data.addProperty("status", "failed");
            }
        }
        catch(Exception e){
            data.addProperty("status", "failed");
        }
        return gson.toJson(data);
    }
    public String SET_THEME_MOOD(String email, int valueOfToken, String theme , String mood, int ayat, String surah) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                PreparedStatement qs = connection.prepareStatement(
                        "INSERT INTO MOOD_VERSES (mood, ayat, surah, theme) VALUES (?, ?, ?, ?)"
                );
                qs.setString(1, mood);
                qs.setInt(2, ayat);
                qs.setString(3, surah);
                qs.setString(4, theme);
                int check = qs.executeUpdate();

                if(check > 0) {
                    data.addProperty("status", "success");
                }
                else{
                    data.addProperty("status", "failed");
                }
            }
            else{
                data.addProperty("status", "failed");
            }
        }
        catch(Exception e){
            data.addProperty("status", "failed");
        }
        return gson.toJson(data);
    }
}
