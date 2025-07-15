package controller;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import util.GlobalState;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public abstract class BaseControllerAdmin {
    // SCENECONTROLLER SETUP
    protected SceneController sceneController;
    public void setSceneController(SceneController controller) {
        this.sceneController = controller;
    }


    // METHOD TO PLAY THE CLICK SOUND
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

    // VIEW SIDEBAR CONTROLS
    public void handleUserViewBtn(MouseEvent e) throws IOException {
        System.out.println("User View Button Pressed");
        AdminUserViewController adminUserViewController = (AdminUserViewController) sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
        adminUserViewController.setupUserViewTable();
        playClickSound();
    }

    public void handleVerseViewBtn(MouseEvent e) throws IOException {
        System.out.println("Quran View Button Pressed");
        AdminVerseViewController adminVerseViewController = (AdminVerseViewController) sceneController.switchTo(GlobalState.ADMIN_VERSE_VIEW_FILE);
        adminVerseViewController.setupVerseViewTable();
        playClickSound();
    }

    public void handleDuaViewBtn(MouseEvent e) throws IOException {
        System.out.println("Dua View Button Pressed");
        AdminDuaViewController adminDuaViewController = (AdminDuaViewController) sceneController.switchTo(GlobalState.ADMIN_DUA_VIEW_FILE);
        adminDuaViewController.setupDuaViewTable();
        playClickSound();
    }

    public void handleRecitationViewBtn(MouseEvent e) throws IOException {
        System.out.println("Recitation View Button Pressed");
        sceneController.switchTo(GlobalState.ADMIN_RECITATION_VIEW_FILE);
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