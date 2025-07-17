package com.example.server;

import com.google.genai.*;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.server.HelloApplication.tokenValidator;

public class APIBasedVerseGenerator {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public synchronized String generate(String email, int valueOfToken, String text) {
        Client client = new Client();
        //TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        if (!tokenValidator.VALIDATE(email, valueOfToken)) {
            data.addProperty("status", "404"); // Unauthorized
            return gson.toJson(data);
        }

        String prompt = "Read the following text carefully:\n\n" +
                text + "\n\n" +
                "Then, perform the following tasks with a Quranic and religious perspective:\n" +
                "1. Identify the most appropriate **theme** for this text.\n" +
                "2. Identify the most fitting **emotion** conveyed by or relevant to the text.\n" +
                "3. If no suitable theme is found, set \"theme\" to \"null\".\n" +
                "4. If no suitable emotion is found, set \"emotion\" to \"null\".\n" +
                "5. Note: Both \"theme\" and \"emotion\" cannot be \"null\" at the same time — at least one must be meaningful.\n" +
                "6. Choose a related ayah (number) from the Quran that aligns with the theme/emotion.\n" +
                "7. Do not select an overly long ayah — it must be **150 characters or fewer**.\n\n" +
                "### Response Format\n" +
                "- Respond only with a JSON object in the following format (without any explanation or commentary):\n\n" +
                "{ \"theme\": \"hope\", \"emotion\": \"gratitude\", \"surah\": \"2\", \"ayah\": \"3\" }";

        try {
            GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);
            Pattern pattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response.text());

            if (!matcher.find()) {
                data.addProperty("status", "500"); // No valid JSON found
                return gson.toJson(data);
            }

            String json = matcher.group(0).trim();
            JsonObject jsonFile = gson.fromJson(json, JsonObject.class);
            String theme = jsonFile.get("theme").getAsString();
            String emotion = jsonFile.get("emotion").getAsString();
            String surah = jsonFile.get("surah").getAsString();
            String ayah = jsonFile.get("ayah").getAsString();

            data.addProperty("theme", theme);
            data.addProperty("emotion", emotion);
            data.addProperty("surah", surah);
            data.addProperty("ayah", ayah);

            try (Connection connection = DriverManager.getConnection(DB_URL)) {
                PreparedStatement qsCheck = connection.prepareStatement(
                        "SELECT COUNT(*) FROM MOOD_VERSES WHERE emotion = ? AND theme = ? AND surah = ? AND ayah = ?"
                );
                qsCheck.setString(1, emotion);
                qsCheck.setString(2, theme);
                qsCheck.setString(3, surah);
                qsCheck.setString(4, ayah);
                ResultSet rs = qsCheck.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    PreparedStatement qs = connection.prepareStatement(
                            "INSERT INTO MOOD_VERSES (emotion, theme, ayah, surah) VALUES (?, ?, ?, ?)"
                    );
                    qs.setString(1, emotion);
                    qs.setString(2, theme);
                    qs.setInt(3, Integer.parseInt(ayah));
                    qs.setString(4, surah);

                    int inserted = qs.executeUpdate();
                    data.addProperty("status", inserted > 0 ? "200" : "404");
                } else {
                    data.addProperty("status", "200"); // Conflict: already exists
                }
            } catch (Exception e) {
                e.printStackTrace();
                data.addProperty("status", "500");
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "500");
        }

        return gson.toJson(data);
    }
}
