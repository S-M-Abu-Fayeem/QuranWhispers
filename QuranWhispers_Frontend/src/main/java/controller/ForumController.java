package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;
import util.BackendAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.GlobalState;
import util.SessionManager;


public class ForumController extends BaseController {
    @FXML TextArea promptArea;
    @FXML VBox containerVBox;
    @FXML ScrollPane containerScrollPane;
    JSONArray latestResponse = new JSONArray();

    public void setupForum() {
        Task<Void> fetchTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    JSONObject jsonResponse = BackendAPI.continuousFetch("start");
                    if (jsonResponse != null && jsonResponse.getString("status").equals("200")) {
                        Platform.runLater(() -> {
                            try {
                                JSONArray chats = jsonResponse.getJSONArray("chats");
                                if (latestResponse.similar(chats)) {
                                    return;
                                }
                                System.out.println("Response received: " + chats.toString());
                                latestResponse.clear();
                                containerVBox.getChildren().clear();
                                for (int i = 0; i < chats.length(); i++) {
                                    JSONObject chat = chats.getJSONObject(i);
                                    latestResponse.put(chat);
                                    String msgID = String.valueOf(chat.getInt("id"));
                                    String sender = chat.getString("sender_username");
                                    String receiver = chat.getString("receiver_username");
                                    String body = chat.getString("body");
                                    String type = chat.getString("type");
                                    String replyChatId = String.valueOf(chat.getInt("reply_chat_id"));
                                    String surah = chat.getString("surah");
                                    String ayah = String.valueOf(chat.getInt("ayah"));
                                    Parent card = null;

                                    if (type.equals("verse")) {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forumCardVerse.fxml"));
                                        card = loader.load();

                                        ForumCardVerseController controller = loader.getController();
                                        if (sceneController != null) controller.setSceneController(sceneController);
                                        controller.setupCard(msgID, sender, surah, ayah);
                                        controller.setParent(GlobalState.FORUM_FILE, ForumController.this);
                                    } else {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forumCard.fxml"));
                                        card = loader.load();

                                        ForumCardController controller = loader.getController();
                                        if (sceneController != null) controller.setSceneController(sceneController);
                                        controller.setupCard(msgID, body, sender, receiver, replyChatId, type);
                                        controller.setParent(GlobalState.FORUM_FILE, ForumController.this);
                                    }

                                    if (card != null) {
                                        containerVBox.getChildren().add(card);
                                    }
                                }
                                Platform.runLater(() -> containerScrollPane.setVvalue(1.0));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                System.out.println("❌ Error setting up forum cards: " + ex.getMessage());
                            }
                        });
                    }
                    Thread.sleep(500);
                }
                return null;
            }
        };
        Thread thread = new Thread(fetchTask);
        thread.setDaemon(true);
        thread.start();
    }



    public void sendNormalMessage(String message) {
        JSONObject payload = new JSONObject();
        payload.put("receiver_username", "null");
        payload.put("sender_username", SessionManager.getUsername());
        payload.put("body", message);
        payload.put("type", "normal");
        payload.put("token", SessionManager.getToken());
        payload.put("email", SessionManager.getEmail());
        payload.put("reply_chat_id", 0);
        payload.put("surah", "null");
        payload.put("ayah", 0);
        BackendAPI.sendForumMessage(payload);
    }

    public void sendQuestion(String message) {
        JSONObject payload = new JSONObject();
        payload.put("receiver_username", "null");
        payload.put("sender_username", SessionManager.getUsername());
        payload.put("body", message);
        payload.put("type", "question");
        payload.put("token", SessionManager.getToken());
        payload.put("email", SessionManager.getEmail());
        payload.put("reply_chat_id", 0);
        payload.put("surah", "null");
        payload.put("ayah", 0);
        BackendAPI.sendForumMessage(payload);
    }

    public void sendVerse(String surah, int ayah) {
        JSONObject payload = new JSONObject();
        payload.put("receiver_username", "null");
        payload.put("sender_username", SessionManager.getUsername());
        payload.put("body", "null");
        payload.put("type", "verse");
        payload.put("token", SessionManager.getToken());
        payload.put("email", SessionManager.getEmail());
        payload.put("reply_chat_id", 0);
        payload.put("surah", surah);
        payload.put("ayah", ayah);
        BackendAPI.sendForumMessage(payload);
    }

    public void sendAskAI(String message) {
        Task<Void> askAITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject payload = new JSONObject();
                payload.put("receiver_username", "AI");
                payload.put("sender_username", SessionManager.getUsername());
                payload.put("body", message);
                payload.put("type", "askai");
                payload.put("token", SessionManager.getToken());
                payload.put("email", SessionManager.getEmail());
                payload.put("reply_chat_id", 0);
                payload.put("surah", "null");
                payload.put("ayah", 0);
                BackendAPI.sendForumMessage(payload);
                return null;
            }
        };
        new Thread(askAITask).start();

        JSONObject aiRequest = new JSONObject();
        aiRequest.put("token", SessionManager.getToken());
        aiRequest.put("email", SessionManager.getEmail());
        aiRequest.put("question", message);

        Task<Void> responseAITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = BackendAPI.fetch("askai", aiRequest);
                if (response != null && response.has("status") && response.getInt("status") == 200) {
                    String answer = response.optString("answer", "");
                    JSONObject new_payload = new JSONObject();
                    new_payload.put("receiver_username", SessionManager.getUsername());
                    new_payload.put("sender_username", "AI");
                    new_payload.put("body", answer);
                    new_payload.put("type", "aiAns");
                    new_payload.put("token", SessionManager.getToken());
                    new_payload.put("email", SessionManager.getEmail());
                    new_payload.put("reply_chat_id", 0);
                    new_payload.put("surah", "null");
                    new_payload.put("ayah", 0);
                    Platform.runLater(() -> BackendAPI.sendForumMessage(new_payload));
                }
                return null;
            }
        };
        new Thread(responseAITask).start();
    }

    // Repeat similar for sendVerseEmotion and sendVerseTheme:
    public void sendVerseEmotion(String emotion) {
        JSONObject emotionRequest = new JSONObject();
        emotionRequest.put("token", SessionManager.getToken());
        emotionRequest.put("email", SessionManager.getEmail());
        emotionRequest.put("emotion", emotion);

        Task<Void> emotionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = BackendAPI.fetch("generateemotionbasedverse", emotionRequest);
                if (response != null && response.has("status") && response.getInt("status") == 200) {
                    String surah = response.optString("surah", "null");
                    int ayah = response.optInt("ayah", 0);
                    JSONObject payload = new JSONObject();
                    payload.put("receiver_username", "null");
                    payload.put("sender_username", SessionManager.getUsername());
                    payload.put("body", "null");
                    payload.put("type", "verse");
                    payload.put("token", SessionManager.getToken());
                    payload.put("email", SessionManager.getEmail());
                    payload.put("reply_chat_id", 0);
                    payload.put("surah", surah);
                    payload.put("ayah", ayah);
                    Platform.runLater(() -> BackendAPI.sendForumMessage(payload));
                }
                return null;
            }
        };
        new Thread(emotionTask).start();
    }

    public void sendVerseTheme(String theme) {
        JSONObject themeRequest = new JSONObject();
        themeRequest.put("token", SessionManager.getToken());
        themeRequest.put("email", SessionManager.getEmail());
        themeRequest.put("theme", theme);

        Task<Void> themeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = BackendAPI.fetch("generatethemebasedverse", themeRequest);
                if (response != null && response.has("status") && response.getInt("status") == 200) {
                    String surah = response.optString("surah", "null");
                    int ayah = response.optInt("ayah", 0);
                    JSONObject payload = new JSONObject();
                    payload.put("receiver_username", "null");
                    payload.put("sender_username", SessionManager.getUsername());
                    payload.put("body", "null");
                    payload.put("type", "verse");
                    payload.put("token", SessionManager.getToken());
                    payload.put("email", SessionManager.getEmail());
                    payload.put("reply_chat_id", 0);
                    payload.put("surah", surah);
                    payload.put("ayah", ayah);
                    Platform.runLater(() -> BackendAPI.sendForumMessage(payload));
                }
                return null;
            }
        };
        new Thread(themeTask).start();
    }

    public void sendReply(String messageId, String message) {
        JSONObject payload = new JSONObject();
        payload.put("receiver_username", "null");
        payload.put("sender_username", SessionManager.getUsername());
        payload.put("body", message);
        payload.put("type", "replyMsg");
        payload.put("token", SessionManager.getToken());
        payload.put("email", SessionManager.getEmail());
        payload.put("reply_chat_id", messageId);
        payload.put("surah", "null");
        payload.put("ayah", 0);
        BackendAPI.sendForumMessage(payload);
    }

    public void sendToUser(String username, String message) {
        JSONObject payload = new JSONObject();
        payload.put("receiver_username", username);
        payload.put("sender_username", SessionManager.getUsername());
        payload.put("body", message);
        payload.put("type", "replyUser");
        payload.put("token", SessionManager.getToken());
        payload.put("email", SessionManager.getEmail());
        payload.put("reply_chat_id", 0);
        payload.put("surah", "null");
        payload.put("ayah", 0);
        BackendAPI.sendForumMessage(payload);
    }

    public void sendAboutMessage() {
        JSONObject payload = new JSONObject();
        payload.put("receiver_username", SessionManager.getUsername());
        payload.put("sender_username", "SERVER");
        payload.put("body", "QuranWhispers is an emotional companion to the Quran, offering features like verse search by emotion/theme, soothing recitations, daily duas, verse posters, social sharing, admin controls, and an interactive forum to engage users and admins with smart commands.");
        payload.put("type", "about");
        payload.put("token", SessionManager.getToken());
        payload.put("email", SessionManager.getEmail());
        payload.put("reply_chat_id", 0);
        payload.put("surah", "null");
        payload.put("ayah", 0);
        BackendAPI.sendForumMessage(payload);
    }


    public void extractMessageCommandAndArgs(String input) {
        String message = null;
        String command = null;
        List<String> arguments = new ArrayList<>();

        String regex = "^/([a-zA-Z]+)\\(([^)]*)\\)/(.*)?$";

        if (input.startsWith("/")) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            if (matcher.matches()) {
                command = matcher.group(1);
                String argStr = matcher.group(2).trim();
                message = matcher.group(3) != null ? matcher.group(3).trim() : null;

                if (!argStr.isEmpty()) {
                    for (String arg : argStr.split(",")) {
                        arguments.add(arg.trim());
                    }
                }
            } else {
                // Handle commands without parentheses but maybe with message
                int endIndex = input.indexOf("/", 1);
                if (endIndex != -1) {
                    command = input.substring(1, endIndex);
                    message = input.substring(endIndex + 1).trim();
                } else {
                    command = input.substring(1);
                }
            }
        } else {
            message = input.trim(); // Plain message
        }

        JSONObject json = new JSONObject();
        json.put("message", message != null ? message : "");
        json.put("command", command != null ? command : "");
        json.put("arguments", arguments);

        msgManager(json);
    }


    public void msgManager(JSONObject json) {
        String message = json.getString("message");
        String command = json.getString("command");
        List<String> arguments = new ArrayList<>();
        for (int i = 0; i < json.getJSONArray("arguments").length(); i++) {
            arguments.add(json.getJSONArray("arguments").getString(i));
        }


        if (command.trim().isEmpty()) {
            if (message.trim().isEmpty()) {
                System.out.println("No message or command provided.");
            } else {
                sendNormalMessage(message);
            }
        }

        else {
            switch (command.toLowerCase()) {
                case "question":
                    System.out.println("Command: question, Arguments: " + arguments);
                    if (!arguments.isEmpty()) {
                        System.out.println("Questions dont have arguments, but received: " + arguments);
                        break;
                    }

                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message: " + message);
                        sendQuestion(message);
                    } else {
                        System.out.println("No message provided for question command.");
                    }
                    break;
                case "askai":
                    System.out.println("Command: askai, Arguments: " + arguments);
                    // Handle askai command
                    if (!arguments.isEmpty()) {
                        System.out.println("AskAI command does not require arguments, but received: " + arguments);
                        break;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message: " + message);
                        sendAskAI(message);
                    } else {
                        System.out.println("No message provided for askai command.");
                    }
                    break;
                case "reply":
                    System.out.println("Command: reply, Arguments: " + arguments);
                    // Handle reply command
                    if (arguments.isEmpty()) {
                        System.out.println("Reply command requires a message ID argument.");
                        break;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message: " + message);
                        sendReply(arguments.get(0), message);
                    } else {
                        System.out.println("No message provided for reply command.");
                    }
                    break;
                case "verse":
                    System.out.println("Command: verse, Arguments: " + arguments);
                    // Handle verse command
                    if (arguments.size() < 2) {
                        System.out.println("Verse command requires surah and ayah arguments.");
                        break;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("No message needed for verse command but received: " + message);
                        break;
                    }

                    System.out.println("Surah: " + arguments.get(0) + ", Ayah: " + arguments.get(1));
                    sendVerse(arguments.get(0), Integer.parseInt(arguments.get(1)));
                    break;
                case "verseEmotion":
                    System.out.println("Command: verseEmotion, Arguments: " + arguments);
                    // Handle verseEmotion command
                    if (arguments.isEmpty()) {
                        System.out.println("VerseEmotion command requires an emotion argument.");
                        break;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("No message needed for verse command but received: " + message);
                        break;
                    }
                    System.out.println("Emotion: " + arguments.get(0));
                    sendVerseEmotion(arguments.get(0));
                    break;
                case "verseTheme":
                    System.out.println("Command: verseTheme, Arguments: " + arguments);
                    // Handle verseTheme command
                    if (arguments.isEmpty()) {
                        System.out.println("VerseTheme command requires a theme argument.");
                        break;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("No message needed for verse command but received: " + message);
                        break;
                    }
                    System.out.println("Theme: " + arguments.get(0));
                    sendVerseTheme(arguments.get(0));
                    break;
                case "send":
                    System.out.println("Command: send, Arguments: " + arguments);
                    // Handle send command
                    if (arguments.isEmpty()) {
                        System.out.println("Send command requires a username argument.");
                        break;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message: " + message);
                        sendToUser(arguments.get(0), message);
                    } else {
                        System.out.println("No message provided for send command.");
                    }
                    break;
                case "about":
                    System.out.println("Command: about");
                    // Handle about command
                    if (!arguments.isEmpty()) {
                        System.out.println("About command does not require arguments, but received: " + arguments);
                        break;
                    }

                    if (message != null && !message.isEmpty()) {
                        System.out.println("About command does not require a message, but received: " + message);
                        break;
                    }
                    sendAboutMessage();

                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        }
    }



    public void handleSendBtn() {
        playClickSound();
        System.out.println("Send button pressed");
        System.out.println("Prompt: " + promptArea.getText());
        extractMessageCommandAndArgs(promptArea.getText());
        promptArea.clear();
    }

    public void handleCommand1() {
        playClickSound();
        System.out.println("Command 1 button pressed");
        promptArea.clear();
        promptArea.setText("/question/");
    }

    public void handleCommand2() {
        playClickSound();
        System.out.println("Command 2 button pressed");
        promptArea.clear();
        promptArea.setText("/askai/");
    }

    public void handleCommand3() {
        playClickSound();
        System.out.println("Command 3 button pressed");
        promptArea.clear();
        promptArea.setText("/reply(msg_id)/");
    }

    public void handleCommand4() {
        playClickSound();
        System.out.println("Command 4 button pressed");
        promptArea.clear();
        promptArea.setText("/verse(surah, ayah)/");
    }

    public void handleCommand5() {
        playClickSound();
        System.out.println("Command 5 button pressed");
        promptArea.clear();
        promptArea.setText("/verseEmotion(name)/");
    }

    public void handleCommand6() {
        playClickSound();
        System.out.println("Command 6 button pressed");
        promptArea.clear();
        promptArea.setText("/verseTheme(name)/");
    }

    public void handleCommand7() {
        playClickSound();
        System.out.println("Command 7 button pressed");
        promptArea.clear();
        promptArea.setText("/send(username)/");
    }

    public void handleCommand8() {
        playClickSound();
        System.out.println("Command 8 button pressed");
        promptArea.clear();
        promptArea.setText("/about/");
    }

    public void setPromptArea(String text) {
        if (promptArea != null) {
            promptArea.clear();
            promptArea.setText(text);
        } else {
            System.err.println("Prompt area is not initialized.");
        }
    }
}
