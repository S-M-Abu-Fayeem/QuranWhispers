package controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.AnimationUtil;
import util.GlobalState;

import java.io.IOException;
import java.util.List;

public class HomePageController extends BaseController {
    @FXML private Label heroTitle1;
    @FXML private Label heroTitle2;
    @FXML private Label heroSubtitle;
    @FXML private ImageView heroImg;

    public void setupHomePage() {
        List<Node> elements = List.of(heroTitle1, heroTitle2, heroSubtitle, heroImg);
        AnimationUtil.fadeInElements(elements, 1.0);
        AnimationUtil.startFloatingAnimation(heroImg, 0.75);
    }

    public void handleLearnMoreBtn(MouseEvent e) throws IOException {
        System.out.println("Learn More button pressed");
        playClickSound();
        LearnMoreController learnMoreController = (LearnMoreController) sceneController.switchTo(GlobalState.LEARN_MORE_FILE);
        learnMoreController.setupParent(GlobalState.HOME_PAGE_FILE);
    }


}
