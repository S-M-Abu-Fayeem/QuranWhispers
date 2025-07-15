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


public class AdminDuaViewController extends BaseControllerAdmin {
    @FXML
    VBox containerVBox;
    private List<AdminDuaViewCardController> cardControllers = new ArrayList<>();

    public void setupDuaViewTable() {
        if (sceneController == null) {
            System.err.println("SceneController is null in AdminDuaViewController");
        }

        Task<Void> adminDuaViewBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();

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

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminDuaViewCard.fxml"));
                                Parent card = loader.load();

                                AdminDuaViewCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setSceneController(sceneController);
                                controller.setupAdminDuaViewInfo(title, arabic_body, english_body);

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
