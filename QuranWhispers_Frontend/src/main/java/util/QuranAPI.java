package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import util.GlobalState;

public class QuranAPI {

    // Method to generate the verse JSON object with translations
    public static JSONObject generateVerse(int surahNum, int ayahNum) {
        JSONObject result = new JSONObject();
        result.put("surahNum", surahNum);
        result.put("ayahNum", ayahNum);

        // Fetch translations for Bengali, English, and Arabic
        String bengaliData = fetchDataFromAPI(GlobalState.QURAN_BENGALI_EDITION_NAME, surahNum, ayahNum);
        String englishData = fetchDataFromAPI(GlobalState.QURAN_ENGLISH_EDITION_NAME, surahNum, ayahNum);
        String arabicData = fetchDataFromAPI(GlobalState.QURAN_ARABIC_EDITION_NAME, surahNum, ayahNum);

        // Process and add the data to the JSON object
        if (bengaliData != null) {
            populateJSON(bengaliData, "Bengali", result);
        } else {
            result.put("bengaliTranslationText", "Bengali translation not available.");
        }

        if (englishData != null) {
            populateJSON(englishData, "English", result);
        } else {
            result.put("englishTranslationText", "English translation not available.");
        }

        if (arabicData != null) {
            populateJSON(arabicData, "Arabic", result);
        } else {
            result.put("arabicTranslationText", "Arabic translation not available.");
        }

        return result; // Return the complete JSON object
    }

    // Method to fetch data from the API
    public static String fetchDataFromAPI(String editionName, int surahNumber, int ayahNumber) {
        try {
            String urlString = GlobalState.QURAN_API_URL + editionName + "/" + surahNumber + "/" + ayahNumber + ".json";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                System.out.println("Failed to fetch data, status code: " + status);
            }
        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
        return null;
    }

    // Method to process the response and populate the JSON object
    public static void populateJSON(String data, String language, JSONObject result) {
        try {
            JSONObject jsonResponse = new JSONObject(data);
            String verseText = jsonResponse.getString("text");

            // Update the JSON object with the translation text
            if (language.equals("Bengali")) {
                result.put("bengaliTranslationText", verseText);
            } else if (language.equals("English")) {
                result.put("englishTranslationText", verseText);
            } else if (language.equals("Arabic")) {
                result.put("arabicTranslationText", verseText);
            }

        } catch (Exception e) {
            System.out.println("Error processing response for " + language + ": " + e.getMessage());
            result.put(language.toLowerCase() + "TranslationText", language + " translation not available.");
        }
    }
}
