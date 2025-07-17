package com.example.server;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class LogOut {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public synchronized String Logout(String email, int token) {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        TokenValidator tokenValidator = new TokenValidator();
        if(!tokenValidator.VALIDATE(email,token)){
            json.addProperty("status", "401");
            return gson.toJson(json);
        }
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            try {
                PreparedStatement  addingToken = connection.prepareStatement("UPDATE USERS SET token = ? WHERE email = ?");
                addingToken.setInt(1, -1);
                addingToken.setString(2, email);
                int updatedRows = addingToken.executeUpdate();
                if(updatedRows == 1){
                    json.addProperty("status", "200");
                }
                else{
                    json.addProperty("status", "500");
                }

            }
            catch (Exception e) {
                e.printStackTrace();
                json.addProperty("status", "500");
            }

        } catch (Exception e) {
            e.printStackTrace();
            json.addProperty("status", "500");
        }
        return gson.toJson(json);

    }
}
