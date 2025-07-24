package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import util.BackendAPI;
import util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminForumController extends BaseControllerAdmin{
    @FXML TextArea promptArea;
    @FXML VBox containerVBox;

    // Complete admin actions with threading
    public void deleteMessage(String messageId) {
        JSONObject request = new JSONObject();
        request.put("token", SessionManager.getToken());
        request.put("email", SessionManager.getEmail());
        request.put("id", messageId);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BackendAPI.fetch("deletechatbyid", request);
                return null;
            }
        };
        new Thread(task).start();
    }

    public void banUser(String username) {
        JSONObject request = new JSONObject();
        request.put("token", SessionManager.getToken());
        request.put("email", SessionManager.getEmail());
        request.put("username", username);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BackendAPI.fetch("banuser", request);
                return null;
            }
        };
        new Thread(task).start();
    }

    public void unbanUser(String username) {
        JSONObject request = new JSONObject();
        request.put("token", SessionManager.getToken());
        request.put("email", SessionManager.getEmail());
        request.put("username", username);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BackendAPI.fetch("unbanuser", request);
                return null;
            }
        };
        new Thread(task).start();
    }

    public void clearHistory() {
        JSONObject request = new JSONObject();
        request.put("token", SessionManager.getToken());
        request.put("email", SessionManager.getEmail());

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BackendAPI.fetch("clearchatHistory", request);
                return null;
            }
        };
        new Thread(task).start();
    }

    public void removeLatestMessages(int num) {
        JSONObject request = new JSONObject();
        request.put("token", SessionManager.getToken());
        request.put("email", SessionManager.getEmail());
        request.put("number_of_chats", String.valueOf(num));

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BackendAPI.fetch("deletelatest", request);
                return null;
            }
        };
        new Thread(task).start();
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
        JSONObject aiRequest = new JSONObject();
        aiRequest.put("token", SessionManager.getToken());
        aiRequest.put("email", SessionManager.getEmail());
        aiRequest.put("question", message);

        Task<Void> askAITask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = BackendAPI.fetch("askai", aiRequest);
                if (response != null && response.has("status") && response.getInt("status") == 200) {
                    String answer = response.optString("answer", "");
                    JSONObject payload = new JSONObject();
                    payload.put("receiver_username", SessionManager.getUsername());
                    payload.put("sender_username", "AI");
                    payload.put("body", answer);
                    payload.put("type", "aiAns");
                    payload.put("token", SessionManager.getToken());
                    payload.put("email", SessionManager.getEmail());
                    payload.put("reply_chat_id", 0);
                    payload.put("surah", "null");
                    payload.put("ayah", 0);
                    Platform.runLater(() -> BackendAPI.sendForumMessage(payload));
                }
                return null;
            }
        };
        new Thread(askAITask).start();
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
        payload.put("type", "replyMessage");
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
        payload.put("receiver_username", "null");
        payload.put("sender_username", SessionManager.getUsername());
        payload.put("body", "null");
        payload.put("type", "about");
        payload.put("token", SessionManager.getToken());
        payload.put("email", SessionManager.getEmail());
        payload.put("reply_chat_id", 0);
        payload.put("surah", "null");
        payload.put("ayah", 0);
        BackendAPI.sendForumMessage(payload);
    }


    public void setupAdminForum() {
        BackendAPI.continuousFetch("start");
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
                case "delete":
                    if (arguments.isEmpty()) {
                        System.out.println("No message ID provided for delete command.");
                        return;
                    }
                    if (arguments.size() > 1) {
                        System.out.println("Too many arguments for delete command.");
                        return;
                    }

                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message provided with delete command, ignoring message: " + message);
                        return;
                    }

                    try {
                        String messageId = arguments.get(0);
                        deleteMessage(messageId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid message ID format for delete command.");
                    }

                    break;
                case "ban":
                    if (arguments.isEmpty()) {
                        System.out.println("No username provided for ban command.");
                        return;
                    }
                    if (arguments.size() > 1) {
                        System.out.println("Too many arguments for ban command.");
                        return;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message provided with ban command, ignoring message: " + message);
                        return;
                    }
                    String username = arguments.get(0);
                    if (username.isEmpty()) {
                        System.out.println("Username cannot be empty for ban command.");
                        return;
                    }
                    banUser(username);
                    break;
                case "unban":
                    if (arguments.isEmpty()) {
                        System.out.println("No username provided for unban command.");
                        return;
                    }
                    if (arguments.size() > 1) {
                        System.out.println("Too many arguments for unban command.");
                        return;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message provided with unban command, ignoring message: " + message);
                        return;
                    }
                    String unbanUsername = arguments.get(0);
                    if (unbanUsername.isEmpty()) {
                        System.out.println("Username cannot be empty for unban command.");
                        return;
                    }
                    unbanUser(unbanUsername);
                    break;
                case "clearhistory":
                    if (!arguments.isEmpty()) {
                        System.out.println("Arguments provided for clearHistory command, ignoring them.");
                        break;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message provided with clearHistory command, ignoring message: " + message);
                        return;
                    }
                    clearHistory();
                    break;
                case "removelatest":
                    if (arguments.isEmpty()) {
                        System.out.println("No number provided for removeLatest command.");
                        return;
                    }
                    if (arguments.size() > 1) {
                        System.out.println("Too many arguments for removeLatest command.");
                        return;
                    }
                    if (message != null && !message.isEmpty()) {
                        System.out.println("Message provided with removeLatest command, ignoring message: " + message);
                        return;
                    }
                    try {
                        int num = Integer.parseInt(arguments.get(0));
                        if (num <= 0) {
                            System.out.println("Number must be greater than 0 for removeLatest command.");
                            return;
                        }
                        removeLatestMessages(num);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format for removeLatest command.");
                    }
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
        promptArea.setText("/delete(msg_id)/");
    }

    public void handleCommand2() {
        playClickSound();
        System.out.println("Command 2 button pressed");
        promptArea.clear();
        promptArea.setText("/ban(username)/");
    }

    public void handleCommand3() {
        playClickSound();
        System.out.println("Command 3 button pressed");
        promptArea.clear();
        promptArea.setText("/unban(username)/");
    }

    public void handleCommand4() {
        playClickSound();
        System.out.println("Command 4 button pressed");
        promptArea.clear();
        promptArea.setText("/clearHistory/");
    }

    public void handleCommand5() {
        playClickSound();
        System.out.println("Command 5 button pressed");
        promptArea.clear();
        promptArea.setText("/removeLatest(num)/");
    }
}
