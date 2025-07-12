package controller;

import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;
import util.BackendAPI;
import util.GlobalState;
import java.io.IOException;

public class LandingController extends BaseController{
    public void handleLearnMoreBtn(MouseEvent e) throws IOException {
        System.out.println("Learn More button pressed");
        Task<Void> BackendAPITestingTask= new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("password", "782hghg");
                request.put("email", "ahammad@gmail.com");
                request.put("username", "ahammad");


                JSONObject response = BackendAPI.fetch("register", request);
                for (String key : response.keySet()) {
                    System.out.println("Key: " + key + " | Value: " + response.get(key));
                }
                return null;
            }
        };

        // Start the task in a background thread
        new Thread(BackendAPITestingTask).start();

        playClickSound();
    }

    @Override
    public void handleTitleLink(MouseEvent e) throws IOException {
        System.out.println("Title pressed");
        sceneController.switchTo(GlobalState.LANDING_FILE);
        playClickSound();
    }
}
