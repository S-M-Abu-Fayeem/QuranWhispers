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

public class LandingController extends BaseController{
    @FXML private Label heroTitle1;
    @FXML private Label heroTitle2;
    @FXML private Label heroSubtitle;
    @FXML private ImageView heroImg;

    public void setupLandingPage() {
        List<Node> elements = List.of(heroTitle1, heroTitle2, heroSubtitle, heroImg);
        AnimationUtil.fadeInElements(elements, 1.0);
        AnimationUtil.startFloatingAnimation(heroImg, 0.75);
    }

    public void handleLearnMoreBtn(MouseEvent e) throws IOException {
        System.out.println("Learn More button pressed");
        playClickSound();

        LearnMoreController learnMoreController = (LearnMoreController) sceneController.switchTo(GlobalState.LEARN_MORE_FILE);
        learnMoreController.setupParent(GlobalState.LANDING_FILE);
    }


    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.LANDING_FILE);
    }

    @Override
    public void handleHomeNavlink(MouseEvent e) throws IOException {
        System.out.println("Home navlink button pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.LANDING_FILE);
    }

    @Override
    public void handleProfileNavlink(MouseEvent e) throws IOException {
        System.out.println("Profile navlink button pressed");
        playClickSound();
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this profile page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
    }

    @Override
    public void handleSearchNavlink(MouseEvent e) throws IOException {
        System.out.println("Search navlink button pressed");
        playClickSound();
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this search page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
    }

    @Override
    public void handleForumNavlink(MouseEvent e) throws IOException {
        System.out.println("Forum navlink button pressed");
        playClickSound();
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this forum page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
    }

    @Override
    public void handleFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Favourites button pressed");
        playClickSound();
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this favourites page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
    }

    @Override
    public void handleNotificationBtn(MouseEvent e) throws IOException {
        System.out.println("Notification button pressed");
        playClickSound();
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this received verses page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
    }
}
