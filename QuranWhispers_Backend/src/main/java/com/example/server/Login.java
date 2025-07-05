package com.example.server;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Boolean.getBoolean;


public class Login {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String GET(String userName, String password) {
        HashMap<String, String> data = new HashMap<>();
        Gson gson = new Gson();
        data.put("email", userName);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            try {
                //fetching password by username
                PreparedStatement ps = connection.prepareStatement("SELECT password FROM USERS WHERE email = ?");

                ps.setString(1, userName);
                ResultSet rs = ps.executeQuery();

                //creating json file to send response
               // System.out.println("done");
                HashingFunction hasFunction = new HashingFunction();
                int hashValue = hasFunction.getHash(password);
                data.put("status", "404");
                data.put("token", "-1");

                while (rs.next()) {
                    System.out.println(rs.getInt("password"));
                    System.out.println(hashValue);
                    if (rs.getInt("password") == hashValue) {

                        data.put("email", userName);
                        data.put("status", "200");

                        // creating the token using hashing function , token format = TimeStamp + email -> converted to hashValue
                        LocalDateTime now = LocalDateTime.now();
                        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String concat = formatted + userName;
                        int hashValueOfToken = hasFunction.getHash(concat);
                        //token update
                        PreparedStatement  addingToken = connection.prepareStatement("UPDATE USERS SET token = ? WHERE email = ?");
                        addingToken.setInt(1, hashValueOfToken);
                        addingToken.setString(2, userName);
                        int updatedRows = addingToken.executeUpdate();
                        data.put("token", String.valueOf(hashValueOfToken));
                        PreparedStatement isAdmin = connection.prepareStatement("SELECT is_admin FROM USERS WHERE email = ?");
                        isAdmin.setString(1, userName);
                        ResultSet Admin = isAdmin.executeQuery();
                        Admin.next();
                        if(Admin.getBoolean("is_admin")) {
                            data.put("admin", "true");
                        }
                        else{
                            data.put("admin", "false");
                        }
                    }
                }

                //converting the json file to string to send to client
                return gson.toJson(data);
            }
            catch (Exception e) {
                data.put("status", "404");
                return gson.toJson(data);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        data.put("status", "500");
        return gson.toJson(data);
    }
}
