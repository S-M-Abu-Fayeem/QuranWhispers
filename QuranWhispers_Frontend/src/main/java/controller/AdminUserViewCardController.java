package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;

import java.io.IOException;

public class AdminUserViewCardController extends BaseControllerAdmin {
    @FXML Label usernameField;
    @FXML Label emailField;
    @FXML Label savedVerseField;
    @FXML Label receivedVerseField;
    String userEmail;

    public void setupAdminUserViewInfo(String username, String email, String savedVerse, String receivedVerse) {
        usernameField.setText(username);
        emailField.setText(email);
        savedVerseField.setText(savedVerse);
        receivedVerseField.setText(receivedVerse);
        userEmail = email;
    }

    public void handleDeleteBtn(MouseEvent e) throws IOException {
        System.out.println("Delete Btn Pressed");
        Task<Void> deleteUserBackendAPITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("useremail", userEmail);

                JSONObject response = BackendAPI.fetch("deleteuser", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("User Deleted successfully: " + response.toString());
                    Platform.runLater(() -> {
                        try {
                            if (sceneController != null) {
                                AdminUserViewController adminUserViewController = (AdminUserViewController) sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
                                adminUserViewController.setupUserViewTable();
                            } else {
                                System.err.println("sceneController is null in AdminUserViewCardController.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(deleteUserBackendAPITask).start();
        playClickSound();
    }
}
