package util;

public class SessionManager {
    private static String email;
    private static String username;
    private static String token = "-1";
    static void setEmail(String emailAddress) {
        SessionManager.email = emailAddress;
    }
    static void setToken(String newToken) {
        SessionManager.token = newToken;
    }
    public static String getEmail() {
        return SessionManager.email;
    }
    public static String getToken() {
        return SessionManager.token;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        SessionManager.username = username;
    }

    public static void clearSession() {
        email = null;
        username = null;
        token = "-1";
    }
}
