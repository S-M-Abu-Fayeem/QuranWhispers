package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class RecitationController extends SearchController {
    @FXML ImageView versePosterView;
    @FXML Label categoryField;
    @FXML TextField recitersNameField;
    @FXML TextField filePathField;
    @FXML VBox containerVBox;
    private List<RecitationCardController> cardControllers = new ArrayList<>();
    String surahNum;
    String ayahNum;
    File selectedFile;
    String recitationPath = "src/main/resources/data/recitations_audio/";
    @FXML Label duaTitle;
    @FXML Text duaArabicBody;
    @FXML Text duaEnglishBody;

    // HELPER FUNCTION
    public String getFileExtension(File file) {
        String fileName = file.getName();

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }

        return "";
    }

    public void stopAllOtherCards(RecitationCardController current) {
        for (RecitationCardController controller : cardControllers) {
            if (controller != current && controller.isPlaying()) {
                controller.stopAudio();
            }
        }
    }

    public void setupRecitation() {
        if (sceneController == null) {
            System.err.println("SceneController is null in RecitationController");
        }

        Task<Void> GetRecitationBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("surah", String.valueOf(surahNum));
                request.put("ayah", String.valueOf(ayahNum));

                JSONObject response = BackendAPI.fetch("getayahrecitations", request);
                if (response.getString("status").equals("200")) {
                    Platform.runLater(() -> {
                        try {
                            containerVBox.getChildren().clear();
                            cardControllers.clear();

                            JSONArray recitersArray = response.getJSONArray("recitations");
                            System.out.println(recitersArray.toString());
                            for (int i = 0; i < recitersArray.length(); i++) {
                                String recitersName = recitersArray.getString(i);

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recitationCard.fxml"));
                                Parent card = loader.load();

                                RecitationCardController controller = loader.getController();
                                cardControllers.add(controller);
                                if (sceneController != null) controller.setSceneController(sceneController);
                                controller.setupRecitationCard(surahNum, ayahNum, recitersName);
                                controller.setParentController(RecitationController.this);

                                containerVBox.getChildren().add(card);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(GetRecitationBackendAPITask).start();
    }


    public void setupDuaDetails(String title, String arabicBody, String englishBody) {
        this.duaTitle.setText(title);
        this.duaArabicBody.setText(arabicBody);
        this.duaEnglishBody.setText(englishBody);
    }

    public void setupPage(String posterPath, String categoryName, int surahNum, int ayahNum) {
        File posterFile = new File(posterPath);
        if (posterFile.exists()) {
            Image poster = new Image(posterFile.toURI().toString());
            versePosterView.setImage(poster);
        } else {
            System.out.println("Poster file not found: " + posterFile.getAbsolutePath());
        }
        categoryField.setText(categoryName);
        this.surahNum = String.valueOf(surahNum);
        this.ayahNum = String.valueOf(ayahNum);
    }

    public void handleCloseBtn(MouseEvent e) throws IOException {
        System.out.println("Close Button Pressed");
        playClickSound();

        for (RecitationCardController controller : cardControllers) {
            controller.stopAudio();
        }

        sceneController.switchTo(GlobalState.SEARCH_FILE);
    }

    public void handleRequestBtn(MouseEvent e) throws IOException {
        System.out.println("Request Button Pressed");
        playClickSound();

        if (selectedFile == null) {
            System.out.println("No file selected.");
            return;
        }

        File destinationFolder = new File(recitationPath);

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        File destinationFile = new File(destinationFolder, surahNum + "_" + ayahNum + "_" + recitersNameField.getText() + "." + getFileExtension(selectedFile));

        try {
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully to: " + destinationFile.getAbsolutePath());

            Task<Void> uploadRecitationTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    JSONObject request = new JSONObject();
                    request.put("reciter_name", recitersNameField.getText());
                    request.put("surah", surahNum);
                    request.put("ayah", ayahNum);
                    request.put("mp3file", destinationFile.getAbsolutePath());

                    JSONObject response = BackendAPI.fetch("uploadmp3", request);

                    if (response.getString("status").equals("200")) {
                        System.out.println("Recitation uploaded successfully: " + response.toString());

                        Platform.runLater(() -> {
                            try {
                                selectedFile = null;
                                filePathField.setText("");
                                sceneController.switchTo(GlobalState.SEARCH_FILE);
                                alertGenerator("Action successful", "ACTION: REQUEST RECITATION", response.getString("status_message"), "confirmation", "/images/confrim.png");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    } else if (response.getString("status").equals("409")) {
                        Platform.runLater(() -> {
                            alertGenerator("Request failed", "INVALID USERNAME", response.getString("status_message"), "error", "/images/denied.png");
                        });
                    } else if (response.getString("status").equals("500")) {
                        Platform.runLater(() -> {
                            alertGenerator("Request failed", "SERVER ERROR", response.getString("status_message"), "error", "/images/denied.png");
                        });
                    } else {
                        Platform.runLater(() -> {
                            alertGenerator("Error", "UNKNOWN PROBLEM", "Something went wrong :(", "error", "/images/denied.png");
                        });
                    }
                    return null;
                }
            };
            new Thread(uploadRecitationTask).start();
        } catch (IOException ex) {
            System.out.println("Failed to copy the file: " + ex.getMessage());
        }
    }


    public void handleSelectFileBtn(MouseEvent e) throws IOException {
        System.out.println("Select File Btn Pressed");
        playClickSound();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter audioFilter = new FileChooser.ExtensionFilter("Audio Files","*.mp3");
        fileChooser.getExtensionFilters().add(audioFilter);

        Stage stage = (Stage) filePathField.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getName());
        }
    }
}
