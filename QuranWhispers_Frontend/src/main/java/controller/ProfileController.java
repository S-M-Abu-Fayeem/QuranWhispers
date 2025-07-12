package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
import util.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController extends BaseController {
    @FXML FlowPane containerFlowPane;
    @FXML Label username;
    @FXML Label email;
    @FXML Label verseSaved;
    @FXML Label verseReceived;

    private List<ProfileCardController> cardControllers = new ArrayList<>();

    public void setupProfile() {
        Task<Void> getProfileBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());

                // Print request for debugging
                for (String key : request.keySet()) {
                    System.out.println("Key: " + key + " | Value: " + request.get(key));
                }

                // Fetch the profile info from the backend
                JSONObject response = BackendAPI.fetch("getprofileinfo", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Fetch successful");
                    System.out.println("Response: " + response.toString(2));

                    // Extract favourite_verses array
                    JSONArray favouriteVersesArray = response.getJSONArray("favourite_verses");

                    // Process each favourite verse
                    Platform.runLater(() -> {
                        try {
                            containerFlowPane.getChildren().clear();
                            // Loop through the favourite_verses array
                            for (int i = 0; i < favouriteVersesArray.length(); i++) {
                                JSONObject favourite = favouriteVersesArray.getJSONObject(i);

                                int surahNum = favourite.getInt("surah");
                                int ayahNum = favourite.getInt("ayah");
                                String emotion = favourite.getString("emotion");
                                String theme = favourite.getString("theme");

                                // Load the card FXML
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profileCard.fxml"));
                                Parent card = loader.load();

                                // Access the card controller and pass data
                                ProfileCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setProfileInfo(surahNum, ayahNum, emotion, theme);

                                // Add the card to the VBox (containerFlowPane)
                                if (card != null) {
                                    containerFlowPane.getChildren().add(card);
                                }
                            }

                            // Set other profile info like username, email, etc.
                            username.setText(response.getString("username"));
                            email.setText(response.getString("email"));
                            verseSaved.setText(String.valueOf(response.getInt("total_saved_verse")));
                            verseReceived.setText(String.valueOf(response.getInt("total_received_verse")));

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
        new Thread(getProfileBackendAPITask).start();
    }
}
