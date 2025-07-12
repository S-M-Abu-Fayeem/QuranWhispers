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
import controller.LoginController;
import controller.SignupController;
import controller.HomePageController;
import controller.SearchController;
import controller.ShareController;
import controller.RecitationController;
import controller.NotificationController;
import controller.ProfileController;

import controller.AdminInsertController;
import controller.AdminApproveController;
import controller.AdminUserViewController;
import controller.AdminVerseViewController;
import controller.AdminRecitationViewController;
import controller.AdminDuaViewController;



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
        initController(GlobalState.HOME_PAGE_FILE, HomePageController.class, sceneController);
        initController(GlobalState.LOGIN_FILE, LoginController.class, sceneController);
        initController(GlobalState.SIGNUP_FILE, SignupController.class, sceneController);
        initController(GlobalState.SEARCH_FILE, SearchController.class, sceneController);
        initController(GlobalState.SHARE_FILE, ShareController.class, sceneController);
        initController(GlobalState.RECITATION_FILE, RecitationController.class, sceneController);
        initController(GlobalState.NOTIFICATION_FILE, NotificationController.class, sceneController);
        initController(GlobalState.PROFILE_FILE, ProfileController.class, sceneController);

        initController(GlobalState.ADMIN_INSERT_FILE, AdminInsertController.class, sceneController);
        initController(GlobalState.ADMIN_APPROVE_FILE, AdminApproveController.class, sceneController);


        // CHOOSE THE INITIAL SCENE
        sceneController.switchTo(GlobalState.LANDING_FILE);

        // SETUP THE STAGE
        URL iconURL = getClass().getResource("/images/brand.png");
        if (iconURL != null) {
            stage.getIcons().add(new Image(iconURL.toExternalForm()));
        }

        stage.setTitle("Quran Whispers");
        stage.setResizable(false);
        stage.show();
    }
}
