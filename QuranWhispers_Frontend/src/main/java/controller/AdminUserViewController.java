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

public class AdminUserViewController extends BaseControllerAdmin {
    @FXML
    VBox containerVBox;
    private List<AdminUserViewCardController> cardControllers = new ArrayList<>();


    public void setupUserViewTable() {
        if (sceneController == null) {
            System.err.println("SceneController is null in AdminUserViewController");
        }

        // Proceed with the rest of your setup
        Task<Void> getAdminUserViewBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", SessionManager.getEmail());
                request.put("token", SessionManager.getToken());


                JSONObject response = BackendAPI.fetch("getallusers", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Received notification data successfully" + response.toString());
                    Platform.runLater(() -> {
                        try {
                            containerVBox.getChildren().clear();
                            JSONArray adminUserViewArray = response.getJSONArray("users");
                            for (int i = 0; i < adminUserViewArray.length(); i++) {
                                JSONObject adminUserView = adminUserViewArray.getJSONObject(i);
                                String username = adminUserView.getString("username");
                                String email = adminUserView.getString("email");
                                String savedVerse = String.valueOf(adminUserView.getInt("total_saved_verse"));
                                String receivedVerse = String.valueOf(adminUserView.getInt("total_received_verse"));

                                // Load the card FXML
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adminUserViewCard.fxml"));
                                Parent card = loader.load();

                                // Access the card controller and pass data
                                AdminUserViewCardController controller = loader.getController();
                                cardControllers.add(controller);
                                controller.setSceneController(sceneController);
                                controller.setupAdminUserViewInfo(username, email, savedVerse, receivedVerse);

                                // Add to VBox
                                if (card != null) {
                                    containerVBox.getChildren().add(card);
                                    System.out.println("Added card for user: " + username);
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
        new Thread(getAdminUserViewBackendAPITask).start();
    }
}
