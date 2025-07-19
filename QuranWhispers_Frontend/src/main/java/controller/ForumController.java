package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import org.json.JSONObject;
import util.BackendAPI;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForumController extends BaseController {
    @FXML TextArea promptArea;
    @FXML VBox containerVBox;

    public void sendNormalMessage(String message) {}
    public void sendQuestion(String message) {}
    public void sendAskAI(String message) {}
    public void sendVerse(String surah, String ayah) {}
    public void sendVerseEmotion(String emotion) {}
    public void sendVerseTheme(String theme) {}
    public void sendReply(String messageId, String message) {}
    public void sendToUser(String username, String message) {}
    public void sendAboutMessage(String message) {}

    public void setupForum() {
        BackendAPI.continuousFetch("start");
    }

    public void extractMessageCommandAndArgs(String input) {
        String message = null;
        String command = null;
        List<String> arguments = null;

        String regex = "/([a-zA-Z]+)\\(([^)]+)\\)/";

        if (input.startsWith("/")) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                command = matcher.group(1);
                arguments = new ArrayList<String>();
                for (String arg : matcher.group(2).split(",")) {
                    arguments.add(arg.trim());
                }
                message = null;
            } else {
                int endIndex = input.indexOf("/", 1);
                if (endIndex != -1) {
                    command = input.substring(1, endIndex);
                    message = input.substring(endIndex + 1).trim();
                } else {
                    command = input.substring(1);
                    message = null;
                }
            }
        } else {
            message = input.trim();
        }

        JSONObject json = new JSONObject();
        json.put("message", message != null ? message : "");
        json.put("command", command != null ? command : "");
        json.put("arguments", arguments != null && !arguments.isEmpty() ? new ArrayList<>(arguments) : new ArrayList<String>());

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

        if (!command.trim().isEmpty()) {
            switch (command) {
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
                    sendVerse(arguments.get(0), arguments.get(1));
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
                        System.out.println("Message: " + message);
                        sendAboutMessage(message);
                    } else {
                        System.out.println("No message provided for about command.");
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
}
