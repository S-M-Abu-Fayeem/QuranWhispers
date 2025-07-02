package controller;

import javafx.scene.input.MouseEvent;
import util.GlobalState;

import java.io.IOException;

public class LandingController extends BaseController{
    public void handleLearnMoreBtn(MouseEvent e) throws IOException {
        System.out.println("Learn More button pressed");
        playClickSound();
    }

    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        sceneController.switchTo(GlobalState.LANDING_FILE);
        playClickSound();
    }
}
