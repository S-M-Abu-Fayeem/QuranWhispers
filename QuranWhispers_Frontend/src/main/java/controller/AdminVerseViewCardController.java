package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;
import util.SessionManager;

import java.io.IOException;

public class AdminVerseViewCardController extends BaseControllerAdmin {
    @FXML Label surahField;
    @FXML Label ayahField;
    @FXML Label emotionField;
    @FXML Label themeField;

    public void setupAdminVerseViewInfo(String surah, String ayah, String emotion, String theme) {
        surahField.setText(surah);
        ayahField.setText(ayah);
        emotionField.setText(emotion);
        themeField.setText(theme);
    }

    public void handleDeleteBtn(MouseEvent e) throws IOException {
        System.out.println("Delete Btn Pressed");
        Task<Void> deleteVerseBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("emotion", emotionField.getText());
                request.put("theme", themeField.getText());
                request.put("surah", surahField.getText());
                request.put("ayah", ayahField.getText());

                JSONObject response = BackendAPI.fetch("deleteverse", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Verse Deleted successfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            if (sceneController != null) {
                                AdminVerseViewController adminVerseViewController = (AdminVerseViewController) sceneController.switchTo(GlobalState.ADMIN_VERSE_VIEW_FILE);
                                adminVerseViewController.setupVerseViewTable();
                            } else {
                                System.err.println("sceneController is null in AdminVerseViewCardController.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(deleteVerseBackendAPITask).start();
        playClickSound();
    }
}
