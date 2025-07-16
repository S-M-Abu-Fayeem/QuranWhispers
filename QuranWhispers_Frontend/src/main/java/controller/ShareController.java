package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;

import java.io.File;
import java.io.IOException;

public class ShareController extends SearchController{

    @FXML TextField receiverUsername;
    @FXML ImageView versePosterView;
    @FXML Label categoryField;
    @FXML Label duaTitle;
    @FXML Text duaArabicBody;
    @FXML Text duaEnglishBody;
    String emotionName;
    String themeName;
    int surahNum;
    int ayahNum;


    public void setupVerseDetails(int surahNum, int ayahNum, String emotionName, String themeName) {
        this.surahNum = surahNum;
        this.ayahNum = ayahNum;
        this.emotionName = emotionName;
        this.themeName = themeName;
    }

    public void setupDuaDetails(String title, String arabicBody, String englishBody) {
        this.duaTitle.setText(title);
        this.duaArabicBody.setText(arabicBody);
        this.duaEnglishBody.setText(englishBody);
    }

    public void setupPoster(String posterPath, String categoryName) {
        File posterFile = new File(posterPath);
        if (posterFile.exists()) {
            Image poster = new Image(posterFile.toURI().toString());
            versePosterView.setImage(poster);
        } else {
            System.out.println("Poster file not found: " + posterFile.getAbsolutePath());
        }
        categoryField.setText(categoryName);
    }


    public void handleCloseBtn(MouseEvent e) throws IOException {
        System.out.println("Close Button Pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.SEARCH_FILE);
    }

    public void handleSendBtn(MouseEvent e) throws IOException {
        System.out.println("Send Button Pressed");
        playClickSound();
        Task<Void> SendBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("emotion", emotionName);
                request.put("theme", themeName);
                request.put("ayah", String.valueOf(ayahNum));
                request.put("surah", String.valueOf(surahNum));
                request.put("receiver_username", receiverUsername.getText());
                for (String key : request.keySet()) {
                    System.out.println("Key: " + key + " | Value: " + request.get(key));
                }

                JSONObject response = BackendAPI.fetch("sendtofriend", request);
                System.out.println(response.getString("status"));
                if (response.getString("status").equals("200")) {
                    System.out.println("Fetch successful");
                    Platform.runLater(() -> {
                        try {
                            sceneController.switchTo(GlobalState.SEARCH_FILE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    System.out.println("Fetch failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(SendBackendAPITask).start();
    }
}
