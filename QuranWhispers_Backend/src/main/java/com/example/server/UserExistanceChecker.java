package com.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserExistanceChecker {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public synchronized boolean doesExist(String username) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {

                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT id FROM USERS WHERE username = ?");
                preparedStatement1.setString(1, username);
                ResultSet resultSet = preparedStatement1.executeQuery();
                if(resultSet.next()) {
                    return true;
                }
                else{
                    return false;
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return false;

    }
}

