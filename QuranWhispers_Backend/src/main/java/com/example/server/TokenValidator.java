package com.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TokenValidator {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public boolean VALIDATE(String email,int valueOfToken){
        //testing;

        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = connection.prepareStatement("SELECT token FROM USERS WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if(valueOfToken == rs.getInt("token")) {
                    return true;
                }
                else{
                    return false;
                }
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
