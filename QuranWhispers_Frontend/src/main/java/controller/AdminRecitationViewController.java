package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminRecitationViewController extends BaseControllerAdmin implements Initializable {
    @FXML
    VBox containerVBox;
    private List<AdminRecitationViewCardController> cardControllers = new ArrayList<>();

    public void stopAllOtherCards(AdminRecitationViewCardController current) {
        for (AdminRecitationViewCardController controller : cardControllers) {
            if (controller != current && controller.isPlaying()) {
                controller.stopAudio();
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream is = getClass().getResourceAsStream("/data/adminRecitationView_test.json");
            if (is == null) {
                System.out.println("JSON file not found in resources!");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JSONArray adminRecitationViewArray = new JSONArray(content);

            for (int i = 0; i < adminRecitationViewArray.length(); i++) {
                JSONObject adminRecitationView = adminRecitationViewArray.getJSONObject(i);
                String surah = String.valueOf(adminRecitationView.getInt("surahNum"));
                String ayah = String.valueOf(adminRecitationView.getInt("ayahNum"));
                String reciterName = adminRecitationView.getString("reciterName");
                String path = adminRecitationView.getString("path");

                // Load the card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminRecitationViewCard.fxml"));
                Parent card = loader.load();

                // Access the card controller and pass data
                AdminRecitationViewCardController controller = loader.getController();
                cardControllers.add(controller);
                controller.setupAdminRecitationViewInfo(surah, ayah, reciterName, path);
                controller.setupParentController(this);

                // Add to VBox
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
}
