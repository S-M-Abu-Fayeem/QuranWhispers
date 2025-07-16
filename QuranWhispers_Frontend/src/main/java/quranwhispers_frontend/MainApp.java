package quranwhispers_frontend;

import controller.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
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

//import controller.GlobalRecitationController;

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

import controller.GlobalShareController;
import controller.ForumController;


public class MainApp extends Application {
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

        initController(GlobalState.ADMIN_INSERT_FILE, AdminInsertController.class, sceneController);
        initController(GlobalState.ADMIN_USER_VIEW_FILE, AdminUserViewController.class, sceneController);
        initController(GlobalState.ADMIN_USER_VIEW_CARD_FILE, AdminUserViewCardController.class, sceneController);
        initController(GlobalState.ADMIN_VERSE_VIEW_FILE, AdminVerseViewController.class, sceneController);
        initController(GlobalState.ADMIN_VERSE_VIEW_CARD_FILE, AdminVerseViewCardController.class, sceneController);
        initController(GlobalState.ADMIN_DUA_VIEW_FILE, AdminDuaViewController.class, sceneController);
        initController(GlobalState.ADMIN_DUA_VIEW_CARD_FILE, AdminDuaViewCardController.class, sceneController);

        initController(GlobalState.ADMIN_RECITATION_VIEW_FILE, AdminRecitationViewController.class, sceneController);

        initController(GlobalState.ADMIN_APPROVE_FILE, AdminApproveController.class, sceneController);
        initController(GlobalState.ADMIN_APPROVE_CARD_FILE, AdminApproveCardController.class, sceneController);
        initController(GlobalState.FORUM_FILE, ForumController.class, sceneController);


        // CHOOSE THE INITIAL SCENE
        sceneController.switchTo(GlobalState.LANDING_FILE);

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
}
