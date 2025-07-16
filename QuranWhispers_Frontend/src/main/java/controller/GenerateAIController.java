package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;

import java.io.File;
import java.io.IOException;

public class GenerateAIController extends SearchController{
    @FXML TextArea promptTextArea;
    @FXML ImageView versePosterView;
    @FXML Label categoryField;
    String emotionName;
    String themeName;
    String surah;
    String ayah;
    @FXML Label duaTitle;
    @FXML Text duaArabicBody;
    @FXML Text duaEnglishBody;

    public void setupDuaDetails(String title, String arabicBody, String englishBody) {
        this.duaTitle.setText(title);
        this.duaArabicBody.setText(arabicBody);
        this.duaEnglishBody.setText(englishBody);
    }

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

    public void handleSearchBtn(MouseEvent e) throws IOException {
        System.out.println("Search Button Pressed");
        playClickSound();
        System.out.println("Prompt: " + promptTextArea.getText());

        if (promptTextArea.getText().isEmpty()) {
            System.out.println("Prompt text area is empty. Please enter a prompt.");
            return;
        }

        Task<Void> generateApiBasedVerseTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("text", promptTextArea.getText());

                JSONObject response = BackendAPI.fetch("generateapibasedverse", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Dua Generated successfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            surah = response.getString("surah");
                            ayah = response.getString("ayah");
                            emotionName = response.getString("emotion");
                            themeName = response.getString("theme");

                            SearchController searchController = (SearchController) sceneController.switchTo(GlobalState.SEARCH_FILE);
                            searchController.setupListView();
                            searchController.generatePoster(Integer.parseInt(surah), Integer.parseInt(ayah), emotionName, themeName);

                            promptTextArea.clear();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(generateApiBasedVerseTask).start();
    }
}
