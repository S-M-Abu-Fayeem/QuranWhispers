package controller;

import javafx.animation.Animation;
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

public class LoginController extends BaseController{
    @FXML TextField emailAddressField;
    @FXML PasswordField passwordField;
    @FXML ImageView heroImg;

    public void setupLoginPage() {
        AnimationUtil.startFloatingAnimation(heroImg, 0.75);
    }

    public void handleSignupBtn(MouseEvent e) throws IOException {
        System.out.println("SignUp Button pressed");
        playClickSound();
        SignupController signupController = (SignupController) sceneController.switchTo(GlobalState.SIGNUP_FILE);
        signupController.setupSignupPage();
    }

    public void handleContinueBtn(MouseEvent e) throws IOException {
        System.out.println("Continue button pressed");
        playClickSound();

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
                                HomePageController homePageController = (HomePageController) sceneController.switchTo(GlobalState.HOME_PAGE_FILE);
                                homePageController.setupHomePage();
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
    }

    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.LANDING_FILE);
    }

}
