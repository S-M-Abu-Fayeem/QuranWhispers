package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;

import java.util.ArrayList;
import java.util.List;

public class AdminApproveController extends BaseControllerAdmin {
    @FXML VBox containerVBox;
    private List<AdminApproveCardController> cardControllers = new ArrayList<>();

    public void stopAllOtherCards(AdminApproveCardController current) {
        for (AdminApproveCardController controller : cardControllers) {
            if (controller != current && controller.isPlaying()) {
                controller.stopAudio();
            }
        }
    }

    public void setupApproveTable() {
        Task<Void> getPendingRecitationsTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getpendingrecitations", request);

                if (response != null && response.getString("status").equals("200")) {
                    System.out.println(response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerVBox.getChildren().clear();
                            JSONArray recitations = response.getJSONArray("pendingrecitations");
                            for (int i = 0; i < recitations.length(); i++) {
                                JSONObject rec = recitations.getJSONObject(i);
                                String surah = rec.getString("surah");
                                String ayah = rec.getString("ayah");
                                String username = rec.getString("username");
                                String reciterName = rec.getString("reciter_name");

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminApproveCard.fxml"));
                                Parent card = loader.load();

                                AdminApproveCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setSceneController(sceneController);
                                controller.setupAdminApproveInfo(surah, ayah, username, reciterName);
                                controller.setupParentController(AdminApproveController.this);

                                if (card != null) {
                                    containerVBox.getChildren().add(card);
                                    System.out.println("✅ Added card for: " + reciterName + " [" + surah + ":" + ayah + "]");
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.out.println("❌ Error setting up recitation cards: " + ex.getMessage());
                        }
                    });
                } else {
                    System.out.println("❌ Failed to fetch recitations or status not 200");
                }
                return null;
            }
        };

        new Thread(getPendingRecitationsTask).start();
    }
}
