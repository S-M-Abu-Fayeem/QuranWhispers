package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class AddDua {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String SET_DUA(String email, int valueOfToken, String title, String arabicBody, String englishBody) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                System.out.println("Validation done");
                PreparedStatement qs = connection.prepareStatement("INSERT INTO DUA (title , body_english, body_arabic) VALUES (?, ?, ?)");
                qs.setString(1, title);
                qs.setString(2, englishBody);
                qs.setString(3, arabicBody);
                int check = qs.executeUpdate();
                if(check > 0) {
                    data.addProperty("status", "200");
                }
                else{
                    data.addProperty("status", "404");
                }
            }
            else{
                data.addProperty("status", "401");
            }
        }
        catch(Exception e){
            data.addProperty("status", "500");
        }
        return gson.toJson(data);
    }
    public String SET_THEME_MOOD(String email, int valueOfToken, String theme , String emotion, int ayah, String surah) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                PreparedStatement qs = connection.prepareStatement(
                        "INSERT INTO MOOD_VERSES (emotion, theme, ayah, surah) VALUES (?, ?, ?, ?)"
                );
                qs.setString(1, emotion);
                qs.setString(2, theme);
                qs.setInt(3, ayah);
                qs.setString(4, surah);

                int check = qs.executeUpdate();

                if(check > 0) {
                    data.addProperty("status", "200");
                }
                else{
                    data.addProperty("status", "404");
                }
            }
            else{
                data.addProperty("status", "401");
            }
        }
        catch(Exception e){
            data.addProperty("status", "500");
        }
        return gson.toJson(data);
    }
}
