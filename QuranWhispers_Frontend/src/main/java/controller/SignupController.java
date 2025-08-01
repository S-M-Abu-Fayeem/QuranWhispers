package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import util.AnimationUtil;
import util.BackendAPI;
import util.GlobalState;

import java.io.IOException;

public class SignupController extends BaseController{
    @FXML TextField usernameField;
    @FXML TextField emailAddressField;
    @FXML PasswordField passwordField;
    @FXML
    ImageView heroImg;

    public void setupSignupPage() {
        AnimationUtil.startFloatingAnimation(heroImg, 0.75);
    }

    public void handleLoginBtn(MouseEvent e) throws IOException {
        System.out.println("Login Button pressed");
        playClickSound();
        LoginController loginController = (LoginController) sceneController.switchTo(GlobalState.LOGIN_FILE);
        loginController.setupLoginPage();
    }

    public void handleContinueBtn(MouseEvent e) throws IOException {
        System.out.println("Continue button pressed");
        playClickSound();
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
                } else if (response.getString("status").equals("401")) {
                    Platform.runLater(() -> {
                        alertGenerator("Registration failed", "INVALID OPERATION", response.getString("status_message"), "error", "/images/denied.png");
                    });
                } else if (response.getString("status").equals("500")) {
                    Platform.runLater(() -> {
                        alertGenerator("Registration failed", "SERVER ERROR", response.getString("status_message"), "error", "/images/denied.png");
                    });
                } else {
                    Platform.runLater(() -> {
                        alertGenerator("Error", "UNKNOWN PROBLEM", "Something went wrong :(", "error", "/images/denied.png");
                    });
                }
                return null;
            }
        };
        new Thread(registerBackendAPITask).start();
    }

    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.LANDING_FILE);
    }
}
