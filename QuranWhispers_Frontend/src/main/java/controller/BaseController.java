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


public abstract class BaseController {
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
    public void handleHomeNavlink(MouseEvent e) throws IOException {
        System.out.println("Home navlink button pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.HOME_PAGE_FILE);
    }
    public void handleProfileNavlink(MouseEvent e) throws IOException {
        System.out.println("Profile navlink button pressed");
        playClickSound();
        ProfileController profileController = (ProfileController) sceneController.switchTo(GlobalState.PROFILE_FILE);
        profileController.setupProfile();
    }
    public void handleSearchNavlink(MouseEvent e) throws IOException {
        System.out.println("Search navlink button pressed");
        playClickSound();
        SearchController searchController = (SearchController) sceneController.switchTo(GlobalState.SEARCH_FILE);
        searchController.setupListView();
        searchController.setupDua();
    }

    public void handleForumNavlink(MouseEvent e) throws IOException {
        System.out.println("Forum navlink button pressed");
        playClickSound();
        ForumController forumController = (ForumController) sceneController.switchTo(GlobalState.FORUM_FILE);
        forumController.setupForum();
    }

    public void handleFavouritesBtn(MouseEvent e) throws IOException {
        System.out.println("Favourites button pressed");
        playClickSound();
        ProfileController profileController = (ProfileController) sceneController.switchTo(GlobalState.PROFILE_FILE);
        profileController.setupProfile();
    }

    public void handleNotificationBtn(MouseEvent e) throws IOException {
        System.out.println("Notification button pressed");
        playClickSound();
        NotificationController notificationController = (NotificationController) sceneController.switchTo(GlobalState.NOTIFICATION_FILE);
        notificationController.setupNotification();
    }

    public void handleJoinNowBtn(MouseEvent e) throws IOException {
        System.out.println("Join Now button pressed");
        playClickSound();
        LoginController loginController = (LoginController) sceneController.switchTo(GlobalState.LOGIN_FILE);
        loginController.setupLoginPage();
    }

    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        playClickSound();
        sceneController.switchTo(GlobalState.HOME_PAGE_FILE);
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
}