package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import util.BackendAPI;
import util.SessionManager;

import java.io.IOException;

public class AdminInsertController extends BaseControllerAdmin {
    @FXML TextField duaTitleField;
    @FXML TextField duaArabicBodyField;
    @FXML TextField duaEnglishBodyField;
    @FXML TextField surahNumField;
    @FXML TextField ayahNumField;
    @FXML TextField emotionField;
    @FXML TextField themeField;

    public void handleDuaSubmitBtn(MouseEvent e) throws IOException {
        System.out.println("Dua Submit Button Pressed");
        playClickSound();
        Task<Void> addDuaBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("title", duaTitleField.getText());
                request.put("arabic_body", duaArabicBodyField.getText());
                request.put("english_body", duaEnglishBodyField.getText());

                JSONObject response = BackendAPI.fetch("adddua", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Dua Added successfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            duaTitleField.clear();
                            duaArabicBodyField.clear();
                            duaEnglishBodyField.clear();
                            alertGenerator("Action successful", "ACTION: ADD DUA", response.getString("status_message"), "confirmation", "/images/confrim.png");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(addDuaBackendAPITask).start();
    }

    public void handleVerseSubmitBtn(MouseEvent e) throws IOException {
        System.out.println("Verse Submit Button Pressed");
        playClickSound();
        Task<Void> addVerseBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());
                request.put("surah", surahNumField.getText());
                request.put("ayah", ayahNumField.getText());
                request.put("emotion", emotionField.getText());
                request.put("theme", themeField.getText());

                JSONObject response = BackendAPI.fetch("addverse", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Verse Added successfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            surahNumField.clear();
                            ayahNumField.clear();
                            emotionField.clear();
                            themeField.clear();
                            alertGenerator("Action successful", "ACTION: ADD VERSE", response.getString("status_message"), "confirmation", "/images/confrim.png");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else if (response.getString("status").equals("404")) {
                    Platform.runLater(() -> {
                        alertGenerator("Add failed", "INVALID ACTION", response.getString("status_message"), "error", "/images/denied.png");
                    });
                } else if (response.getString("status").equals("500")) {
                    Platform.runLater(() -> {
                        alertGenerator("Add failed", "SERVER ERROR", response.getString("status_message"), "error", "/images/denied.png");
                    });
                } else {
                    Platform.runLater(() -> {
                        alertGenerator("Error", "UNKNOWN PROBLEM", "Something went wrong :(", "error", "/images/denied.png");
                    });
                }
                return null;
            }
        };
        new Thread(addVerseBackendAPITask).start();
    }
}
