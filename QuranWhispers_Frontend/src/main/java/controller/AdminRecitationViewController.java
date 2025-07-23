package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminRecitationViewController extends BaseControllerAdmin {
    @FXML VBox containerVBox;
    private List<AdminRecitationViewCardController> cardControllers = new ArrayList<>();

    public void stopAllOtherCards(AdminRecitationViewCardController current) {
        for (AdminRecitationViewCardController controller : cardControllers) {
            if (controller != current && controller.isPlaying()) {
                controller.stopAudio();
            }
        }
    }

    public void setupRecitationViewTable() {
        Task<Void> getApprovedRecitationsTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

                JSONObject response = BackendAPI.fetch("getapprovedrecitations", request);

                if (response != null && response.getString("status").equals("200")) {
                    System.out.println(response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerVBox.getChildren().clear();
                            JSONArray recitations = response.getJSONArray("approvedrecitations");
                            for (int i = 0; i < recitations.length(); i++) {
                                JSONObject rec = recitations.getJSONObject(i);
                                String surah = rec.getString("surah");
                                String ayah = rec.getString("ayah");
                                String reciterName = rec.getString("reciter_name");

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminRecitationViewCard.fxml"));
                                Parent card = loader.load();

                                AdminRecitationViewCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setSceneController(sceneController);
                                controller.setupAdminRecitationViewInfo(surah, ayah, reciterName);
                                controller.setupParentController(AdminRecitationViewController.this);

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

        new Thread(getApprovedRecitationsTask).start();
    }
}
