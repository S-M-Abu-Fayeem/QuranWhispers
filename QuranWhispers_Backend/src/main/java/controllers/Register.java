package controllers;

import Validators.HashingFunction;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import static com.example.server.HelloApplication.*;

public class Register {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public synchronized String GET(String email, String username, String password) {
        HashMap<String, String> data = new HashMap<>();
        Gson gson = new Gson();
        data.put("email", email);
        data.put("username", username);

        try (Connection connection = DriverManager.getConnection(DB_URL)) {


            PreparedStatement check = connection.prepareStatement("SELECT 1 FROM USERS WHERE email = ?");
            check.setString(1, email);
            ResultSet rs = check.executeQuery();
            PreparedStatement check2 = connection.prepareStatement("SELECT 1 FROM USERS WHERE username = ? ");
            check2.setString(1, username);
            ResultSet rs2 = check2.executeQuery();
            if(rs2.next()) {
                data.put("status", "401");
                data.put("status_message", "Username already exists.Please choose another one.");
                return gson.toJson(data);
            }

            if (rs.next()) {
                data.put("status", "401");
                data.put("status_message", "Email already exists. Please choose another one.");
                return gson.toJson(data);
            }
           // Boolean val = userNameValidator.isValidUsername(username);
            //System.out.println(username + " " + password + " " + val);
            if(!userNameValidator.isValidUsername(username)) {
                data.put("status", "401");
                data.put("status_message", "Invalid username: Should be 15 characters or fewer and must not contain any spaces");
                return gson.toJson(data);
            }
            if(!emailValidator.isValidEmail(email)) {
                data.put("status", "401");
                data.put("status_message", "Please follow the standard email address format.");
                return gson.toJson(data);
            }
            if(!passwordValidator.isValidPassword(password)) {
                data.put("status", "401");
                data.put("status_message", "Invalid Password: Must be at least 6 characters long and include at least one alphabet and one digit.");
                return gson.toJson(data);

            }




            HashingFunction hashFunction = new HashingFunction();
            int hashValue = hashFunction.getHash(password);
            PreparedStatement ps = connection.prepareStatement("INSERT INTO USERS (username, email, password, active_user) VALUES (?, ?, ?, ?)");
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setInt(3, hashValue);
            ps.setBoolean(4, true);
            ps.executeUpdate();

            data.put("status", "200");
            return gson.toJson(data);

        } catch (Exception e) {
            e.printStackTrace();
            data.put("status", "500");
            data.put("status_message", "It is not your fault , it's our fault . Please contact the developers");
            return gson.toJson(data);
        }
    }
}
