package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;
import java.io.IOException;

public class LoginController extends BaseController{
    @FXML TextField emailAddressField;
    @FXML PasswordField passwordField;

    public void handleSignupBtn(MouseEvent e) throws IOException {
        System.out.println("SignUp Button pressed");
        sceneController.switchTo(GlobalState.SIGNUP_FILE);
        playClickSound();
    }

    public void handleContinueBtn(MouseEvent e) throws IOException {
        System.out.println("Continue button pressed");

        Task<Void> loginBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("email", emailAddressField.getText());
                request.put("password", passwordField.getText());


                JSONObject response = BackendAPI.fetch("login", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Login successful");
                    Platform.runLater(() -> {
                        try {
                            emailAddressField.clear();
                            passwordField.clear();
                            if (response.getString("admin").equals("true")) {
                                AdminUserViewController adminUserViewController = (AdminUserViewController) sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
                                adminUserViewController.setupUserViewTable();
                            } else {
                                sceneController.switchTo(GlobalState.HOME_PAGE_FILE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    System.out.println("Login failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(loginBackendAPITask).start();
        playClickSound();
    }

    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        sceneController.switchTo(GlobalState.LANDING_FILE);
        playClickSound();
    }

}
