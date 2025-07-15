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

public class AdminApproveController extends BaseControllerAdmin implements Initializable {
    @FXML VBox containerVBox;
    private List<AdminApproveCardController> cardControllers = new ArrayList<>();

    public void stopAllOtherCards(AdminApproveCardController current) {
        for (AdminApproveCardController controller : cardControllers) {
            if (controller != current && controller.isPlaying()) {
                controller.stopAudio();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream is = getClass().getResourceAsStream("/data/adminApprove_test.json");
            if (is == null) {
                System.out.println("JSON file not found in resources!");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JSONArray adminApproveArray = new JSONArray(content);

            for (int i = 0; i < adminApproveArray.length(); i++) {
                JSONObject adminApprove = adminApproveArray.getJSONObject(i);
                int surahNum = adminApprove.getInt("surahNum");
                int ayahNum = adminApprove.getInt("ayahNum");
                String requestorUsername = adminApprove.getString("requestorUsername");
                String reciterName = adminApprove.getString("reciterName");
                String path = adminApprove.getString("path");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminApproveCard.fxml"));
                Parent card = loader.load();

                AdminApproveCardController controller = loader.getController();
                cardControllers.add(controller);
                controller.setupAdminApproveInfo(surahNum, ayahNum, requestorUsername, reciterName, path);
                controller.setupParentController(this);

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
