package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import util.GlobalState;
import util.SessionManager;

public class ForumCardController extends BaseController{
    @FXML HBox wrapperHBox;
    @FXML Rectangle msgCard;
    @FXML Text msgBody;
    @FXML Label senderUsername;
    @FXML Rectangle senderUsernameWrapper;
    @FXML Label receiverUsername;
    @FXML Rectangle receiverUsernameWrapper;
    @FXML Label replyMsgID;
    @FXML Rectangle replyMsgIDWrapper;
    @FXML Label tagName;
    @FXML Label msgID;
    @FXML Rectangle tagNameWrapper;
    String parent;
    ForumController forumController;
    AdminForumController adminForumController;

    public void setParent(String parent, ForumController forumController) {
        this.parent = parent;
        this.forumController = forumController;
    };

    public void setParent(String parent, AdminForumController adminForumController) {
        this.parent = parent;
        this.adminForumController = adminForumController;
    };

    public void setupCard(String msgID, String msgBody, String senderUsername, String receiverUsername, String replyMsgID, String tagName) {
        this.msgID.setText(msgID);
        this.msgBody.setText(msgBody);
        this.senderUsername.setText(senderUsername);
        if (receiverUsername.equals("null") || receiverUsername.isEmpty()) {
            this.receiverUsernameWrapper.setVisible(false);
            this.receiverUsername.setText("");
        } else {
            this.receiverUsername.setText(receiverUsername);
            this.receiverUsernameWrapper.setVisible(true);
        }
        if (replyMsgID.equals("0") || replyMsgID.isEmpty()) {
            this.replyMsgIDWrapper.setVisible(false);
            this.replyMsgID.setText("");
        } else {
            this.replyMsgID.setText("replyTo: "+ replyMsgID);
            this.replyMsgIDWrapper.setVisible(true);
        }
        this.tagName.setText(tagName);

        if (senderUsername.equals(SessionManager.getUsername())) {
            msgCard.setStroke(Color.BLACK);
            wrapperHBox.setAlignment(Pos.CENTER_RIGHT);
        }
    }

    public void handleReplyBtn() {
        playClickSound();
        if (parent.equals(GlobalState.FORUM_FILE)) {
            forumController.setPromptArea("/reply("+msgID.getText()+")/");
        } else {
            adminForumController.setPromptArea("/reply("+msgID.getText()+")/");
        }
    }

}
