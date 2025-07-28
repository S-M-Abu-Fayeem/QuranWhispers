package Validators;

public class UserNameValidator {
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        //Boolean val = !username.matches(".*\\s.*");
        //System.out.println(username + " " + username + " " + val);
        return ((username.length() <= 15) && (!username.matches(".*\\s.*")));
    }

}
