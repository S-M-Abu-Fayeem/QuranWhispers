package Validators;

public class PasswordValidator {
    public static  synchronized boolean isValidPassword(String password) {
        if (password == null || password.length() < 6)
            return false;

        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasLetter && hasDigit;
    }
}
