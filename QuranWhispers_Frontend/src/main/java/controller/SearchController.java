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
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;
import util.PosterGenerator;
import javafx.concurrent.Task;
import util.SessionManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    // THIS WILL BE FETCHED FROM BACKEND
    int surahNum;
    int ayahNum;
    String [] emotionArray; // = {"Afraid", "Depressed", "Feeling Lonely", "Last Hope", "Need Courage", "Seeking Peace", "Need Direction", ""};
    String [] themeArray; // = {"Faith and Belief (Iman)", "Guidance (Hidayah)", "Worship (Ibadah)", "Patience (Sabr)", "Gratitude (Shukr)", "Justice (Adl)", "The Afterlife (Akhirah)", "Repentance (Tawbah)", "Family and Relationships"};

    String posterPath;

    public void setupListView() {
        Task<Void> getListBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());
                for (String key : request.keySet()) {
                    System.out.println("Key: " + key + " | Value: " + request.get(key));
                }

                JSONObject response = BackendAPI.fetch("getlist", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Fetch successful");
                    JSONArray jsonArrayEmotion = response.getJSONArray("emotions");
                    emotionArray = new String[jsonArrayEmotion.length()];

                    for (int i = 0; i < jsonArrayEmotion.length(); i++) {
                        emotionArray[i] = jsonArrayEmotion.getString(i);  // Get each string from JSONArray
                    }
                    JSONArray jsonArrayTheme = response.getJSONArray("themes");
                    themeArray = new String[jsonArrayTheme.length()];

                    for (int i = 0; i < jsonArrayTheme.length(); i++) {
                        themeArray[i] = jsonArrayTheme.getString(i);  // Get each string from JSONArray
                    }
                } else {
                    System.out.println("Fetch failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(getListBackendAPITask).start();
    }

    public void setupDua() {
        Task<Void> getDuaBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryListView.setVisible(false);

        categoryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                categoryName = categoryListView.getSelectionModel().getSelectedItem();
                categoryField.setText(categoryName);
                categoryListView.setVisible(false);
                categoryListView.getItems().clear();

                // Fetch the Surah and Ayah numbers based on the selected category
                Task<Void> getVerseBackendAPITask= new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        JSONObject request = new JSONObject();
                        request.put("email", SessionManager.getEmail());
                        request.put("token", SessionManager.getToken());
                        for (String key : request.keySet()) {
                            System.out.println("Key: " + key + " | Value: " + request.get(key));
                        }

                        JSONObject response;
                        if (isEmotion) {
                            request.put("emotion", categoryName);
                            response = BackendAPI.fetch("generateemotionbasedverse", request);
                        } else {
                            request.put("theme", categoryName);
                            response = BackendAPI.fetch("generatethemebasedverse", request);
                        }
                        if (response.getString("status").equals("200")) {
                            System.out.println("Fetch successful");
                            surahNum = response.getInt("surah");
                            ayahNum = response.getInt("ayah");
                            emotionName = response.getString("emotion");
                            themeName = response.getString("theme");
                        } else {
                            System.out.println("Fetch failed: " + response.getString("message"));
                        }
                        return null;
                    }
                };
                new Thread(getVerseBackendAPITask).start();

                // Create a new task to generate the poster in the background
                getVerseBackendAPITask.setOnSucceeded(event -> {
                    posterPath = "src/main/resources/images/verse_posters/" + surahNum + "_" + ayahNum + ".png";
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
                    posterGenerationTask.setOnSucceeded(ev -> {
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
                });
            }
        });
    }

    public void handleEmotionBtn(MouseEvent e) throws IOException {
        System.out.println("Emotion Pressed");
        categoryListView.getItems().clear();

        categoryType = "Emotion";

        categoryListView.getItems().addAll(emotionArray);
        categoryListViewVisible = !categoryListViewVisible;
        categoryListView.setVisible(categoryListViewVisible);
        if (categoryListViewVisible) {
            isEmotion = true;
        }
        playClickSound();
    };


    public void handleThemeBtn(MouseEvent e) throws IOException {
        System.out.println("Theme Btn Pressed");
        categoryListView.getItems().clear();
        categoryType = "Theme";
        categoryListView.getItems().addAll(themeArray);
        categoryListViewVisible = !categoryListViewVisible;
        categoryListView.setVisible(categoryListViewVisible);
        if (categoryListViewVisible) {
            isEmotion = false;
        }
        playClickSound();
    };

    public void handleAddToFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Add to Favourites Button Pressed");
        Task<Void> addFavouriteBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());
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
        playClickSound();
    };

    public void handleRecitationViewerBtn(MouseEvent e) throws IOException {
        System.out.println("Recitation Viewer Button Pressed");
        RecitationController recitationControllerObj = (RecitationController) sceneController.switchTo(GlobalState.RECITATION_FILE);
        recitationControllerObj.setupPage(posterPath, categoryName, surahNum, ayahNum);
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
        ShareController shareControllerObj = (ShareController) sceneController.switchTo(GlobalState.SHARE_FILE);
        shareControllerObj.setupPoster(posterPath, categoryName);
        playClickSound();
    }


}
