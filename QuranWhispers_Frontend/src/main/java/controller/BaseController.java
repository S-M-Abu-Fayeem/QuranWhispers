package controller;

import util.GlobalState;

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
}