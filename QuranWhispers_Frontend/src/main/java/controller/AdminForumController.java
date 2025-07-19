package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminForumController extends BaseControllerAdmin{
    @FXML TextArea promptArea;
    @FXML VBox containerVBox;

    public void sendNormalMessage(String message){};
    public void deleteMessage(String messageId){};
    public void banUser(String username){};
    public void unbanUser(String username){};
    public void clearHistory(){};
    public void removeLatestMessages(int num){};

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
                command = input.substring(1, input.indexOf("/", 1));
                message = input.substring(command.length() + 2).trim();
            }
        } else {
            message = input.trim();
            command = null;
            arguments = null;
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

        else {
            switch (command.toLowerCase()) {
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
