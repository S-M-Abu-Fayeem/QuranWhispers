package controller;

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

    // ADD MENUBAR NAVIGATION METHODS AND LOGICS HERE
    public void moveToEventHandling() {
        sceneController.switchTo(GlobalState.EVENT_HANDLING_FILE);
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
}