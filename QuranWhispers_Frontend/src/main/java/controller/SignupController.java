package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import util.GlobalState;

import java.io.IOException;

public class SignupController extends BaseController{
    @FXML Label usernameField;
    @FXML Label emailAddressField;
    @FXML Label passwordField;

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
