package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ShareController extends BaseController {

    @FXML
    TextField receiverUsername;
    public void handleCloseBtn(MouseEvent e) throws IOException {
        System.out.println("Close Button Pressed");
        playClickSound();
    }

    public void handleSendBtn(MouseEvent e) throws IOException {
        System.out.println("Send Button Pressed");
        playClickSound();
    }
}
