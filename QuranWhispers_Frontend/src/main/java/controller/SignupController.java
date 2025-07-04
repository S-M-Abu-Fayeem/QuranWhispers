package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import util.GlobalState;

import java.io.IOException;

public class SignupController extends BaseController{
    @FXML TextField usernameField;
    @FXML TextField emailAddressField;
    @FXML TextField passwordField;

    public void handleLoginBtn(MouseEvent e) throws IOException {
        System.out.println("Login Button pressed");
        sceneController.switchTo(GlobalState.LOGIN_FILE);
        playClickSound();
    }

    public void handleContinueBtn(MouseEvent e) throws IOException {
        System.out.println("Continue button pressed");
        sceneController.switchTo(GlobalState.HOME_PAGE_FILE);
        playClickSound();
    }
}
