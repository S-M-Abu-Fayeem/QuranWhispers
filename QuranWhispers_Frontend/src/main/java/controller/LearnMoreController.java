package controller;

import javafx.scene.input.MouseEvent;
import util.GlobalState;
import java.io.IOException;

public class LearnMoreController extends BaseController {
    String parent;
    public void setupParent(String parent) {
        this.parent = parent;
    }

    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        if (parent != null && parent.equals(GlobalState.LANDING_FILE)) {
            System.out.println("Title pressed");
            playClickSound();
            sceneController.switchTo(GlobalState.LANDING_FILE);
        } else {
            super.handleTitleLink(e);
        }
    }

    @Override
    public void handleHomeNavlink(MouseEvent e) throws IOException {
        if (parent != null && parent.equals(GlobalState.LANDING_FILE)) {
            System.out.println("Home navlink button pressed");
            playClickSound();
            sceneController.switchTo(GlobalState.LANDING_FILE);
        } else {
            super.handleHomeNavlink(e);
        }
    }

    @Override
    public void handleProfileNavlink(MouseEvent e) throws IOException {
        if (parent != null && parent.equals(GlobalState.LANDING_FILE)) {
            System.out.println("Profile navlink button pressed");
            playClickSound();
            alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this profile page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        } else {
            super.handleProfileNavlink(e);
        }
    }

    @Override
    public void handleSearchNavlink(MouseEvent e) throws IOException {
        if (parent != null && parent.equals(GlobalState.LANDING_FILE)) {
            System.out.println("Search navlink button pressed");
            playClickSound();
            alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this search page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        } else {
            super.handleSearchNavlink(e);
        }
    }

    @Override
    public void handleForumNavlink(MouseEvent e) throws IOException {
        if (parent != null && parent.equals(GlobalState.LANDING_FILE)) {
            System.out.println("Forum navlink button pressed");
            playClickSound();
            alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this forum page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        } else {
            super.handleForumNavlink(e);
        }
    }

    @Override
    public void handleFavouritesBtn(MouseEvent e) throws IOException {
        if (parent != null && parent.equals(GlobalState.LANDING_FILE)) {
            System.out.println("Favourites button pressed");
            playClickSound();
            alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this favourites page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        } else {
            super.handleFavouritesBtn(e);
        }
    }

    @Override
    public void handleNotificationBtn(MouseEvent e) throws IOException {
        if (parent != null && parent.equals(GlobalState.LANDING_FILE)) {
            System.out.println("Notification button pressed");
            playClickSound();
            alertGenerator("Access Denied", "YOU NEED TO LOG IN", "Please log in first using the JOIN NOW button to access this received verses page. If you don't have an account, feel free to sign up!", "error", "/images/denied.png");
        } else {
            super.handleNotificationBtn(e);
        }
    }
}
