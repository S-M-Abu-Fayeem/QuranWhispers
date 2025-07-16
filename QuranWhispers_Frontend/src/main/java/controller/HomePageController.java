package controller;

import javafx.scene.input.MouseEvent;
import util.GlobalState;
import java.io.IOException;

public class HomePageController extends BaseController{
    public void handleLearnMoreBtn(MouseEvent e) throws IOException {
        System.out.println("Learn More button pressed");
        playClickSound();
        LearnMoreController learnMoreController = (LearnMoreController) sceneController.switchTo(GlobalState.LEARN_MORE_FILE);
        learnMoreController.setupParent(GlobalState.HOME_PAGE_FILE);
    }
}
