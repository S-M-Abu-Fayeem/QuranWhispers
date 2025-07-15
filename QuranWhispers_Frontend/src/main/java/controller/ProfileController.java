package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;

import java.util.ArrayList;
import java.util.List;

public class ProfileController extends BaseController {
    @FXML FlowPane containerFlowPane;
    @FXML Label username;
    @FXML Label email;
    @FXML Label verseSaved;
    @FXML Label verseReceived;

    private List<ProfileCardController> cardControllers = new ArrayList<>();

    public void setupProfile() {
        if (sceneController == null) {
            System.err.println("SceneController is null in ProfileController");
        }

        Task<Void> getProfileBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getprofileinfo", request);
                if (response.getString("status").equals("200")) {
                    Platform.runLater(() -> {
                        try {
                            containerFlowPane.getChildren().clear();
                            JSONArray favouriteVersesArray = response.getJSONArray("favourite_verses");
                            for (int i = 0; i < favouriteVersesArray.length(); i++) {
                                JSONObject favourite = favouriteVersesArray.getJSONObject(i);
                                int surahNum = favourite.getInt("surah");
                                int ayahNum = favourite.getInt("ayah");
                                String emotion = favourite.getString("emotion");
                                String theme = favourite.getString("theme");

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profileCard.fxml"));
                                Parent card = loader.load();

                                ProfileCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setSceneController(sceneController); // Ensure SceneController is set
                                controller.setupProfileInfo(surahNum, ayahNum, emotion, theme);
                                controller.setupProfileCard();

                                if (card != null) {
                                    containerFlowPane.getChildren().add(card);
                                }
                            }
                            username.setText(response.getString("username"));
                            email.setText(response.getString("email"));
                            verseSaved.setText(String.valueOf(response.getInt("total_saved_verse")));
                            verseReceived.setText(String.valueOf(response.getInt("total_received_verse")));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(getProfileBackendAPITask).start();
    }
}
