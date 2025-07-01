package controller;

import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class HomePageController extends BaseController{
    public void handleHomeNavlink(MouseEvent e) throws IOException {
        System.out.println("Home navlink button pressed");
        playClickSound();
    }
    public void handleProfileNavlink(MouseEvent e) throws IOException {
        System.out.println("Profile navlink button pressed");
        playClickSound();
    }
    public void handleSearchNavlink(MouseEvent e) throws IOException {
        System.out.println("Search navlink button pressed");
        playClickSound();
    }
    public void handleFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Favourites button pressed");
        playClickSound();
    }

    public void handleNotificationBtn(MouseEvent e) throws IOException {
        System.out.println("Notification button pressed");
        playClickSound();
    }

    public void handleJoinNowBtn(MouseEvent e) throws IOException {
        System.out.println("Join Now button pressed");
        playClickSound();
    }

    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        playClickSound();
    }

    public void handleLearnMoreBtn(MouseEvent e) throws IOException {
        System.out.println("Learn More button pressed");
        playClickSound();
    }

    public void handleCopyrightText(MouseEvent e) throws IOException {
        System.out.println("Copyright pressed");
        playClickSound();
    }
}
