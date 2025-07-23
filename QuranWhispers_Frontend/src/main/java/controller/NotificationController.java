package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

public class NotificationController extends BaseController {
    @FXML FlowPane containerFlowPane;
    @FXML TextField filterField;
    @FXML Rectangle filterRectangle;
    @FXML ImageView filterImageView;
    boolean filterOn = false;

    private List<NotificationCardController> cardControllers = new ArrayList<>();

    public void handleFilterBtn(MouseEvent e) {
        System.out.println("Filter button pressed");
        playClickSound();
        filterOn = !filterOn;
        filterField.setEditable(!filterOn);

        if (!filterOn) {
            filterField.clear();

            filterRectangle.setFill(new Color(0.1176, 0.1176, 0.1176, 0.8));
            URL iconURL = getClass().getResource("/images/search.png");
            if (iconURL != null) {
                filterImageView.setImage(new Image(iconURL.toExternalForm()));
            }

            setupNotification();
            return;
        }

        String filterText = filterField.getText().trim();
        if (filterText.isEmpty()) {
            System.out.println("Empty filter text");
            filterOn = !filterOn;
            filterField.setEditable(!filterOn);
            return;
        }

        if (sceneController == null) {
            System.err.println("SceneController is null in NotificationController");
        }

        Task<Void> FilterNotificationBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getreceivedinfo", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Received notification data successfully" + response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerFlowPane.getChildren().clear();
                            cardControllers.clear();

                            JSONArray receivedVersesArray = response.getJSONArray("received_verses");
                            for (int i = 0; i < receivedVersesArray.length(); i++) {
                                JSONObject received = receivedVersesArray.getJSONObject(i);
                                int surahNum = received.getInt("surah");
                                int ayahNum = received.getInt("ayah");
                                String emotion = received.getString("emotion");
                                String theme = received.getString("theme");
                                String sender = received.getString("sender_username");

                                if (emotion.toLowerCase().contains(filterText.toLowerCase()) ||
                                        theme.toLowerCase().contains(filterText.toLowerCase()) ||
                                sender.toLowerCase().contains(filterText.toLowerCase())) {

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notificationCard.fxml"));
                                    Parent card = loader.load();

                                    NotificationCardController controller = loader.getController();
                                    cardControllers.add(controller);
                                    if (sceneController != null) controller.setSceneController(sceneController); // Ensure SceneController is set
                                    controller.setupNotificationInfo(surahNum, ayahNum, emotion, theme, sender);
                                    controller.setupNotificationCard();
                                    containerFlowPane.getChildren().add(card);
                                }
                            }

                            filterRectangle.setFill(Color.web("#ff6ec7", 0.8));
                            URL iconURL = getClass().getResource("/images/cross_white.png");
                            if (iconURL != null) {
                                filterImageView.setImage(new Image(iconURL.toExternalForm()));
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(FilterNotificationBackendAPITask).start();
    }

    public void setupNotification() {
        if (sceneController == null) {
            System.err.println("SceneController is null in NotificationController");
        }

        Task<Void> GetNotificationBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getreceivedinfo", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Received notification data successfully" + response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerFlowPane.getChildren().clear();
                            cardControllers.clear();

                            JSONArray receivedVersesArray = response.getJSONArray("received_verses");
                            for (int i = 0; i < receivedVersesArray.length(); i++) {
                                JSONObject received = receivedVersesArray.getJSONObject(i);
                                int surahNum = received.getInt("surah");
                                int ayahNum = received.getInt("ayah");
                                String emotion = received.getString("emotion");
                                String theme = received.getString("theme");
                                String sender = received.getString("sender_username");

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notificationCard.fxml"));
                                Parent card = loader.load();

                                NotificationCardController controller = loader.getController();
                                cardControllers.add(controller);
                                if (sceneController != null) controller.setSceneController(sceneController); // Ensure SceneController is set
                                controller.setupNotificationInfo(surahNum, ayahNum, emotion, theme, sender);
                                controller.setupNotificationCard();
                                containerFlowPane.getChildren().add(card);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(GetNotificationBackendAPITask).start();
    }
}
