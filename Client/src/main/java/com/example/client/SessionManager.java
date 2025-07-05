package com.example.client;

public class SessionManager {
    private String email;
    private String token;
    SessionManager() {
        this.token = "-1";
    }
    void setEmail(String email) {
        this.email = email;
    }
    void setToken(String token) {
        this.token = token;
    }
    public String getEmail() {
        return email;
    }
    public String getToken() {
        return token;
    }
}
