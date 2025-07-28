package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import static com.example.server.HelloApplication.tokenValidator;

public class AddFavVerse {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public String SET(String email, int valueOfToken, String emotion, String theme, int ayah, String surah) {
        //TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        if (tokenValidator.VALIDATE(email, valueOfToken)) {
            try (Connection connection = DriverManager.getConnection(DB_URL)) {

                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT id FROM USERS WHERE email = ?");
                preparedStatement1.setString(1, email);
                ResultSet resultSet = preparedStatement1.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");

                    PreparedStatement checkDuplicate = connection.prepareStatement(
                            "SELECT id FROM FAV_VERSE WHERE user_id = ? AND surah = ? AND ayah = ?"
                    );
                    checkDuplicate.setInt(1, userId);
                    checkDuplicate.setString(2, surah);
                    checkDuplicate.setInt(3, ayah);
                    ResultSet duplicateCheck = checkDuplicate.executeQuery();

                    if (duplicateCheck.next()) {
                        data.addProperty("status", "409");
                        data.addProperty("status_message", "You've already added this verse to your favorites.");
                        return gson.toJson(data);
                    }

                    PreparedStatement preparedStatement2 = connection.prepareStatement(
                            "INSERT INTO FAV_VERSE (user_id, emotion, theme, ayah, surah, timestamp) VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    preparedStatement2.setInt(1, userId);
                    preparedStatement2.setString(2, emotion);
                    preparedStatement2.setString(3, theme);
                    preparedStatement2.setInt(4, ayah);
                    preparedStatement2.setString(5, surah);
                    preparedStatement2.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

                    int checker = preparedStatement2.executeUpdate();
                    if (checker > 0) {
                        data.addProperty("status", "200");

                        PreparedStatement updateSaved = connection.prepareStatement(
                                "UPDATE USERS SET total_saved_verse = total_saved_verse + 1 WHERE id = ?"
                        );
                        updateSaved.setInt(1, userId);
                        updateSaved.executeUpdate();
                    } else {
                        data.addProperty("status", "500");
                        data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
                        return gson.toJson(data);
                    }
                } else {
                    data.addProperty("status", "401");

                }
            } catch (Exception e) {
                data.addProperty("status", "500");
                data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
                e.printStackTrace();
                return gson.toJson(data);

            }
        } else {
            data.addProperty("status", "404");
        }

        return gson.toJson(data);
    }
}
/*<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>QuranWhispers_Frontend</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>QuranWhispers_Frontend</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>21</java.version>
        <javafx.version>24.0.1</javafx.version>
        <junit.version>5.10.2</junit.version>
    </properties>

    <dependencies>
        <!-- JSON -->
        <dependency>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
            <version>20210307</version>
        </dependency>

        <!-- JavaFX modules -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-media</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- JUnit for Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>

            <!-- JavaFX Maven Plugin -->
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>org.example.quranwhispers_frontend.HelloApplication</mainClass>
                </configuration>
            </plugin>
        </plugins>

        <resources>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>
    </build>
</project>
*/