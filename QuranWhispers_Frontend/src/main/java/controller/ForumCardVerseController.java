package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import util.GlobalState;
import util.PosterGenerator;
import util.SessionManager;
import java.io.File;

public class ForumCardVerseController extends BaseController {
    @FXML Rectangle msgCard;
    @FXML HBox wrapperHBox;
    @FXML Label senderUsername;
    @FXML Rectangle senderUsernameWrapper;
    @FXML ImageView versePosterView;
    @FXML Label msgID;
    String surah;
    String ayah;

    String parent;
    ForumController forumController;
    AdminForumController adminForumController;

    public void setParent(String parent, ForumController forumController) {
        this.parent = parent;
        this.forumController = forumController;
    };

    public void setParent(String parent, AdminForumController adminForumController) {
        this.parent = parent;
        this.adminForumController = adminForumController;
    };

    public void setupCard(String msgID, String senderUsername, String surah, String ayah) {
        this.msgID.setText(msgID);
        this.senderUsername.setText(senderUsername);
        this.surah = surah;
        this.ayah = ayah;
        if (senderUsername.equals(SessionManager.getUsername())) {
            msgCard.setStroke(Color.BLACK);
            wrapperHBox.setAlignment(Pos.CENTER_RIGHT);
        }
        generatePoster(Integer.parseInt(surah), Integer.parseInt(ayah));
    }

    public void generatePoster(int surahNum, int ayahNum) {
        String posterPath = "src/main/resources/images/verse_posters/" + surahNum + "_" + ayahNum + ".png";
        Task<Void> posterGenerationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                File posterFile = new File(posterPath);
                if (!posterFile.exists()) {
                    PosterGenerator.generatePosterAndSave(surahNum, ayahNum);
                }
                return null;
            }
        };

        posterGenerationTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                File posterFile = new File(posterPath);
                if (posterFile.exists()) {
                    Image poster = new Image(posterFile.toURI().toString());
                    versePosterView.setImage(poster);
                } else {
                    System.out.println("Poster file not found: " + posterFile.getAbsolutePath());
                }
            });
        });
        new Thread(posterGenerationTask).start();
    }

    public void handleReplyBtn() {
        playClickSound();
        if (parent.equals(GlobalState.FORUM_FILE)) {
            forumController.setPromptArea("/reply("+msgID.getText()+")/");
        } else {
            adminForumController.setPromptArea("/reply("+msgID.getText()+")/");
        }
    }

}
