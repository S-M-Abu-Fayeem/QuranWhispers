package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.json.JSONObject;
import util.BackendAPI;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class GlobalRecitationCardController extends BaseController{
    @FXML Label recitersName;
    String audioPath;
    MediaPlayer mediaPlayer;
    private GlobalRecitationController parentController;
    String surahNum;
    String ayahNum;
    @FXML ImageView audioImageView;

    public void setParentController(GlobalRecitationController controller) {
        this.parentController = controller;
    }
    

    public void setupGlobalRecitationCard(String surahNum, String ayahNum, String name) {
        this.surahNum = surahNum;
        this.ayahNum = ayahNum;
        recitersName.setText(name);
    }

    public void handleReciteBtn(MouseEvent e) throws IOException{
        if (mediaPlayer != null) {
            stopAudio();
            return;
        }
        System.out.println("Recite Btn Pressed");
        playClickSound();
        Task<Void> requestAudioAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("surah", surahNum);
                request.put("ayah", ayahNum);
                request.put("reciter_name", recitersName.getText());

                JSONObject response = BackendAPI.fetch("listenapprovedrecitation", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Received notification data successfully" + response.toString());
                    Platform.runLater(() -> {
                        try {
                            audioPath = response.getString("audio_path");
                            listenrecitationAudio();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(requestAudioAPITask).start();
    }

    public void listenrecitationAudio() {
        if (mediaPlayer == null) {
            if (parentController != null) {
                parentController.stopAllOtherCards(this);
            }

            Media media = new Media(new File(audioPath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            URL pauseIconURL = getClass().getResource("/images/pause.png");
            if (pauseIconURL != null) {
                audioImageView.setImage(new Image(pauseIconURL.toExternalForm()));
            }
        } else {
            stopAudio();
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;

            URL playIconURL = getClass().getResource("/images/play.png");
            if (playIconURL != null) {
                audioImageView.setImage(new Image(playIconURL.toExternalForm()));
            }
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null;
    }
}
