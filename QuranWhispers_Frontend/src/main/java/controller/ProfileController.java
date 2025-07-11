package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
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

public class ProfileController extends BaseController implements Initializable {
    @FXML FlowPane containerFlowPane;
    @FXML Label username;
    @FXML Label email;
    @FXML Label verseSaved;
    @FXML Label verseReceived;

    private List<ProfileCardController> cardControllers = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText("ahammadShawki88");
        email.setText("ahammadshawki88@gmail.com");
        verseSaved.setText("54");
        verseReceived.setText("08");

        try {
            InputStream is = getClass().getResourceAsStream("/data/favourite_test.json");
            if (is == null) {
                System.out.println("JSON file not found in resources!");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JSONArray favouriteArray = new JSONArray(content);

            for (int i = 0; i < favouriteArray.length(); i++) {
                JSONObject favourite = favouriteArray.getJSONObject(i);
                int surahNum = favourite.getInt("surahNum");
                int ayahNum = favourite.getInt("ayahNum");

                // Load the card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profileCard.fxml"));
                Parent card = loader.load();

                // Access the card controller and pass data
                ProfileCardController controller = loader.getController();
                cardControllers.add(controller);
                controller.setProfileInfo(surahNum, ayahNum);

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
