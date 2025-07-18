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
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProfileController extends BaseController {
    @FXML FlowPane containerFlowPane;
    @FXML Label username;
    @FXML Label email;
    @FXML Label verseSaved;
    @FXML Label verseReceived;
    @FXML TextField filterField;
    @FXML Rectangle filterRectangle;
    @FXML ImageView filterImageView;
    boolean filterOn = false;

    private List<ProfileCardController> cardControllers = new ArrayList<>();

    public void handleFilterBtn(MouseEvent e) {
        System.out.println("Filter button pressed");
        playClickSound();
        filterOn = !filterOn;

        if (!filterOn) {
            filterField.clear();

            filterRectangle.setFill(new Color(0.1176, 0.1176, 0.1176, 0.8));
            URL iconURL = getClass().getResource("/images/search.png");
            if (iconURL != null) {
                filterImageView.setImage(new Image(iconURL.toExternalForm()));
            }

            setupProfile();
            return;
        }

        String filterText = filterField.getText().trim();
        if (filterText.isEmpty()) {
            System.out.println("Empty filter text");
            filterOn = !filterOn;
            return;
        }

        if (sceneController == null) {
            System.err.println("SceneController is null in ProfileController");
        }

        Task<Void> FilterProfileBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getprofileinfo", request);
                if (response.getString("status").equals("200")) {
                    Platform.runLater(() -> {
                        try {
                            containerFlowPane.getChildren().clear();
                            cardControllers.clear();

                            JSONArray favouriteVersesArray = response.getJSONArray("favourite_verses");
                            for (int i = 0; i < favouriteVersesArray.length(); i++) {
                                JSONObject favourite = favouriteVersesArray.getJSONObject(i);
                                int surahNum = favourite.getInt("surah");
                                int ayahNum = favourite.getInt("ayah");
                                String emotion = favourite.getString("emotion");
                                String theme = favourite.getString("theme");

                                if (emotion.toLowerCase().contains(filterText.toLowerCase()) ||
                                        theme.toLowerCase().contains(filterText.toLowerCase())) {

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profileCard.fxml"));
                                    Parent card = loader.load();

                                    ProfileCardController controller = loader.getController();
                                    cardControllers.add(controller);
                                    if (sceneController != null) controller.setSceneController(sceneController);
                                    controller.setupProfileInfo(surahNum, ayahNum, emotion, theme);
                                    controller.setupProfileCard();

                                    containerFlowPane.getChildren().add(card);
                                }
                            }

                            filterRectangle.setFill(Color.web("#ff6ec7", 0.8));
                            URL iconURL = getClass().getResource("/images/cross_white.png");
                            if (iconURL != null) {
                                filterImageView.setImage(new Image(iconURL.toExternalForm()));
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
        new Thread(FilterProfileBackendAPITask).start();
    }

    public void setupProfile() {
        if (sceneController == null) {
            System.err.println("SceneController is null in ProfileController");
        }

        Task<Void> GetProfileBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getprofileinfo", request);
                if (response.getString("status").equals("200")) {
                    Platform.runLater(() -> {
                        try {
                            containerFlowPane.getChildren().clear();
                            cardControllers.clear();

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
                                if (sceneController != null) controller.setSceneController(sceneController);
                                controller.setupProfileInfo(surahNum, ayahNum, emotion, theme);
                                controller.setupProfileCard();

                                containerFlowPane.getChildren().add(card);
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
        new Thread(GetProfileBackendAPITask).start();
    }
}
