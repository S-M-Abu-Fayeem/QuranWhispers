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
import util.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class AdminVerseViewController extends BaseControllerAdmin {
    @FXML
    VBox containerVBox;
    private List<AdminVerseViewCardController> cardControllers = new ArrayList<>();


    public void setupVerseViewTable() {
        if (sceneController == null) {
            System.err.println("SceneController is null in AdminUserViewController");
        }

        // Proceed with the rest of your setup
        Task<Void> getAdminVerseViewBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());


                JSONObject response = BackendAPI.fetch("getallverses", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Received notification data successfully" + response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerVBox.getChildren().clear();
                            JSONArray adminVerseViewArray = response.getJSONArray("verses");

                            for (int i = 0; i < adminVerseViewArray.length(); i++) {
                                JSONObject adminVerseView = adminVerseViewArray.getJSONObject(i);
                                String surah = adminVerseView.getString("surah");
                                String ayah = adminVerseView.getString("ayah");
                                String emotion = adminVerseView.getString("emotion");
                                String theme = adminVerseView.getString("theme");

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminVerseViewCard.fxml"));
                                Parent card = loader.load();

                                AdminVerseViewCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setSceneController(sceneController);
                                controller.setupAdminVerseViewInfo(surah, ayah, emotion, theme);

                                if (card != null) {
                                    containerVBox.getChildren().add(card);
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
        new Thread(getAdminVerseViewBackendAPITask).start();
    }
}
