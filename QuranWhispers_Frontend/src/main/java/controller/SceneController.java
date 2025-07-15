package controller;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.HashMap;

public class SceneController {
    private final Stage stage;
    private final HashMap<String, Scene> sceneMap = new HashMap<>();
    private final HashMap<String, Object> controllerMap = new HashMap<>();
    private final HashMap<String, EventHandler<KeyEvent>> keyEventHandlers = new HashMap<>();

    public SceneController(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void addScene(String name, Parent root, Object controller) {
        Scene scene = new Scene(root, 1280, 750);

        URL cssFile = getClass().getResource("/styles/main.css");
        if (cssFile != null) {
            scene.getStylesheets().add(cssFile.toExternalForm());
        }

        sceneMap.put(name, scene);
        controllerMap.put(name, controller);
    }

    public void addKeyEventHandler(String sceneName, EventHandler<KeyEvent> keyEventHandler) {
        keyEventHandlers.put(sceneName, keyEventHandler);
    }

    // Switch to a scene by name, and set the key event handler if available
    public Object switchTo(String name) {
        Scene scene = sceneMap.get(name);
        if (scene == null) {
            throw new RuntimeException("Scene not registered: " + name);
        }

        stage.setScene(scene);

        EventHandler<KeyEvent> keyEventHandler = keyEventHandlers.get(name);
        if (keyEventHandler != null) {
            scene.setOnKeyPressed(keyEventHandler);
        }

        return controllerMap.get(name);
    }
}
