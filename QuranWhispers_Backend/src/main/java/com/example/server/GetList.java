package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;

import static com.example.server.HelloApplication.tokenValidator;

public class GetList {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public synchronized String GET(String email, int token) {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        //TokenValidator tokenValidator = new TokenValidator();

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            data.addProperty("email", email);

            if (!tokenValidator.VALIDATE(email, token)) {
                data.addProperty("status", "404");
                return gson.toJson(data);
            }

            JsonArray emotions = new JsonArray();
            JsonArray themes = new JsonArray();
            PreparedStatement psMood = connection.prepareStatement(
                    "SELECT DISTINCT emotion, theme FROM MOOD_VERSES");
            ResultSet moodRs = psMood.executeQuery();
            while (moodRs.next()) {
                String check = moodRs.getString("emotion");
                if(!check.equalsIgnoreCase("null")) emotions.add(check);
                check = moodRs.getString("theme");
                if(!check.equalsIgnoreCase("null")) themes.add(check);
            }
            data.add("emotions", emotions);
            data.add("themes", themes);

            data.addProperty("status", "200");

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "500");
        }

        return gson.toJson(data);
    }
}
