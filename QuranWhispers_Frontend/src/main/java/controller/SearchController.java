package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import util.PosterGenerator;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    };

    public void handleShareOptionsBtn(MouseEvent e) throws IOException {
        System.out.println("Share Options Btn Pressed");
        playClickSound();
    }


}
