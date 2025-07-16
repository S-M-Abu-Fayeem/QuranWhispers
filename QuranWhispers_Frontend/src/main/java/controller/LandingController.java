package controller;

import javafx.scene.input.MouseEvent;
import util.GlobalState;
import java.io.IOException;

public class LandingController extends BaseController{

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
