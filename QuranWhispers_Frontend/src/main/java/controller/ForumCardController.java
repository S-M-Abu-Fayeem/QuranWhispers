package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ForumCardController extends BaseController{
    @FXML Rectangle msgCard;
    @FXML Text msgBody;
    @FXML Label senderUsername;
    @FXML Rectangle senderUsernameWrapper;
    @FXML Label receiverUsername;
    @FXML Rectangle receiverUsernameWrapper;
    @FXML Label replyMsgID;
    @FXML Rectangle replyMsgIDWrapper;
    @FXML Label tagName;
    @FXML Rectangle tagNameWrapper;

    public void setupCard() {

    }

}
