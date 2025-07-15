package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

public class AdminRecitationViewCardController extends BaseControllerAdmin {
    @FXML Label surahField;
    @FXML Label ayahField;
    @FXML Label reciterNameField;
    String audioPath;
    MediaPlayer mediaPlayer;
    private AdminRecitationViewController parentController;

    public void setupParentController(AdminRecitationViewController controller) {
        this.parentController = controller;
    }

    public void setupAdminRecitationViewInfo(String surah, String ayah, String reciterName, String path) {
        surahField.setText(surah);
        ayahField.setText(ayah);
        reciterNameField.setText(reciterName);
        audioPath = path;
        System.out.println("Setup setup");
    }

    public void handleDeleteBtn(MouseEvent e) throws IOException {
        System.out.println("Delete Btn Pressed");
        playClickSound();
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
}
