package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;

import java.io.File;
import java.io.IOException;

public class AdminApproveCardController extends BaseControllerAdmin {
    @FXML Label surahNumField;
    @FXML Label ayahNumField;
    @FXML Label requestorUsernameField;
    @FXML Label reciterNameField;
    String audioPath;
    MediaPlayer mediaPlayer;
    private AdminApproveController parentController;

    public void setupParentController(AdminApproveController controller) {
        this.parentController = controller;
    }

    public void setupAdminApproveInfo(String surahNum, String ayahNum, String requestorUsername, String reciterName) {
        surahNumField.setText(surahNum);
        ayahNumField.setText(ayahNum);
        requestorUsernameField.setText(requestorUsername);
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

                JSONObject response = BackendAPI.fetch("listenpendingrecitation", request);
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

    public void handleApproveBtn(MouseEvent e) throws IOException {
        System.out.println("Approve Btn Pressed");
        playClickSound();
        Task<Void> approveRecitationBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("surah", surahNumField.getText());
                request.put("ayah", ayahNumField.getText());
                request.put("reciter_name", reciterNameField.getText());

                JSONObject response = BackendAPI.fetch("approverecitation", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("RecitationApproveSuccessfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            if (sceneController != null) {
                                AdminApproveController adminApproveController = (AdminApproveController) sceneController.switchTo(GlobalState.ADMIN_APPROVE_FILE);
                                adminApproveController.setupApproveTable();
                            } else {
                                System.err.println("sceneController is null in AdminApproveCardController.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(approveRecitationBackendAPITask).start();
    }


    public void handleDeclineBtn(MouseEvent e) throws IOException {
        System.out.println("Decline Btn Pressed");
        playClickSound();
        Task<Void> declineRecitationBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("surah", surahNumField.getText());
                request.put("ayah", ayahNumField.getText());
                request.put("reciter_name", reciterNameField.getText());

                JSONObject response = BackendAPI.fetch("disapproverecitation", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("RecitationDeclineSuccessfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            if (sceneController != null) {
                                AdminApproveController adminApproveController = (AdminApproveController) sceneController.switchTo(GlobalState.ADMIN_APPROVE_FILE);
                                adminApproveController.setupApproveTable();
                            } else {
                                System.err.println("sceneController is null in AdminApproveCardController.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(declineRecitationBackendAPITask).start();
    }
}
