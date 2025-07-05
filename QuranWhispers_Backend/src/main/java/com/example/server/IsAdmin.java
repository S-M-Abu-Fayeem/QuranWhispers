package com.example.server;

import java.sql.*;

public class IsAdmin {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public boolean isAdmin(String email) {
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            try {
                PreparedStatement isAdmin = connection.prepareStatement("SELECT is_admin FROM USERS WHERE email = ?");
                isAdmin.setString(1, email);
                ResultSet Admin = isAdmin.executeQuery();
                Admin.next();
                if(Admin.getBoolean("is_admin")) {
                   return true;
                }
                else{
                    return false;
                }
            }
            catch(Exception e) {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

}
