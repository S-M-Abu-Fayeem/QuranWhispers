package controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import util.GlobalState;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LearnMoreController extends BaseController {
    String parent;
    @FXML Label actionTitle;

    public void setupParent(String parent) {
        this.parent = parent;
        if (parent != null && parent.equals(GlobalState.HOME_PAGE_FILE)) {
            actionTitle.setText("Logout");
        } else {
            actionTitle.setText("Join Now");
        }

    }

    public void handleShawkiContact(MouseEvent e) throws IOException {
        System.out.println("Copyright pressed");
        playClickSound();
        try {
            Desktop.getDesktop().browse(new URI(GlobalState.SHAWKI_CONTACT_URL));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public void handleFayeemContact(MouseEvent e) throws IOException {
        System.out.println("Copyright pressed");
        playClickSound();
        try {
            Desktop.getDesktop().browse(new URI(GlobalState.FAYEEM_CONTACT_URL));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public void handleActionBtn(MouseEvent e) throws IOException {
        System.out.println("Action button pressed");
        if (parent != null && parent.equals(GlobalState.HOME_PAGE_FILE)) {
            super.handleLogoutBtn(e);
        } else {
           super.handleJoinNowBtn(e);
        }
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
