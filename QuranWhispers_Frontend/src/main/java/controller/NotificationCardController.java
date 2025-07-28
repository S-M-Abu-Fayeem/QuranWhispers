package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;
import util.PosterGenerator;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NotificationCardController extends BaseController {
    @FXML Label senderUsername;
    @FXML ImageView versePosterView;
    int surahNum;
    int ayahNum;
    String emotion;
    String theme;
    String posterPath;

    public void setupNotificationInfo(int surahNum, int ayahNum, String emotion, String theme, String sender) {
        this.surahNum = surahNum;
        this.ayahNum = ayahNum;
        this.emotion = emotion;
        this.theme = theme;
        senderUsername.setText(sender);
        posterPath = "src/main/resources/images/verse_posters/" + surahNum + "_" + ayahNum + ".png";
    }

    public void setupNotificationCard() {
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

    public void handleAddToFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Add to Favourites Button Pressed");
        playClickSound();
        Task<Void> addFavouriteBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("emotion", emotion);
                request.put("theme", theme);
                request.put("ayah", String.valueOf(ayahNum));
                request.put("surah", String.valueOf(surahNum));
                for (String key : request.keySet()) {
                    System.out.println("Key: " + key + " | Value: " + request.get(key));
                }

                JSONObject response = BackendAPI.fetch("addtofavourites", request);
                System.out.println(response.getString("status"));
                if (response.getString("status").equals("200")) {
                    System.out.println("Fetch successful");
                    Platform.runLater(() -> {
                        try {
                            ProfileController profileController = (ProfileController) sceneController.switchTo(GlobalState.PROFILE_FILE);
                            profileController.setupProfile();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else if (response.getString("status").equals("409")) {
                    Platform.runLater(() -> {
                        alertGenerator("Add failed", "INVALID OPERATION", response.getString("status_message"), "error", "/images/denied.png");
                    });
                } else if (response.getString("status").equals("500")) {
                    Platform.runLater(() -> {
                        alertGenerator("Add failed", "SERVER ERROR", response.getString("status_message"), "error", "/images/denied.png");
                    });
                } else {
                    Platform.runLater(() -> {
                        alertGenerator("Error", "UNKNOWN PROBLEM", "Something went wrong :(", "error", "/images/denied.png");
                    });
                }
                return null;
            }
        };
        new Thread(addFavouriteBackendAPITask).start();
    };

    public void handleRecitationViewerBtn(MouseEvent e) throws IOException {
        System.out.println("Recitation Viewer Button Pressed");
        playClickSound();
        GlobalRecitationController globalRecitationController = (GlobalRecitationController) sceneController.switchTo(GlobalState.GLOBAL_RECITATION_FILE);
        globalRecitationController.setupParent(GlobalState.NOTIFICATION_FILE);
        globalRecitationController.setupRecitation(surahNum, ayahNum);
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


        ButtonType bt = alertGenerator("Download Successful", "Verse poster offline download was successful",
                "Your generated verse poster is saved at the download folder: " + GlobalState.DOWNLOAD_FOLDER_PATH,
                "confirmation", null);

        if (bt == ButtonType.OK) {
            if (Desktop.isDesktopSupported()) {
                File imageFile = new File(destinationPath);
                if (imageFile.exists()) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(imageFile);
                } else {
                    System.out.println("File not found: " + destinationPath);
                }
            } else {
                System.out.println("Desktop is not supported on your system.");
            }
        }
    };

    public void handleShareOptionsBtn(MouseEvent e) throws IOException {
        System.out.println("Share Options Btn Pressed");
        playClickSound();
        GlobalShareController globalShareController = (GlobalShareController) sceneController.switchTo(GlobalState.GLOBAL_SHARE_FILE);
        globalShareController.setupVerseDetails(surahNum, ayahNum, emotion, theme);
        globalShareController.setupParent(GlobalState.NOTIFICATION_FILE);
    }
}
