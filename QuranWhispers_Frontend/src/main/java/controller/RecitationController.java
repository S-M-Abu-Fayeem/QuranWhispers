package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import util.GlobalState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RecitationController extends SearchController implements Initializable {
    @FXML ImageView versePosterView;
    @FXML Label categoryField;
    @FXML TextField recitersNameField;
    @FXML TextField filePathField;
    @FXML VBox containerVBox;
    private List<RecitationCardController> cardControllers = new ArrayList<>();
    int surahNum;
    int ayahNum;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream is = getClass().getResourceAsStream("/data/recitation_test.json");
            if (is == null) {
                System.out.println("JSON file not found in resources!");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray recitersArray = new JSONArray(content);

            for (int i = 0; i < recitersArray.length(); i++) {
                JSONObject reciter = recitersArray.getJSONObject(i);
                String recitersName = reciter.getString("recitersName");
                String path = reciter.getString("path");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recitationCard.fxml"));
                Parent card = loader.load();

                RecitationCardController controller = loader.getController();
                cardControllers.add(controller);
                controller.setReciterInfo(recitersName, path);
                controller.setParentController(this);

                if (card != null) {
                    containerVBox.getChildren().add(card);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error loading FXML or setting controller data: " + e.getMessage());
            e.printStackTrace();
        }
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
        this.surahNum = surahNum;
        this.ayahNum = ayahNum;
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

        if (selectedFile != null) {
            File destinationFolder = new File(recitationPath);

            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            File destinationFile = new File(destinationFolder, surahNum + "_" + ayahNum + "_" + recitersNameField.getText() + "." + getFileExtension(selectedFile));

            try {
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File copied successfully to: " + destinationFile.getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Failed to copy the file: " + ex.getMessage());
            }
        } else {
            System.out.println("No file selected.");
        }
        selectedFile = null;
        filePathField.setText("");
        sceneController.switchTo(GlobalState.SEARCH_FILE);
    }

    public void handleSelectFileBtn(MouseEvent e) throws IOException {
        System.out.println("Select File Btn Pressed");
        playClickSound();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter audioFilter = new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav");
        fileChooser.getExtensionFilters().add(audioFilter);

        Stage stage = (Stage) filePathField.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getName());
        }
    }
}
