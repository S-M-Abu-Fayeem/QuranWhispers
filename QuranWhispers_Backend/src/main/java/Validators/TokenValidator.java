package Validators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TokenValidator {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public synchronized boolean VALIDATE(String email, int valueOfToken) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = connection.prepareStatement("SELECT token FROM USERS WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int dbToken = rs.getInt("token");
                //System.out.println("Validating token. Email: " + email + ", Received: " + valueOfToken + ", In DB: " + dbToken);
                return valueOfToken == dbToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
