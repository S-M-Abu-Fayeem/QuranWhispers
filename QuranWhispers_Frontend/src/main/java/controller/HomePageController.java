package controller;

import javafx.scene.input.MouseEvent;
import util.GlobalState;
import java.io.IOException;

public class HomePageController extends BaseController{
    public void handleLearnMoreBtn(MouseEvent e) throws IOException {
        System.out.println("Learn More button pressed");

        sceneController.switchTo(GlobalState.ADMIN_INSERT_FILE);
        playClickSound();
    }
}
