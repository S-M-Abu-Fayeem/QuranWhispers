package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationController extends BaseController implements Initializable {
    @FXML FlowPane containerFlowPane;
    private List<NotificationCardController> cardControllers = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream is = getClass().getResourceAsStream("/data/notification_test.json");
            if (is == null) {
                System.out.println("JSON file not found in resources!");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JSONArray notificationsArray = new JSONArray(content);

            for (int i = 0; i < notificationsArray.length(); i++) {
                JSONObject notification = notificationsArray.getJSONObject(i);
                String senderUsername = notification.getString("senderUsername");
                int surahNum = notification.getInt("surahNum");
                int ayahNum = notification.getInt("ayahNum");

                // Load the card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notificationCard.fxml"));
                Parent card = loader.load();

                // Access the card controller and pass data
                NotificationCardController controller = loader.getController();
                cardControllers.add(controller);
                controller.setNotificationInfo(surahNum, ayahNum, senderUsername);

                // Add to VBox
                if (card != null) {
                    containerFlowPane.getChildren().add(card);
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
