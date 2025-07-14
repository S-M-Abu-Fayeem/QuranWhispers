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


public class AdminDuaViewController extends BaseControllerAdmin {
    @FXML
    VBox containerVBox;
    private List<AdminDuaViewCardController> cardControllers = new ArrayList<>();

    public void setupDuaViewTable() {
        if (sceneController == null) {
            System.err.println("SceneController is null in AdminDuaViewController");
        }

        // Proceed with the rest of your setup
        Task<Void> adminDuaViewBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());


                JSONObject response = BackendAPI.fetch("getallduas", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Received dua data successfully" + response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerVBox.getChildren().clear();
                            JSONArray adminDuaViewArray = response.getJSONArray("duas");

                            for (int i = 0; i < adminDuaViewArray.length(); i++) {
                                JSONObject adminDuaView = adminDuaViewArray.getJSONObject(i);
                                String title = adminDuaView.getString("title");
                                String arabic_body = adminDuaView.getString("body_arabic");
                                String english_body = adminDuaView.getString("body_english");

                                // Load the card FXML
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminDuaViewCard.fxml"));
                                Parent card = loader.load();

                                // Access the card controller and pass data
                                AdminDuaViewCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setSceneController(sceneController);
                                controller.setupAdminDuaViewInfo(title, arabic_body, english_body);

                                // Add to VBox
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
        new Thread(adminDuaViewBackendAPITask).start();
    }
}
