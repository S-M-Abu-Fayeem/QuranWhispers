package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class RecitationCardController extends BaseController{
    @FXML Label recitersName;
    String audioPath;
    MediaPlayer mediaPlayer;
    private RecitationController parentController;

    public void setParentController(RecitationController controller) {
        this.parentController = controller;
    }

    public void setReciterInfo(String name, String path) {
        recitersName.setText(name);
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
}
