package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;
import util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class GlobalShareController extends BaseController{
    @FXML ImageView profileNavImageView;
    @FXML ImageView notificationNavImageView;
    @FXML Label profileNavlink;

    @FXML TextField receiverUsername;
    String emotionName;
    String themeName;
    int surahNum;
    int ayahNum;
    String parent;

    public void setupVerseDetails(int surahNum, int ayahNum, String emotionName, String themeName) {
        this.surahNum = surahNum;
        this.ayahNum = ayahNum;
        this.emotionName = emotionName;
        this.themeName = themeName;
    }

    public void setupParent(String parent) {
        this.parent = parent;

        if (Objects.equals(parent, GlobalState.PROFILE_FILE)) {
            profileNavlink.setTextFill(Color.BLACK);
            URL profileIconURL = getClass().getResource("/images/black_heart.png");
            if (profileIconURL != null) {
                profileNavImageView.setImage(new Image(profileIconURL.toExternalForm()));
            }
            URL notificationIconURL = getClass().getResource("/images/notifications.png");
            if (notificationIconURL != null) {
                notificationNavImageView.setImage(new Image(notificationIconURL.toExternalForm()));
            }
        } else if (Objects.equals(parent, GlobalState.NOTIFICATION_FILE)) {
            profileNavlink.setTextFill(Color.WHITE);
            URL profileIconURL = getClass().getResource("/images/heart.png");
            if (profileIconURL != null) {

                profileNavImageView.setImage(new Image(profileIconURL.toExternalForm()));
            }

            URL notificationIconURL = getClass().getResource("/images/notifications_active.png");
            if (notificationIconURL != null) {
                notificationNavImageView.setImage(new Image(notificationIconURL.toExternalForm()));
            }
        }
    }

    public void handleCloseBtn(MouseEvent e) throws IOException {
        System.out.println("Close Button Pressed");
        playClickSound();
        sceneController.switchTo(parent);
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
                            sceneController.switchTo(parent);
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
