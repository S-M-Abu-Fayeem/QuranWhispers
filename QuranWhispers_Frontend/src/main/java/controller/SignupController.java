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

public class SignupController extends BaseController{
    @FXML TextField usernameField;
    @FXML TextField emailAddressField;
    @FXML PasswordField passwordField;

    public void handleLoginBtn(MouseEvent e) throws IOException {
        System.out.println("Login Button pressed");
        sceneController.switchTo(GlobalState.LOGIN_FILE);
        playClickSound();
    }

    public void handleContinueBtn(MouseEvent e) throws IOException {
        System.out.println("Continue button pressed");
        Task<Void> registerBackendAPITask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                System.out.println("Username: " + usernameField.getText());
                System.out.println("password: " + passwordField.getText());
                System.out.println("emailAddress: " + emailAddressField.getText());
                request.put("username", usernameField.getText());
                request.put("email", emailAddressField.getText());
                request.put("password", passwordField.getText());

                JSONObject response = BackendAPI.fetch("register", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Registration successful");
                    usernameField.clear();
                    emailAddressField.clear();
                    passwordField.clear();
                    Platform.runLater(() -> {
                        try {
                            usernameField.clear();
                            emailAddressField.clear();
                            passwordField.clear();
                            sceneController.switchTo(GlobalState.LOGIN_FILE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    System.out.println("Registration failed: " + response.getString("message"));
                }
                return null;
            }
        };
        new Thread(registerBackendAPITask).start();
        playClickSound();
    }

    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        sceneController.switchTo(GlobalState.LANDING_FILE);
        playClickSound();
    }
}
