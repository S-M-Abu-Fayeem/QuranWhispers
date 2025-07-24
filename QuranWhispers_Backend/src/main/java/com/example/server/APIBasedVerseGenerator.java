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
        while(true) {
            String prompt = "Read the following text carefully:\n\n" +
                    text + "\n\n" +
                    "Then, perform the following tasks with a Quranic and religious perspective:\n" +
                    "1. Identify the most appropriate **theme** for this text.\n" +
                    "2. Identify the most fitting **emotion** conveyed by or relevant to the text.\n" +
                    "3. If no suitable theme is found, set \"theme\" to \"null\".\n" +
                    "4. If no suitable emotion is found, set \"emotion\" to \"null\".\n" +
                    "5. Note: Both \"theme\" and \"emotion\" cannot be \"null\" at the same time — at least one must be meaningful.\n" +
                    "6. Choose a related ayah (number) from the Quran that aligns with the theme/emotion.\n" +
                    "7. Do not select an overly long ayah — it must be **150 characters or fewer**  for all - arabic , bangla , english.\n" +
                    "8. Do not suggest multiple consecutive ayah\n" +
                    "9. Do not suggest part of a single ayah\n" +
                    "10.The ayah field must be a single integer\n" +
                    "11. Emotion and theme must be - **20 characters or fewer**\n" +
                    "12.**Strictly follow the character rules previously mentioined**\n" +
                    "13. Here are some examples of emotions : ** Afraid\", \"Depressed\", \"Feeling Lonely\", \"Last Hope\", \"Need Courage\", \"Seeking Peace\", \"Need Direction\", \"Happy\", \"Sad\", \"Angry\", \"Grateful\", \"Hopeful\", \"Confused\", \"Stressed\", \"Anxious\", \"Lost\", \"Insecure\", \"Overwhelmed\", \"Disappointed\", \"Jealous\", \"Guilty\"**\n" +
                    "14. Here are some examples of themes : **Faith and Belief (Iman)\", \"Guidance (Hidayah)\", \"Worship (Ibadah)\", \"Patience (Sabr)\", \"Gratitude (Shukr)\", \"Justice (Adl)\", \"The Afterlife (Akhirah)\", \"Repentance (Tawbah)\", \"Family and Relationships\", \"Community and Society\", \"Knowledge and Wisdom\", \"Charity and Generosity (Sadaqah)\", \"Forgiveness (Maghfirah)\", \"Love and Compassion (Rahmah)\", \"Unity (Wahdah)\", \"Peace (Salam)\", \"Trust in Allah (Tawakkul)\", \"Hope (Raja')\", \"Courage (Shaja'ah)\", \"Humility (Tawadu')\", \"Self-Reflection (Muhasabah)\"** \n" +
                    "15. Again remember arabic text donot contain more than 150 characters you always forget this\n\n" +
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
                String prompt2 = "Max characters the surah " + surah + " ayah " + ayah + " have in english , arabic , bangla" + "\n. Just return the number max number\n" + "Do not add any comment or anything just the number\n";
                GenerateContentResponse response2 = client.models.generateContent("gemini-2.5-flash", prompt2, null);
                String regex = "-?\\d+";

                Pattern pattern2 = Pattern.compile(regex);
                Matcher matcher2 = pattern2.matcher(response2.text());
                if (matcher2.find()) {
                    int number = Integer.parseInt(matcher2.group());
                    if(number > 150) {
                        continue;
                    }
                } else {
                    continue;
                }

                data.addProperty("theme", theme.toLowerCase());
                data.addProperty("emotion", emotion.toLowerCase());
                data.addProperty("surah", surah);
                data.addProperty("ayah", ayah);

                try (Connection connection = DriverManager.getConnection(DB_URL)) {
                    PreparedStatement qsCheck = connection.prepareStatement(
                            "SELECT COUNT(*) FROM MOOD_VERSES WHERE emotion = ? AND theme = ? AND surah = ? AND ayah = ?"
                    );
                    qsCheck.setString(1, emotion.toLowerCase());
                    qsCheck.setString(2, theme.toLowerCase());
                    qsCheck.setString(3, surah);
                    qsCheck.setString(4, ayah);
                    ResultSet rs = qsCheck.executeQuery();

                    if (rs.next() && rs.getInt(1) == 0) {
                        PreparedStatement qs = connection.prepareStatement(
                                "INSERT INTO MOOD_VERSES (emotion, theme, ayah, surah) VALUES (?, ?, ?, ?)"
                        );
                        qs.setString(1, emotion.toLowerCase());
                        qs.setString(2, theme.toLowerCase());
                        qs.setInt(3, Integer.parseInt(ayah));
                        qs.setString(4, surah);

                        int inserted = qs.executeUpdate();
                        data.addProperty("status", inserted > 0 ? "200" : "404");
                        break;
                    } else {
                        data.addProperty("status", "200");
                        break;// Conflict: already exists
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    data.addProperty("status", "500");
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                data.addProperty("status", "500");
                break;
            }
        }

        return gson.toJson(data);
    }
}
