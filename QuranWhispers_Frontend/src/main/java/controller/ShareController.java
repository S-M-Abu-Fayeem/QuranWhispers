package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import util.GlobalState;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ShareController extends SearchController implements Initializable{

    @FXML
    TextField receiverUsername;


    public void handleCloseBtn(MouseEvent e) throws IOException {
        System.out.println("Close Button Pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.SEARCH_FILE);
    }

    public void handleSendBtn(MouseEvent e) throws IOException {
        System.out.println("Send Button Pressed");
        playClickSound();
    }
}
