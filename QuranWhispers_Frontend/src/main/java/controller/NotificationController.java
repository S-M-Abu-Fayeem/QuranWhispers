package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;
import java.util.ArrayList;
import java.util.List;

public class NotificationController extends BaseController {
    @FXML FlowPane containerFlowPane;
    private List<NotificationCardController> cardControllers = new ArrayList<>();

    public void setupNotification() {
        if (sceneController == null) {
            System.err.println("SceneController is null in NotificationController");
        }

        Task<Void> getNotificationBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getreceivedinfo", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Received notification data successfully" + response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerFlowPane.getChildren().clear();
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
                                controller.setSceneController(sceneController); // Ensure SceneController is set
                                controller.setupNotificationInfo(surahNum, ayahNum, emotion, theme, sender);
                                controller.setupNotificationCard();

                                if (card != null) {
                                    containerFlowPane.getChildren().add(card);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(getNotificationBackendAPITask).start();
    }
}
