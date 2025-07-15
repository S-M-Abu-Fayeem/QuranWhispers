package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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

    public void setupAdminApproveInfo(int surahNum, int ayahNum, String requestorUsername, String reciterName, String path) {
        surahNumField.setText(String.valueOf(surahNum));
        ayahNumField.setText(String.valueOf(ayahNum));
        requestorUsernameField.setText(requestorUsername);
        reciterNameField.setText(reciterName);
        audioPath = path;
    }

    public void handleReciteBtn(MouseEvent e) throws IOException {
        System.out.println("Recite Btn Pressed");
        playClickSound();

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
    }
    public void handleDeclineBtn(MouseEvent e) throws IOException {
        System.out.println("Decline Btn Pressed");
        playClickSound();
    }
}
