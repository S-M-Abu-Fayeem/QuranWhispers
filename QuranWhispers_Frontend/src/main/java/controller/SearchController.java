package controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class SearchController extends BaseController implements Initializable {
    @FXML Label categoryField;
    @FXML ListView <String> categoryListView;
    @FXML Label duaTitle;
    @FXML Text duaArabicBody;
    @FXML Text duaEnglishBody;
    @FXML ImageView versePosterView;
    String categoryType;
    String categoryName;
    String emotionName;
    String themeName;
    boolean categoryListViewVisible = false;
    boolean isEmotion = false;
    int surahNum;
    int ayahNum;
    String [] emotionArray; // = {"Afraid", "Depressed", "Feeling Lonely", "Last Hope", "Need Courage", "Seeking Peace", "Need Direction", "Happy", "Sad", "Angry", "Grateful", "Hopeful", "Confused", "Stressed", "Anxious", "Lost", "Insecure", "Overwhelmed", "Disappointed", "Jealous", "Guilty"};
    String [] themeArray; // = {"Faith and Belief (Iman)", "Guidance (Hidayah)", "Worship (Ibadah)", "Patience (Sabr)", "Gratitude (Shukr)", "Justice (Adl)", "The Afterlife (Akhirah)", "Repentance (Tawbah)", "Family and Relationships", "Community and Society", "Knowledge and Wisdom", "Charity and Generosity (Sadaqah)", "Forgiveness (Maghfirah)", "Love and Compassion (Rahmah)", "Unity (Wahdah)", "Peace (Salam)", "Trust in Allah (Tawakkul)", "Hope (Raja')", "Courage (Shaja'ah)", "Humility (Tawadu')", "Self-Reflection (Muhasabah)"};

    String posterPath;

    boolean isGenerateAIloading = false;
    @FXML
    Pane loadingOverlay;
    @FXML private Rectangle loaderRectangle;
    private double angle = 0;
    private double speedFactor = 3;

    public void setupLoaderAnimation() {
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                angle += speedFactor;
                if (angle >= 360) {
                    angle = 0;
                }
                loaderRectangle.setRotate(angle);
            }
        };
        animationTimer.start();
    }

    public void toggleLoadingOverlay(boolean show) {
        if (show) {
            loadingOverlay.setVisible(true);
            loaderRectangle.setVisible(true);
            setupLoaderAnimation();
        } else {
            loadingOverlay.setVisible(false);
            loaderRectangle.setVisible(false);
        }
    }

    public void setCategoryListViewVisible(boolean categoryListViewVisible) {
        this.categoryListViewVisible = categoryListViewVisible;
    }

    public void setupListView() {
        Task<Void> getListBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                for (String key : request.keySet()) {
                    System.out.println("Key: " + key + " | Value: " + request.get(key));
                }

                JSONObject response = BackendAPI.fetch("getlist", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Fetch successful");
                    JSONArray jsonArrayEmotion = response.getJSONArray("emotions");
                    emotionArray = new String[jsonArrayEmotion.length()];

                    for (int i = 0; i < jsonArrayEmotion.length(); i++) {
                        if (!(jsonArrayEmotion.isNull(i) || jsonArrayEmotion.getString(i).isEmpty() || jsonArrayEmotion.getString(i).equals(""))) {
                            emotionArray[i] = jsonArrayEmotion.getString(i);  // Get each string from JSONArray
                        }
                    }
                    emotionArray = Arrays.stream(emotionArray).filter(Objects::nonNull).toArray(String[]::new);

                    JSONArray jsonArrayTheme = response.getJSONArray("themes");
                    themeArray = new String[jsonArrayTheme.length()];

                    for (int i = 0; i < jsonArrayTheme.length(); i++) {
                        if (!(jsonArrayTheme.isNull(i) || jsonArrayTheme.getString(i).isEmpty() || jsonArrayTheme.getString(i).equals(""))) {
                            themeArray[i] = jsonArrayTheme.getString(i);  // Get each string from JSONArray
                        }
                    }
                    themeArray = Arrays.stream(themeArray).filter(Objects::nonNull).toArray(String[]::new);

                } else {
                    System.out.println("Fetch failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(getListBackendAPITask).start();
    }

    public void setupDua() {
        toggleLoadingOverlay(isGenerateAIloading);
        Task<Void> getDuaBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                for (String key : request.keySet()) {
                    System.out.println("Key: " + key + " | Value: " + request.get(key));
                }

                JSONObject response = BackendAPI.fetch("getduaoftheday", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Fetch successful");
                    System.out.println("Response: " + response.getString("title"));
                    Platform.runLater(() -> {
                        System.out.println("Setting up Dua of the Day");
                        try {
                            duaTitle.setText(response.getString("title"));
                            duaArabicBody.setText(response.getString("arabic"));
                            duaEnglishBody.setText(response.getString("english"));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    System.out.println("Fetch failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(getDuaBackendAPITask).start();
    }

    private void fetchAndGeneratePoster(String category, String requestType) {
        Task<Void> getVerseBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put(requestType, category);

                JSONObject response = BackendAPI.fetch("generate" + requestType + "basedverse", request);
                if (response.getString("status").equals("200")) {
                    surahNum = response.getInt("surah");
                    ayahNum = response.getInt("ayah");
                    emotionName = response.getString("emotion");
                    themeName = response.getString("theme");

                    System.out.println("Emotion: " + emotionName + ", Theme: " + themeName);

                    generatePoster(surahNum, ayahNum, emotionName, themeName);
                } else {
                    System.out.println("Fetch failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(getVerseBackendAPITask).start();
    }


    public void generatePoster(int surahNum, int ayahNum, String emotionName, String themeName) {
        posterPath = "src/main/resources/images/verse_posters/" + surahNum + "_" + ayahNum + ".png";
        Task<Void> posterGenerationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                isGenerateAIloading = true;
                toggleLoadingOverlay(isGenerateAIloading);
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
                    if (emotionName != null && !emotionName.isEmpty() && isEmotion) {
                        categoryName = emotionName;
                    } else  {
                        categoryName = themeName;
                    }
                    System.out.println("Category Name: " + categoryName);
                    categoryField.setText(categoryName);
                    categoryListViewVisible = !categoryListViewVisible;
                    System.out.println("Category List View Visible (inside trigger): " + categoryListViewVisible);
                    categoryListView.setVisible(categoryListViewVisible);
                    categoryListView.getItems().clear();
                } else {
                    System.out.println("Poster file not found: " + posterFile.getAbsolutePath());
                }
                isGenerateAIloading =  false;
                toggleLoadingOverlay(isGenerateAIloading);
            });
        });
        new Thread(posterGenerationTask).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryListView.setVisible(false);

        categoryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            categoryName = categoryListView.getSelectionModel().getSelectedItem();

            if (categoryName != null) {
                fetchAndGeneratePoster(categoryName, isEmotion ? "emotion" : "theme");
            }
        });
    }


    public void handleEmotionBtn(MouseEvent e) throws IOException {
        System.out.println("Emotion Pressed");
        playClickSound();
        System.out.println("Is Emotion when pressed: " + isEmotion);
        categoryListView.getItems().clear();

        categoryType = "Emotion";

        categoryListView.getItems().addAll(emotionArray);
        categoryListViewVisible = !categoryListViewVisible;
        System.out.println("Category List View Visible: " + categoryListViewVisible);
        categoryListView.setVisible(categoryListViewVisible);
        if (categoryListViewVisible) {
            isEmotion = true;
        }
        System.out.println("Is Emotion after pressed: " + isEmotion);
    };


    public void handleThemeBtn(MouseEvent e) throws IOException {
        System.out.println("Theme Btn Pressed");
        playClickSound();
        System.out.println("Is Emotion when pressed: " + isEmotion);
        categoryListView.getItems().clear();
        categoryType = "Theme";
        categoryListView.getItems().addAll(themeArray);
        categoryListViewVisible = !categoryListViewVisible;
        categoryListView.setVisible(categoryListViewVisible);
        if (categoryListViewVisible) {
            isEmotion = false;
        }
        System.out.println("Is Emotion after pressed: " + isEmotion);
    };

    public void handleAddToFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Add to Favourites Button Pressed");
        playClickSound();
        Task<Void> addFavouriteBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("emotion", emotionName);
                request.put("theme", themeName);
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
                } else {
                    System.out.println("Fetch failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(addFavouriteBackendAPITask).start();
    };

    public void handleRecitationViewerBtn(MouseEvent e) throws IOException {
        System.out.println("Recitation Viewer Button Pressed");
        playClickSound();
        RecitationController recitationController = (RecitationController) sceneController.switchTo(GlobalState.RECITATION_FILE);
        recitationController.setupPage(posterPath, categoryName, surahNum, ayahNum);
        recitationController.setupDuaDetails(duaTitle.getText(), duaArabicBody.getText(), duaEnglishBody.getText());
        recitationController.setupRecitation();
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
        ShareController shareController = (ShareController) sceneController.switchTo(GlobalState.SHARE_FILE);
        shareController.setupVerseDetails(surahNum, ayahNum, emotionName, themeName);
        shareController.setupDuaDetails(duaTitle.getText(), duaArabicBody.getText(), duaEnglishBody.getText());
        shareController.setupPoster(posterPath, categoryName);
    }


    public void handleGenerateAIBtn(MouseEvent e) throws IOException {
        System.out.println("Generate AI Button Pressed");
        playClickSound();
        GenerateAIController generateAIController = (GenerateAIController) sceneController.switchTo(GlobalState.GENERATE_AI_FILE);
        generateAIController.setupDuaDetails(duaTitle.getText(), duaArabicBody.getText(), duaEnglishBody.getText());
        generateAIController.setupPoster(posterPath, categoryName);
    }

}
