package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.server.HelloApplication.tokenValidator;

public class RemoveVerse {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public synchronized String DELETE(String email, int valueOfToken, String emotion, String theme, int ayah, String surah) {
        //TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        if (tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                PreparedStatement getUserId = connection.prepareStatement("SELECT id FROM USERS WHERE email = ?");
                getUserId.setString(1, email);
                ResultSet resultSet = getUserId.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");

                    PreparedStatement deleteStmt = connection.prepareStatement(
                            "DELETE FROM FAV_VERSE WHERE user_id = ? AND emotion = ? AND theme = ? AND ayah = ? AND surah = ?"
                    );
                    deleteStmt.setInt(1, userId);
                    deleteStmt.setString(2, emotion);
                    deleteStmt.setString(3, theme);
                    deleteStmt.setInt(4, ayah);
                    deleteStmt.setString(5, surah);

                    int deletedRows = deleteStmt.executeUpdate();

                    if (deletedRows > 0) {
                        PreparedStatement updateSavedVerse = connection.prepareStatement(
                                "UPDATE USERS SET total_saved_verse = total_saved_verse - 1 WHERE id = ? AND total_saved_verse > 0"
                        );
                        updateSavedVerse.setInt(1, userId);
                        updateSavedVerse.executeUpdate();

                        data.addProperty("status", "200");
                    } else {
                        data.addProperty("status", "404");
                    }
                } else {
                    data.addProperty("status", "404");
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
