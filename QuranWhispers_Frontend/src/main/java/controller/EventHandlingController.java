package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import util.GlobalState;

public class EventHandlingController extends BaseController{

    @FXML

    private Circle myCircle;
    private double x;
    private double y;


    public void up() {
        System.out.println("up");
        myCircle.setCenterY(y -= 10);
    }

    public void down() {
        System.out.println("down");
        myCircle.setCenterY(y += 10);
    }

    public void left() {
        System.out.println("left");
        myCircle.setCenterX(x -= 10);
    }

    public void right() {
        System.out.println("right");
        myCircle.setCenterX(x += 10);
    }

    // KEY EVENT HANDLER LOGIC
    String CURRENT_FILE_NAME = GlobalState.EVENT_HANDLING_FILE; // UPDATE THIS
    private EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            System.out.println("Key pressed: " + keyEvent.getCode());

            // ADD YOUR KEY PRESS LOGIC HERE
            if (keyEvent.getCode().toString().equals("W")) {
                up();
            } else if (keyEvent.getCode().toString().equals("S")) {
                down();
            } else if (keyEvent.getCode().toString().equals("A")) {
                left();
            } else if (keyEvent.getCode().toString().equals("D")) {
                right();
            }
        }
    };

    public void initializeKeyHandling() {
        sceneController.addKeyEventHandler(CURRENT_FILE_NAME, keyEventHandler);
    }

    @Override
    public void setSceneController(SceneController controller) {
        super.setSceneController(controller);
        initializeKeyHandling();
    }
    // --------------------------------------------------------------------------------------------
};