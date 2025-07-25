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
import util.GlobalState;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AdminRecitationViewCardController extends BaseControllerAdmin {
    @FXML Label surahNumField;
    @FXML Label ayahNumField;
    @FXML Label reciterNameField;
    String audioPath;
    MediaPlayer mediaPlayer;
    private AdminRecitationViewController parentController;
    @FXML ImageView audioImageView;

    public void setupParentController(AdminRecitationViewController controller) {
        this.parentController = controller;
    }

    public void setupAdminRecitationViewInfo(String surahNum, String ayahNum, String reciterName) {
        surahNumField.setText(surahNum);
        ayahNumField.setText(ayahNum);
        reciterNameField.setText(reciterName);
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
                request.put("surah", surahNumField.getText());
                request.put("ayah", ayahNumField.getText());
                request.put("reciter_name", reciterNameField.getText());

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


    public void handleDeleteBtn(MouseEvent e) throws IOException {
        System.out.println("Decline Btn Pressed");
        playClickSound();
        Task<Void> deleteRecitationBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("surah", surahNumField.getText());
                request.put("ayah", ayahNumField.getText());
                request.put("reciter_name", reciterNameField.getText());

                JSONObject response = BackendAPI.fetch("deleteapprovedrecitation", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Recitation Deleted Successfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            if (sceneController != null) {
                                AdminRecitationViewController adminRecitationViewController = (AdminRecitationViewController) sceneController.switchTo(GlobalState.ADMIN_RECITATION_VIEW_FILE);
                                adminRecitationViewController.setupRecitationViewTable();
                            } else {
                                System.err.println("sceneController is null in AdminRecitationViewCardController.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(deleteRecitationBackendAPITask).start();
    }
}
