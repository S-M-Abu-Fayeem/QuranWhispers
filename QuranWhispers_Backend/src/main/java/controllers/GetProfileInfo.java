package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;

import static com.example.server.HelloApplication.tokenValidator;

public class GetProfileInfo {
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


            PreparedStatement psInfo = connection.prepareStatement(
                    "SELECT id, username, total_saved_verse, total_received_verse FROM USERS WHERE email = ?");
            psInfo.setString(1, email);
            ResultSet userRs = psInfo.executeQuery();

            if (!userRs.next()) {
                data.addProperty("status", "404");
                return gson.toJson(data);
            }

            int userId = userRs.getInt("id");
            data.addProperty("username", userRs.getString("username"));
            data.addProperty("total_saved_verse", userRs.getInt("total_saved_verse"));
            data.addProperty("total_received_verse", userRs.getInt("total_received_verse"));


            PreparedStatement psFav = connection.prepareStatement(
                    "SELECT emotion, theme,  ayah, surah, timestamp FROM FAV_VERSE WHERE user_id = ? ORDER BY timestamp DESC"
            );
            psFav.setInt(1, userId);
            ResultSet favRs = psFav.executeQuery();

            JsonArray favArray = new JsonArray();
            while (favRs.next()) {
                JsonObject verse = new JsonObject();
                verse.addProperty("emotion", favRs.getString("emotion"));
                verse.addProperty("theme", favRs.getString("theme"));
                verse.addProperty("ayah", favRs.getInt("ayah"));
                verse.addProperty("surah", favRs.getString("surah"));
                favArray.add(verse);
            }
            data.add("favourite_verses", favArray);
            data.addProperty("status", "200");

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "500");
        }

        return gson.toJson(data);
    }
}
