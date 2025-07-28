package quranwhispers_frontend;

import controller.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import util.GlobalState;
import java.net.URL;

// IMPORT ALL CONTROLLERS HERE
import controller.LandingController;
import controller.LearnMoreController;
import controller.LoginController;
import controller.SignupController;
import controller.HomePageController;
import controller.SearchController;
import controller.ShareController;
import controller.GenerateAIController;
import controller.RecitationController;
import controller.NotificationController;
import controller.NotificationCardController;
import controller.ProfileController;
import controller.ProfileCardController;
import controller.GlobalShareController;
import controller.GlobalRecitationController;
import controller.GlobalRecitationCardController;
import controller.AdminInsertController;
import controller.AdminApproveController;
import controller.AdminApproveCardController;
import controller.AdminUserViewController;
import controller.AdminUserViewCardController;
import controller.AdminVerseViewController;
import controller.AdminVerseViewCardController;
import controller.AdminDuaViewController;
import controller.AdminDuaViewCardController;
import controller.AdminRecitationViewController;
import controller.AdminRecitationViewCardController;
import controller.ForumCardController;
import controller.ForumCardVerseController;
import controller.ForumController;
import controller.AdminForumController;


public class MainApp extends Application {
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;


    public static void main(String[] args) {
        launch(args);
    }

    // CONTROLLER INITIALIZATION METHOD - DO NOT MODIFY
    private <T> void initController(String resourceFile, Class<T> controllerClass, SceneController sceneController) {
        try {
            URL location = getClass().getResource("/fxml/" + resourceFile + ".fxml");
            if (location == null) {
                throw new RuntimeException("FXML not found: " + resourceFile);
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controllerClass.isInstance(controller)) {
                controllerClass
                        .getMethod("setSceneController", SceneController.class)
                        .invoke(controller, sceneController);
            } else {
                throw new RuntimeException("Controller type mismatch for: " + resourceFile);
            }

            sceneController.addScene(resourceFile, root, controller);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing controller for " + resourceFile, e);
        }
    }



    @Override
    public void start(Stage stage) throws Exception {
        SceneController sceneController = new SceneController(stage);

        // REGISTER CONTROLLERS
        initController(GlobalState.LANDING_FILE, LandingController.class, sceneController);
        initController(GlobalState.LEARN_MORE_FILE, LearnMoreController.class, sceneController);
        initController(GlobalState.HOME_PAGE_FILE, HomePageController.class, sceneController);
        initController(GlobalState.LOGIN_FILE, LoginController.class, sceneController);
        initController(GlobalState.SIGNUP_FILE, SignupController.class, sceneController);
        initController(GlobalState.SEARCH_FILE, SearchController.class, sceneController);
        initController(GlobalState.SHARE_FILE, ShareController.class, sceneController);
        initController(GlobalState.GENERATE_AI_FILE, GenerateAIController.class, sceneController);
        initController(GlobalState.RECITATION_FILE, RecitationController.class, sceneController);
        initController(GlobalState.NOTIFICATION_FILE, NotificationController.class, sceneController);
        initController(GlobalState.NOTIFICATION_CARD_FILE, NotificationCardController.class, sceneController);
        initController(GlobalState.PROFILE_FILE, ProfileController.class, sceneController);
        initController(GlobalState.PROFILE_CARD_FILE, ProfileCardController.class, sceneController);
        initController(GlobalState.GLOBAL_SHARE_FILE, GlobalShareController.class, sceneController);
        initController(GlobalState.GLOBAL_RECITATION_FILE, GlobalRecitationController.class, sceneController);
        initController(GlobalState.GLOBAL_RECITATION_CARD_FILE, GlobalRecitationCardController.class, sceneController);
        initController(GlobalState.ADMIN_INSERT_FILE, AdminInsertController.class, sceneController);
        initController(GlobalState.ADMIN_USER_VIEW_FILE, AdminUserViewController.class, sceneController);
        initController(GlobalState.ADMIN_USER_VIEW_CARD_FILE, AdminUserViewCardController.class, sceneController);
        initController(GlobalState.ADMIN_VERSE_VIEW_FILE, AdminVerseViewController.class, sceneController);
        initController(GlobalState.ADMIN_VERSE_VIEW_CARD_FILE, AdminVerseViewCardController.class, sceneController);
        initController(GlobalState.ADMIN_DUA_VIEW_FILE, AdminDuaViewController.class, sceneController);
        initController(GlobalState.ADMIN_DUA_VIEW_CARD_FILE, AdminDuaViewCardController.class, sceneController);
        initController(GlobalState.ADMIN_RECITATION_VIEW_FILE, AdminRecitationViewController.class, sceneController);
        initController(GlobalState.ADMIN_RECITATION_VIEW_CARD_FILE, AdminRecitationViewCardController.class, sceneController);
        initController(GlobalState.ADMIN_APPROVE_FILE, AdminApproveController.class, sceneController);
        initController(GlobalState.ADMIN_APPROVE_CARD_FILE, AdminApproveCardController.class, sceneController);
        initController(GlobalState. FORUM_CARD_FILE, ForumCardController.class, sceneController);
        initController(GlobalState.FORUM_CARD_VERSE_FILE, ForumCardVerseController.class, sceneController);
        initController(GlobalState.FORUM_FILE, ForumController.class, sceneController);
        initController(GlobalState.ADMIN_FORUM_FILE, AdminForumController.class, sceneController);


        // PLAY INTRO VIDEO
        playIntroVideo(stage, sceneController);

        // SETUP THE STAGE
        URL iconURL = getClass().getResource("/images/brand.png");
        if (iconURL != null) {
            stage.getIcons().add(new Image(iconURL.toExternalForm()));
        }

        stage.setTitle("Quran Whispers");
        stage.setResizable(false);
        System.setProperty("prism.lcdtext", "false");
        stage.show();
    }


    // HELPER METHOD TO PLAY INTRO VIDEO
    private void playIntroVideo(Stage stage, SceneController sceneController) {
        URL videoURL = getClass().getResource("/video/intro.mp4");
        if (videoURL == null) {
            System.out.println("Video file not found: intro.mp4");
            return;
        }
        String videoPath = videoURL.toExternalForm();

        Media media = new Media(videoPath);
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);

        mediaView.setFitWidth(1280);
        mediaView.setFitHeight(750);

        Group root = new Group();
        root.getChildren().add(mediaView);

        Scene videoScene = new Scene(root, 1280, 750);
        stage.setScene(videoScene);

        mediaPlayer.play();

        mediaPlayer.setOnEndOfMedia(() -> {
            root.getChildren().remove(mediaView);

            // SETUP THE INITIAL SCENE
            LandingController landingController = (LandingController) sceneController.switchTo(GlobalState.LANDING_FILE);  // Switch to the landing page
            landingController.setupLandingPage();
        });
    }
}
