package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import util.GlobalState;
import util.PosterGenerator;
import javafx.concurrent.Task;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class SearchController extends BaseController implements Initializable {
    @FXML Label catagoryField;
    @FXML ListView <String> catagoryListView;
    @FXML Label duaTitle;
    @FXML Text duaArabicBody;
    @FXML Text duaEnglishBody;
    @FXML ImageView versePosterView;
    String catagoryType;
    String catagoryName;

    // THIS WILL BE FETCHED FROM BACKEND
    int surahNum = 10;
    int ayahNum = 80;
    String[] emotionArray = {"Afraid", "Depressed", "Feeling Lonely", "Last Hope", "Need Courage", "Seeking Peace", "Need Direction", ""};
    String[] ThemeArray = {"Faith and Belief (Iman)", "Guidance (Hidayah)", "Worship (Ibadah)", "Patience (Sabr)", "Gratitude (Shukr)", "Justice (Adl)", "The Afterlife (Akhirah)", "Repentance (Tawbah)", "Family and Relationships"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        catagoryListView.setVisible(false);

        catagoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String currentCatagory = catagoryListView.getSelectionModel().getSelectedItem();
                catagoryField.setText(currentCatagory);
                catagoryName = currentCatagory;
                catagoryListView.setVisible(false);
                catagoryListView.getItems().clear();

                // Create a new task to generate the poster in the background
                Task<Void> posterGenerationTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        System.out.println("Hello");
                        PosterGenerator.generatePosterAndSave(surahNum, ayahNum);
                        return null;
                    }
                };

                // After the task completes, update the image
                posterGenerationTask.setOnSucceeded(event -> {
                    Platform.runLater(() -> {
                        File posterFile = new File("src/main/resources/images/verse_posters/" + surahNum + "_" + ayahNum + ".png");
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
        });
    }

    public void handleEmotionBtn(MouseEvent e) throws IOException {
        System.out.println("Emotion Pressed");
        catagoryListView.getItems().clear();

        catagoryType = "Emotion";
        catagoryListView.getItems().addAll(emotionArray);
        catagoryListView.setVisible(true);
        playClickSound();
    };


    public void handleThemeBtn(MouseEvent e) throws IOException {
        System.out.println("Theme Btn Pressed");
        catagoryListView.getItems().clear();
        catagoryType = "Theme";
        catagoryListView.getItems().addAll(ThemeArray);
        catagoryListView.setVisible(true);
        playClickSound();
    };

    public void handleAddToFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Add to Favourites Button Pressed");
        playClickSound();
    };

    public void handleRecitationViewerBtn(MouseEvent e) throws IOException {
        System.out.println("Recitation Viewer Button Pressed");
        playClickSound();
    };

    public void handleDownloadOfflineBtn(MouseEvent e) throws IOException {
        System.out.println("Download Offline Button pressed");
        playClickSound();

        String sourceImagePath = "src/main/resources/images/verse_posters/" + surahNum + "_" + ayahNum + ".png";
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
        sceneController.switchTo(GlobalState.SHARE_FILE);
        playClickSound();
    }


}
