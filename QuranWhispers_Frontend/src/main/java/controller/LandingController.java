package controller;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import util.GlobalState;
import java.io.IOException;
import java.net.URL;

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

    @Override
    public void handleHomeNavlink(MouseEvent e) throws IOException {
        System.out.println("Home navlink button pressed");
        sceneController.switchTo(GlobalState.LANDING_FILE);
        playClickSound();
    }

    @Override
    public void handleProfileNavlink(MouseEvent e) throws IOException {
        System.out.println("Profile navlink button pressed");
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this profile page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        playClickSound();
    }

    @Override
    public void handleSearchNavlink(MouseEvent e) throws IOException {
        System.out.println("Search navlink button pressed");
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this search page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        playClickSound();
    }

    @Override
    public void handleFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Favourites button pressed");
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this favourites page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        playClickSound();
    }

    @Override
    public void handleNotificationBtn(MouseEvent e) throws IOException {
        System.out.println("Notification button pressed");
        alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this received verses page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        playClickSound();
    }
}
