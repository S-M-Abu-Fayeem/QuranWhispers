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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalRecitationController extends BaseController {
    @FXML TextField recitersNameField;
    @FXML TextField filePathField;
    @FXML VBox containerVBox;
    private List<GlobalRecitationCardController> cardControllers = new ArrayList<>();
    String surahNum;
    String ayahNum;
    File selectedFile;
    String recitationPath = "src/main/resources/data/recitations_audio/";

    String parent;
    @FXML ImageView profileNavImageView;
    @FXML ImageView notificationNavImageView;
    @FXML Label profileNavlink;

    // HELPER FUNCTION
    public String getFileExtension(File file) {
        String fileName = file.getName();

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }

        return "";
    }

    public void setupParent(String parent) {
        this.parent = parent;

        if (Objects.equals(parent, GlobalState.PROFILE_FILE)) {
            profileNavlink.setTextFill(Color.BLACK);
            URL profileIconURL = getClass().getResource("/images/black_heart.png");
            if (profileIconURL != null) {
                profileNavImageView.setImage(new Image(profileIconURL.toExternalForm()));
            }
            URL notificationIconURL = getClass().getResource("/images/notifications.png");
            if (notificationIconURL != null) {
                notificationNavImageView.setImage(new Image(notificationIconURL.toExternalForm()));
            }
        } else if (Objects.equals(parent, GlobalState.NOTIFICATION_FILE)) {
            profileNavlink.setTextFill(Color.WHITE);
            URL profileIconURL = getClass().getResource("/images/heart.png");
            if (profileIconURL != null) {
                profileNavImageView.setImage(new Image(profileIconURL.toExternalForm()));
            }

            URL notificationIconURL = getClass().getResource("/images/notifications_active.png");
            if (notificationIconURL != null) {
                notificationNavImageView.setImage(new Image(notificationIconURL.toExternalForm()));
            }
        }
    }

    public void stopAllOtherCards(GlobalRecitationCardController current) {
        for (GlobalRecitationCardController controller : cardControllers) {
            if (controller != current && controller.isPlaying()) {
                controller.stopAudio();
            }
        }
    }

    public void setupRecitation(int surah, int ayah) {
        surahNum = String.valueOf(surah);
        ayahNum = String.valueOf(ayah);

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

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/globalRecitationCard.fxml"));
                                Parent card = loader.load();

                                GlobalRecitationCardController controller = loader.getController();
                                cardControllers.add(controller);
                                if (sceneController != null) controller.setSceneController(sceneController);
                                controller.setupGlobalRecitationCard(surahNum, ayahNum, recitersName);
                                controller.setParentController(GlobalRecitationController.this);

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


    public void handleCloseBtn(MouseEvent e) throws IOException {
        System.out.println("Close Button Pressed");
        playClickSound();

        for (GlobalRecitationCardController controller : cardControllers) {
            controller.stopAudio();
        }

        sceneController.switchTo(parent);
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
                                sceneController.switchTo(parent);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
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
        FileChooser.ExtensionFilter audioFilter = new FileChooser.ExtensionFilter("Audio Files", "*.mp3");
        fileChooser.getExtensionFilters().add(audioFilter);

        Stage stage = (Stage) filePathField.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getName());
        }
    }
}
