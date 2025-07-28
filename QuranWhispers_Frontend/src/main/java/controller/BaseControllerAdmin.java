package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;
import util.SessionManager;

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
        playClickSound();
        System.out.println("View Navlink Pressed");
        sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
    }
    public void handleInsertNavlink(MouseEvent e) throws IOException {
        playClickSound();
        System.out.println("Insert Navlink Pressed");
        sceneController.switchTo(GlobalState.ADMIN_INSERT_FILE);
    }
    public void handleApproveNavlink(MouseEvent e) throws IOException {
        playClickSound();
        System.out.println("Approve Navlink Pressed");
        AdminApproveController adminApproveController = (AdminApproveController) sceneController.switchTo(GlobalState.ADMIN_APPROVE_FILE);
        adminApproveController.setupApproveTable();
    }

    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
    }

    public void handleLogoutBtn(MouseEvent e) throws IOException {
        System.out.println("Logout Pressed");
        playClickSound();
        JSONObject request = new JSONObject();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = BackendAPI.fetch("logout", request);
                if (response.getString("status").equals("200")) {
                    System.out.println("Logout Successful");
                    Platform.runLater(() -> {
                        try {
                            SessionManager.clearSession();
                            LandingController landingController = (LandingController) sceneController.switchTo(GlobalState.LANDING_FILE);
                            landingController.setupLandingPage();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    // VIEW SIDEBAR CONTROLS
    public void handleUserViewBtn(MouseEvent e) throws IOException {
        System.out.println("User View Button Pressed");
        playClickSound();
        AdminUserViewController adminUserViewController = (AdminUserViewController) sceneController.switchTo(GlobalState.ADMIN_USER_VIEW_FILE);
        adminUserViewController.setupUserViewTable();
    }

    public void handleVerseViewBtn(MouseEvent e) throws IOException {
        System.out.println("Quran View Button Pressed");
        playClickSound();
        AdminVerseViewController adminVerseViewController = (AdminVerseViewController) sceneController.switchTo(GlobalState.ADMIN_VERSE_VIEW_FILE);
        adminVerseViewController.setupVerseViewTable();
    }

    public void handleDuaViewBtn(MouseEvent e) throws IOException {
        System.out.println("Dua View Button Pressed");
        playClickSound();
        AdminDuaViewController adminDuaViewController = (AdminDuaViewController) sceneController.switchTo(GlobalState.ADMIN_DUA_VIEW_FILE);
        adminDuaViewController.setupDuaViewTable();
    }

    public void handleRecitationViewBtn(MouseEvent e) throws IOException {
        System.out.println("Recitation View Button Pressed");
        playClickSound();
        AdminRecitationViewController adminRecitationViewController = (AdminRecitationViewController) sceneController.switchTo(GlobalState.ADMIN_RECITATION_VIEW_FILE);
        adminRecitationViewController.setupRecitationViewTable();
    }

    public void handleForumNavlink(MouseEvent e) throws IOException {
        System.out.println("Forum navlink button pressed");
        playClickSound();
        AdminForumController adminForumController = (AdminForumController) sceneController.switchTo(GlobalState.ADMIN_FORUM_FILE);
        adminForumController.setupAdminForum();
    }

    // ALERT TOASTER GENERATE
    public ButtonType alertGenerator(String title, String header, String content, String type, String iconPath) {
        Alert alert;
        if (type.equals("error")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else if (type.equals("warning")) {
            alert = new Alert(Alert.AlertType.WARNING);
        } else if (type.equals("confirmation")) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }

        alert.setTitle(title);

        if (iconPath != null && !iconPath.isEmpty()) {
            URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                alert.getDialogPane().setGraphic(new ImageView(new Image(iconURL.toExternalForm())));
            }
        }

        alert.getDialogPane().setStyle("-fx-font-family: 'Century Gothic'; -fx-font-size: 14px; -fx-text-fill: #000000;");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(400);
        alert.getDialogPane().setMaxHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMaxWidth(600);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().get();
    }



    // FOOTER CONTROLS
    public void handleCopyrightText(MouseEvent e) throws IOException {
        System.out.println("Copyright pressed");
        playClickSound();
        try {
            Desktop.getDesktop().browse(new URI(GlobalState.COPYRIGHT_URL));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }


    // HELPER METHOD
    public static String toTitleCase(String sentence) {
        String[] words = sentence.split(" ");
        StringBuilder titleCasedSentence = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize the first letter and make the rest lowercase
                titleCasedSentence.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return titleCasedSentence.toString().trim();
    }
}