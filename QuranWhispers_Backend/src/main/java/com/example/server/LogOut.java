package com.example.server;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class LogOut {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public void Logout(String userName, String password) {
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            try {
                PreparedStatement  addingToken = connection.prepareStatement("UPDATE USERS SET token = ? WHERE email = ?");
                addingToken.setInt(1, -1);
                addingToken.setString(2, userName);
                int updatedRows = addingToken.executeUpdate();

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
