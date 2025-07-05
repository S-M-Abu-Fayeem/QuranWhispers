package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HelloApplication {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public static void main(String[] args) {
        Gson gson = new Gson();
        Login login = new Login();
        Register register = new Register();
        UserInfoGetter userInfoGetter = new UserInfoGetter();
        AddFavVerse addFavVerse = new AddFavVerse();
        RemoveVerse removeVerse = new RemoveVerse();
        SendVerseToFriend sendVerseToFriend = new SendVerseToFriend();
        AddDua addDua = new AddDua();
        GeneratingDuaOfTheDay generatingDuaOfTheDay = new GeneratingDuaOfTheDay();
        AdminController adminController = new AdminController();
        //TokenValidator tokenValidator = new TokenValidator();
        RandomizedSelection randomizedSelection = new RandomizedSelection();
        //AdminController adminController1 = new AdminController();
        LogOut logOut = new LogOut();

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server running.........");

            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
                ) {
                    generatingDuaOfTheDay.getDua();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Client: " + line);

                        //parsing json file
                        JsonObject json = gson.fromJson(line, JsonObject.class);
                        String action = json.get("action").getAsString();
                        String email = json.get("email").getAsString();
                        System.out.println("action: " + action);
                        // checking  action
                        String response;
                        if (action.equalsIgnoreCase("login")) {
                            String password = json.get("password").getAsString();
                            response = login.GET(email, password);
                        } else if (action.equalsIgnoreCase("register")) {

                            String password = json.get("password").getAsString();
                            String username = json.get("username").getAsString();
                            //System.out.println(json.get("name").getAsString());
                            System.out.println(email + ": " + password + ": " + username);
                            response = register.GET(email,username, password);
                        }
                        else if(action.equalsIgnoreCase("getinfo")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            response = userInfoGetter.GET(email, valueOfToken);
                        }
                        else if(action.equalsIgnoreCase("addtofavorite") || action.equalsIgnoreCase("addtofavourites")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String emotion = json.get("emotion").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = addFavVerse.SET(email, valueOfToken, emotion, theme, Integer.parseInt(ayah), surah);
                        }
                        else if(action.equalsIgnoreCase("rmvfavverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String emotion = json.get("emotion").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = removeVerse.DELETE(email, valueOfToken, emotion, theme, Integer.parseInt(ayah), surah);

                        }
                        else if(action.equalsIgnoreCase("sendToFriend")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String emotion = json.get("emotion").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            String friendUserName = json.get("friendusername").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = sendVerseToFriend.SEND(email,valueOfToken,friendUserName, emotion,theme,  Integer.parseInt(ayah) ,surah);
                        }
                        else if(action.equalsIgnoreCase("adddua")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String title = json.get("title").getAsString();
                            String english = json.get("englishbody").getAsString();
                           // String bangla = json.get("banglabody").getAsString();
                            String arabic = json.get("arabicbody").getAsString();
                            response = addDua.SET_DUA(email,valueOfToken, title, arabic, english);
                        }
                        else if(action.equalsIgnoreCase("addverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String surah = json.get("surah").getAsString();
                            String verse = json.get("ayah").getAsString();
                            String emotion = json.get("emotion").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = addDua.SET_THEME_MOOD(email,valueOfToken, theme, emotion, Integer.parseInt(verse), surah);
                        }
                        else if(action.equalsIgnoreCase("getduaoftheday")) {
                            System.out.println("Here is the duaoftheday");
                            response = generatingDuaOfTheDay.getDua();
                        }
                        else if(action.equalsIgnoreCase("generateemotionbasedverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String emotion = json.get("emotion").getAsString();
                            response = randomizedSelection.generateMoodBased(email,valueOfToken, emotion);
                        }
                        else if(action.equalsIgnoreCase("generatethemebasedverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String theme = json.get("theme").getAsString();
                            response = randomizedSelection.generateThemeBased(email,valueOfToken, theme);
                        }
                        else if(action.equalsIgnoreCase("deleteverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String emotion = json.get("emotion").getAsString();
                            String theme = json.get("theme").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            response = adminController.DELETE_VERSE(email,valueOfToken,emotion,theme,ayah,surah);
                        }
                        else if(action.equalsIgnoreCase("deletedua")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String title = json.get("title").getAsString();
                            response = adminController.DELETE_DUA(email,valueOfToken,title);
                        }
                        else if(action.equalsIgnoreCase("deleteuser")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String userEmail= json.get("useremail").getAsString();
                            response = adminController.DELETE_USER(email,valueOfToken,userEmail);
                        }
                        else if(action.equalsIgnoreCase("getallinfo")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            response = adminController.getAllInfo(email,valueOfToken);

                        }
                        else if(action.equalsIgnoreCase("approverecitation")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String recitername = json.get("recitername").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            response = adminController.approveRecitation(email, valueOfToken, recitername, surah, ayah);
                        }
                        else if (action.equalsIgnoreCase("uploadmp3")) {
                            TokenValidator tokenValidator = new TokenValidator();
                            IsAdmin isAdmin = new IsAdmin();
                            JsonObject res = new JsonObject();
                            res.addProperty("email", email);

                            String tokenStr = json.get("token").getAsString();
                            int token = Integer.parseInt(tokenStr);
                            String filename = json.get("filename").getAsString();
                            long filesize = Long.parseLong(json.get("filesize").getAsString());

                            String reciterName = json.get("reciter_name").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();

                            if (tokenValidator.VALIDATE(email, token) && isAdmin.isAdmin(email)) {
                                // 🔁 Acknowledge header receipt BEFORE reading file
                                writer.write("READY_TO_RECEIVE");
                                writer.newLine();
                                writer.flush();

                                try {
                                    InputStream is = socket.getInputStream();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[4096];
                                    long bytesReceived = 0;
                                    int read;
                                    while (bytesReceived < filesize && (read = is.read(buffer)) != -1) {
                                        baos.write(buffer, 0, read);
                                        bytesReceived += read;
                                    }

                                    try (Connection conn = DriverManager.getConnection(DB_URL)) {
                                        PreparedStatement stmt = conn.prepareStatement(
                                                "INSERT INTO PendingRecitations (uploader_email, reciter_name, surah, ayah, file_name, audio_data) VALUES (?, ?, ?, ?, ?, ?)"
                                        );
                                        stmt.setString(1, email);
                                        stmt.setString(2, reciterName);
                                        stmt.setString(3, surah);
                                        stmt.setString(4, ayah);
                                        stmt.setString(5, filename);
                                        stmt.setBinaryStream(6, new ByteArrayInputStream(baos.toByteArray()), baos.size());
                                        stmt.executeUpdate();

                                        res.addProperty("status", "200");
                                    } catch (Exception e) {
                                        res.addProperty("status", "db_error");
                                        res.addProperty("error", e.getMessage());
                                    }

                                } catch (IOException e) {
                                    res.addProperty("status", "io_error");
                                    res.addProperty("error", e.getMessage());
                                }
                            } else {
                                res.addProperty("status", "unauthorized");
                            }

                            writer.write(res.toString());
                            writer.newLine();
                            writer.flush();
                            System.out.println("Receiving done");
                            continue;  // ✅ Prevent double response
                        }

                        else if (action.equals("listenrecitation")) {
                            String reciter = json.get("reciter").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            TokenValidator tokenValidator = new TokenValidator();
                            JsonObject res = new JsonObject();

                            if (tokenValidator.VALIDATE(email, Integer.parseInt(json.get("token").getAsString()))) {
                                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                                    PreparedStatement ps = conn.prepareStatement("SELECT audio_data FROM Recitations WHERE reciter_name = ? AND surah = ? AND ayah = ? LIMIT 1");
                                    ps.setString(1, reciter);
                                    ps.setString(2, surah);
                                    ps.setString(3, ayah);
                                    ResultSet rs = ps.executeQuery();

                                    if (rs.next()) {
                                        InputStream audioStream = rs.getBinaryStream("audio_data");
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        byte[] buffer = new byte[4096];
                                        int read;
                                        while ((read = audioStream.read(buffer)) != -1) {
                                            baos.write(buffer, 0, read);
                                        }
                                        byte[] audioBytes = baos.toByteArray();

                                        // Send file size then raw audio bytes
                                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                                        dos.writeLong(audioBytes.length);
                                        dos.write(audioBytes);
                                        dos.flush();
                                        continue;
                                    } else {
                                        res.addProperty("status", "404");
                                    }
                                } catch (Exception e) {
                                    res.addProperty("status", "500");

                                }
                            } else {
                                res.addProperty("status", "401");
                            }

                            writer.write(res.toString());
                            writer.newLine();
                            writer.flush();
                            continue;
                        }
                        else if(action.equals("deletuser")) {
                            int value = Integer.parseInt(json.get("value").getAsString());
                            response=adminController.DELETE_USER(email,value, json.get("useremail").getAsString());
                        }
                        else if(action.equals("deletedua")){
                            int value = Integer.parseInt(json.get("value").getAsString());
                            response = adminController.DELETE_DUA(email,value, json.get("title").getAsString());
                        }
                        else if(action.equals("deleteverse")) {
                            int value = Integer.parseInt(json.get("value").getAsString());
                            String emotion = json.get("emotion").getAsString();
                            String theme = json.get("theme").getAsString();
                            String ayah =json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();

                            response = adminController.DELETE_VERSE(email, value, emotion, theme, ayah, surah);
                        }
                        else if (action.equals("disapproverecitation")) {
                            int value = Integer.parseInt(json.get("value").getAsString());
                            String reciterName = json.get("recitername").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();

                            response = adminController.DISAPPROVE_RECITATION(email, value, reciterName, surah, ayah);
                        }
                        else if (action.equals("deleteapprovedrecitation")) {
                            int value = Integer.parseInt(json.get("value").getAsString());
                            String reciterName = json.get("recitername").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();

                            response = adminController.DELETE_APPROVED_RECITATION(email, value, reciterName, surah, ayah);
                        }
                        else if(action.equalsIgnoreCase("logout")){
                            response = logOut.Logout(json.get("email").getAsString(),Integer.parseInt(json.get("token").getAsString()));
                        }
                        else {
                            JsonObject error = new JsonObject();
                            error.addProperty("status", "Invalid action");
                            response = gson.toJson(error);
                        }

                        //Sending response
                        writer.write(response);
                        writer.newLine();
                        writer.flush();
                    }
                } catch (Exception e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
