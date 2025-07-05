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
                            String mood = json.get("mood").getAsString();
                            String ayat = json.get("ayat").getAsString();
                            String surah = json.get("surah").getAsString();
                            response = addFavVerse.SET(email, valueOfToken, mood, Integer.parseInt(ayat), surah);
                        }
                        else if(action.equalsIgnoreCase("rmvfavverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String mood = json.get("mood").getAsString();
                            String ayat = json.get("ayat").getAsString();
                            String surah = json.get("surah").getAsString();
                            response = removeVerse.DELETE(email, valueOfToken, mood, Integer.parseInt(ayat), surah);
                        }
                        else if(action.equalsIgnoreCase("sendToFriend")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String mood = json.get("mood").getAsString();
                            String ayat = json.get("ayat").getAsString();
                            String surah = json.get("surah").getAsString();
                            String friendUserName = json.get("friendusername").getAsString();
                            response = sendVerseToFriend.SEND(email,valueOfToken,friendUserName, mood, Integer.parseInt(ayat) ,surah);
                        }
                        else if(action.equalsIgnoreCase("adddua")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String title = json.get("title").getAsString();
                            String english = json.get("englishbody").getAsString();
                            String bangla = json.get("banglabody").getAsString();
                            String arabic = json.get("arabicbody").getAsString();
                            response = addDua.SET_DUA(email,valueOfToken, title, arabic, english, bangla);
                        }
                        else if(action.equalsIgnoreCase("addverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String surah = json.get("surah").getAsString();
                            String verse = json.get("ayat").getAsString();
                            String mood = json.get("mood").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = addDua.SET_THEME_MOOD(email,valueOfToken, theme, mood, Integer.parseInt(verse), surah);
                        }
                        else if(action.equalsIgnoreCase("getduaoftheday")) {
                            System.out.println("Here is the duaoftheday");
                            response = generatingDuaOfTheDay.getDua();
                        }
                        else if(action.equalsIgnoreCase("generatemoodbasedverse")) {
                            String curToken = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(curToken);
                            String mood = json.get("mood").getAsString();
                            response = randomizedSelection.generateMoodBased(email,valueOfToken, mood);
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
                            String mood = json.get("mood").getAsString();
                            String theme = json.get("theme").getAsString();
                            String ayat = json.get("ayat").getAsString();
                            String surah = json.get("surah").getAsString();
                            response = adminController.DELETE_VERSE(email,valueOfToken,mood,theme,ayat,surah);
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
                            String ayat = json.get("ayat").getAsString();
                            response = adminController.approveRecitation(email, valueOfToken, recitername, surah, ayat);
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
                            String ayat = json.get("ayat").getAsString();

                            if (tokenValidator.VALIDATE(email, token) && isAdmin.isAdmin(email)) {
                                try {
                                    // 🔁 Read file from socket into memory (ByteArrayOutputStream)
                                    InputStream is = socket.getInputStream();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[4096];
                                    long bytesReceived = 0;
                                    int read;
                                    while (bytesReceived < filesize && (read = is.read(buffer)) != -1) {
                                        baos.write(buffer, 0, read);
                                        bytesReceived += read;
                                    }

                                    // ✅ Save directly to DB
                                    try (Connection conn = DriverManager.getConnection("jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'")) {
                                        PreparedStatement stmt = conn.prepareStatement(
                                                "INSERT INTO PendingRecitations (uploader_email, reciter_name, surah, ayat, file_name, audio_data) VALUES (?, ?, ?, ?, ?, ?)"
                                        );
                                        stmt.setString(1, email);
                                        stmt.setString(2, reciterName);
                                        stmt.setString(3, surah);
                                        stmt.setString(4, ayat);
                                        stmt.setString(5, filename);
                                        stmt.setBinaryStream(6, new ByteArrayInputStream(baos.toByteArray()), baos.size());
                                        stmt.executeUpdate();

                                        res.addProperty("status", "success");
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
                            System.out.println("recieving done");
                            Gson gson2 = new Gson();
                            JsonObject dataReturn = new JsonObject();
                            dataReturn.addProperty("email", email);
                            dataReturn.addProperty("status", "success");
                            //continue;
                            response = gson2.toJson(dataReturn);
                        }
                        else if (action.equals("listenrecitation")) {
                            String reciter = json.get("reciter").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayat = json.get("ayat").getAsString();
                            TokenValidator tokenValidator = new TokenValidator();
                            JsonObject res = new JsonObject();

                            if (tokenValidator.VALIDATE(email, Integer.parseInt(json.get("token").getAsString()))) {
                                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                                    PreparedStatement ps = conn.prepareStatement("SELECT audio_data FROM Recitations WHERE reciter_name = ? AND surah = ? AND ayat = ? LIMIT 1");
                                    ps.setString(1, reciter);
                                    ps.setString(2, surah);
                                    ps.setString(3, ayat);
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
                                        res.addProperty("status", "not_found");
                                    }
                                } catch (Exception e) {
                                    res.addProperty("status", "error");
                                    res.addProperty("message", e.getMessage());
                                }
                            } else {
                                res.addProperty("status", "unauthorized");
                            }

                            writer.write(res.toString());
                            writer.newLine();
                            writer.flush();
                            continue;
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
