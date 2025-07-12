package controller;

import javafx.scene.input.MouseEvent;
import util.GlobalState;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public abstract class BaseControllerAdmin {
    // SCENECONTROLLER SETUP
    protected SceneController sceneController;
    public void setSceneController(SceneController controller) {
        this.sceneController = controller;
    }


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
    public void handleViewNavlink(MouseEvent e) throws IOException {
        System.out.println("View Navlink Pressed");
        sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
        playClickSound();
    }
    public void handleInsertNavlink(MouseEvent e) throws IOException {
        System.out.println("Insert Navlink Pressed");
        sceneController.switchTo(GlobalState.ADMIN_INSERT_FILE);
        playClickSound();
    }
    public void handleApproveNavlink(MouseEvent e) throws IOException {
        System.out.println("Approve Navlink Pressed");
        sceneController.switchTo(GlobalState.ADMIN_APPROVE_FILE);
        playClickSound();
    }

    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
        playClickSound();
    }

    public void handleLogoutBtn(MouseEvent e) throws IOException {
        System.out.println("Logout Pressed");
        sceneController.switchTo(GlobalState.LANDING_FILE);
        playClickSound();
    }

    // FOOTER CONTROLS
    public void handleCopyrightText(MouseEvent e) throws IOException {
        System.out.println("Copyright pressed");
        try {
            Desktop.getDesktop().browse(new URI(GlobalState.COPYRIGHT_URL));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        playClickSound();
    }
}