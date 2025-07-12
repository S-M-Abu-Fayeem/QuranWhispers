package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.GlobalState;
import util.PosterGenerator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class ProfileCardController extends BaseController implements Initializable {
    @FXML ImageView versePosterView;
    int surahNum;
    int ayahNum;
    String emotion;
    String theme;
    String posterPath;

    public void setProfileInfo(int surahNum, int ayahNum, String emotion, String theme) {
        this.surahNum = surahNum;
        this.ayahNum = ayahNum;
        this.emotion = emotion;
        this.theme = theme;
        posterPath = "src/main/resources/images/verse_posters/" + surahNum + "_" + ayahNum + ".png";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create a new task to generate the poster in the background
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


        // After the task completes, update the image
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

        // Start the task in a background thread
        new Thread(posterGenerationTask).start();
    }

    public void handleRemoveFromFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Add to Favourites Button Pressed");
        playClickSound();
    };

    public void handleRecitationViewerBtn(MouseEvent e) throws IOException {
        System.out.println("Recitation Viewer Button Pressed");
//        RecitationController recitationControllerObj = (RecitationController) sceneController.switchTo(GlobalState.RECITATION_FILE);
//        recitationControllerObj.setupPage(posterPath, categoryName, surahNum, ayahNum);
        playClickSound();
    };

    public void handleDownloadOfflineBtn(MouseEvent e) throws IOException {
        System.out.println("Download Offline Button pressed");
        playClickSound();

        String sourceImagePath = posterPath;
        String destinationFolderPath = GlobalState.DOWNLOAD_FOLDER_PATH;
        File sourceFile = new File(sourceImagePath);
        File destinationFolder = new File(destinationFolderPath);

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        String destinationPath = destinationFolderPath + sourceFile.getName();
        Path destinationFile = Path.of(destinationPath);

        try {
            // Copy the file from source to destination
            Files.copy(sourceFile.toPath(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image copied successfully to: " + destinationPath);
        } catch (IOException exception) {
            System.err.println("Error copying file: " + exception.getMessage());
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Download Toaster");
        alert.setHeaderText("Verse poster offline download was successful");
        alert.setContentText("Your generated verse poster is saved at the download folder: " + GlobalState.DOWNLOAD_FOLDER_PATH);
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (Desktop.isDesktopSupported()) {
                File imageFile = new File(destinationPath);
                Desktop desktop = Desktop.getDesktop();
                desktop.open(imageFile);
            } else {
                System.out.println("Desktop is not supported on your system.");
            }
        }
    };

    public void handleShareOptionsBtn(MouseEvent e) throws IOException {
        System.out.println("Share Options Btn Pressed");
//        ShareController shareControllerObj = (ShareController) sceneController.switchTo(GlobalState.SHARE_FILE);
//        shareControllerObj.setupPoster(posterPath, categoryName);
        playClickSound();
    }
}
