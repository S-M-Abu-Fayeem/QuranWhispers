package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.json.JSONObject;
import util.BackendAPI;

import java.io.File;

public class RecitationCardController extends BaseController{
    @FXML Label recitersName;
    String audioPath;
    MediaPlayer mediaPlayer;
    private RecitationController parentController;
    String surahNum;
    String ayahNum;

    public void setParentController(RecitationController controller) {
        this.parentController = controller;
    }


    public void setupRecitationCard(String surahNum, String ayahNum, String name) {
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
                            listenRecitationAudio();
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

    public void listenRecitationAudio() {
        if (mediaPlayer == null) {
            if (parentController != null) {
                parentController.stopAllOtherCards(this);
            }

            Media media = new Media(new File(audioPath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } else {
            stopAudio();
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null;
    }
}
