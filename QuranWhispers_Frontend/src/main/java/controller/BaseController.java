package controller;

import javafx.scene.input.MouseEvent;
import util.GlobalState;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;


public abstract class BaseController {
    // SCENECONTROLLER SETUP
    protected SceneController sceneController;
    public void setSceneController(SceneController controller) {
        this.sceneController = controller;
    } // Called in the MainApp


    // Method to play the sound
    public static void playClickSound() {
        try {
            File soundFile = new File("src/main/resources/sounds/click.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // NAVBAR CONTROLS
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
        sceneController.switchTo(GlobalState.LOGIN_FILE);
        playClickSound();
    }

    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        sceneController.switchTo(GlobalState.HOME_PAGE_FILE);
        playClickSound();
    }

    // FOOTER CONTROLS
    public void handleCopyrightText(MouseEvent e) throws IOException {
        System.out.println("Copyright pressed");
        playClickSound();
    }
}