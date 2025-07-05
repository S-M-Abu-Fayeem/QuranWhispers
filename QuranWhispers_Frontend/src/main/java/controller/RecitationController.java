package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.GlobalState;

import java.io.File;
import java.io.IOException;

public class RecitationController extends SearchController{
    @FXML ImageView versePosterView;
    @FXML Label categoryField;
    @FXML TextField recitersNameField;
    @FXML TextField filePathField;

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

    public void handleRequestBtn(MouseEvent e) throws IOException {
        System.out.println("Request Button Pressed");
        playClickSound();
    }

    public void handleSelectFileBtn(MouseEvent e) throws IOException {
        System.out.println("Select File Btn Pressed");
        playClickSound();
    }
}
