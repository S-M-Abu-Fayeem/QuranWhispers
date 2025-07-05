package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.*;

public class GeneratingDuaOfTheDay {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    private static final long EXPIRY_DURATION_MS = 30_000; // 30 seconds

    public String getDua() {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        try (Connection connection = DriverManager.getConnection(DB_URL)) {

            PreparedStatement check = connection.prepareStatement("SELECT DUA.id, title, body_english, body_bangla, body_arabic, timestamp FROM DUA_CACHE JOIN DUA ON DUA_CACHE.dua_id = DUA.id");
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                long timestamp = rs.getLong("timestamp");
                long now = System.currentTimeMillis();

                if (now - timestamp < EXPIRY_DURATION_MS) {
                    data.addProperty("title", rs.getString("title"));
                    data.addProperty("english", rs.getString("body_english"));
                    data.addProperty("bangla", rs.getString("body_bangla"));
                    data.addProperty("arabic", rs.getString("body_arabic"));
                    data.addProperty("status", "cached");
                    return gson.toJson(data);
                }
            }

            connection.prepareStatement("DELETE FROM DUA_CACHE").executeUpdate();

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM DUA ORDER BY RAND() LIMIT 1");
            ResultSet randomDua = ps.executeQuery();
            if (randomDua.next()) {
                int duaId = randomDua.getInt("id");
                PreparedStatement insert = connection.prepareStatement("INSERT INTO DUA_CACHE (dua_id, timestamp) VALUES (?, ?)");
                insert.setInt(1, duaId);
                insert.setLong(2, System.currentTimeMillis());
                insert.executeUpdate();
                data.addProperty("title", randomDua.getString("title"));
                data.addProperty("english", randomDua.getString("body_english"));
                data.addProperty("bangla", randomDua.getString("body_bangla"));
                data.addProperty("arabic", randomDua.getString("body_arabic"));
                data.addProperty("status", "new");
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "failed");
        }

        return gson.toJson(data);
    }

}
