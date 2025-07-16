package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForumController extends BaseController{
    @FXML TextArea promptArea;
    @FXML VBox containerVBox;

    public static void extractMessageCommandAndArgs(String input) {
        String message = null;
        String command = null;
        List<String> arguments = null;

        // Regular expression to match commands with arguments like /command(arg1, arg2)/
        String regex = "/([a-zA-Z]+)\\(([^)]+)\\)/"; // Matches /command(arg1, arg2)/

        // Check if the input starts with a command (with or without arguments)
        if (input.startsWith("/")) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            // If the command with arguments is found
            if (matcher.find()) {
                command = matcher.group(1); // Extract the command
                arguments = new ArrayList<>();

                // Split the arguments by comma and add them to the list
                for (String arg : matcher.group(2).split(",")) {
                    arguments.add(arg.trim()); // Trim any leading or trailing spaces
                }
                message = null;
            } else {
                // If no command with arguments, just extract the command without arguments
                command = input.substring(1, input.indexOf("/", 1)); // Extracts the command
                message = input.substring(command.length() + 2).trim(); // The remaining part after the command
            }
        } else {
            // If the input does not start with a command
            message = input.trim();
            command = null;
            arguments = null;
        }

        // Print the extracted values
        System.out.println("Message: " + (message != null ? message : "null"));
        System.out.println("Command: " + (command != null ? command : "null"));
        System.out.println("Arguments: " + (arguments != null ? arguments : "null"));
    }

    public void handleSendBtn() {
        playClickSound();
        System.out.println("Send button pressed");
        System.out.println("Prompt: " + promptArea.getText());
        extractMessageCommandAndArgs(promptArea.getText());
        promptArea.clear();
    }
}
