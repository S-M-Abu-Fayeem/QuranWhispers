package Threading;

import com.example.server.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.Socket;
import java.sql.*;

import static com.example.server.HelloApplication.*;

public class ReadThread implements Runnable {
    private final Thread t;
    private final SocketWrapper socketWrapper;
    private static final Gson gson = new Gson();
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";

    public ReadThread(SocketWrapper socketWrapper) {
        this.socketWrapper = socketWrapper;
        this.t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {


        try {
            String line;
            while ((line = socketWrapper.read()) != null) {
                System.out.println("Client: " + line);
                System.out.println(Thread.currentThread().getName());
                System.out.println("Active Threads: " + Thread.activeCount());

                JsonObject json;
                try {
                    json = gson.fromJson(line, JsonObject.class);
                } catch (JsonSyntaxException e) {
                    System.out.println(e.getMessage());
                    continue;
                }

                String action = json.get("action").getAsString();
                String email = json.get("email").getAsString();
                String response="";
                System.out.println(email);

                try {
                    switch (action.toLowerCase()) {
                        case "login" -> {
                            String password = json.get("password").getAsString();
                            response = login.GET(email, password);
                        }
                        case "register" -> {
                            String password = json.get("password").getAsString();
                            String username = json.get("username").getAsString();
                            response = register.GET(email, username, password);
                        }
                        case "getinfo" -> {
                            int token = json.get("token").getAsInt();
                            response = userInfoGetter.GET(email, token);
                        }
                        case "addtofavorite", "addtofavourites" -> {
                            int token = json.get("token").getAsInt();
                            String emotion = json.get("emotion").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = addFavVerse.SET(email, token, emotion, theme, Integer.parseInt(ayah), surah);
                        }
                        case "rmvfavverse" -> {
                            int token = json.get("token").getAsInt();
                            String emotion = json.get("emotion").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = removeVerse.DELETE(email, token, emotion, theme, Integer.parseInt(ayah), surah);
                        }
                        case "sendtofriend" -> {
                            int token = json.get("token").getAsInt();
                            String friendUsername = json.get("friendusername").getAsString();
                            String emotion = json.get("emotion").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = receivedVerseControll.SEND(email, token, friendUsername, emotion, theme, Integer.parseInt(ayah), surah);
                        }
                        case "adddua" -> {
                            int token = json.get("token").getAsInt();
                            String title = json.get("title").getAsString();
                            String english = json.get("english_body").getAsString();
                            String arabic = json.get("arabic_body").getAsString();
                            response = addDua.SET_DUA(email, token, title, arabic, english);
                        }
                        case "addverse" -> {
                            int token = json.get("token").getAsInt();
                            String surah = json.get("surah").getAsString();
                            String verse = json.get("ayah").getAsString();
                            String emotion = json.get("emotion").getAsString();
                            String theme = json.get("theme").getAsString();
                            response = addDua.SET_THEME_MOOD(email, token, theme, emotion, Integer.parseInt(verse), surah);
                        }
                        case "getduaoftheday" -> response = generatingDuaOfTheDay.getDua();
                        case "generateemotionbasedverse" -> {
                            int token = json.get("token").getAsInt();
                            String emotion = json.get("emotion").getAsString();
                            response = randomizedSelection.generateMoodBased(email, token, emotion);
                        }
                        case "generatethemebasedverse" -> {
                            int token = json.get("token").getAsInt();
                            String theme = json.get("theme").getAsString();
                            response = randomizedSelection.generateThemeBased(email, token, theme);
                        }
                        case "deleteverse" -> {
                            int token = json.get("token").getAsInt();
                            String emotion = json.get("emotion").getAsString();
                            String theme = json.get("theme").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String surah = json.get("surah").getAsString();
                            response = adminController.DELETE_VERSE(email, token, emotion, theme, ayah, surah);
                        }
                        case "deletedua" -> {
                            int token = json.get("token").getAsInt();
                            String title = json.get("title").getAsString();
                            response = adminController.DELETE_DUA(email, token, title);
                        }
                        case "deleteuser" -> {
                            int token = json.get("token").getAsInt();
                            String userEmail = json.get("useremail").getAsString();
                            response = adminController.DELETE_USER(email, token, userEmail);
                        }
                        case "approverecitation" -> {
                            int token = json.get("token").getAsInt();
                            String reciterName = json.get("recitername").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            response = adminController.approveRecitation(email, token, reciterName, surah, ayah);
                        }
                        case "disapproverecitation" -> {
                            int token = json.get("token").getAsInt();
                            String reciterName = json.get("recitername").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            response = adminController.DISAPPROVE_RECITATION(email, token, reciterName, surah, ayah);
                        }
                        case "deleteapprovedrecitation" -> {
                            int token = json.get("token").getAsInt();
                            String reciterName = json.get("recitername").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            response = adminController.DELETE_APPROVED_RECITATION(email, token, reciterName, surah, ayah);
                        }
                        case "logout" -> response = logOut.Logout(email, json.get("token").getAsInt());
                        case "getlist" -> response = getList.GET(email, json.get("token").getAsInt());
                        case "getprofileinfo" -> response = getProfileInfo.GET(email, json.get("token").getAsInt());
                        case "getreceivedinfo" -> response = getRecieveInfo.GET(email, json.get("token").getAsInt());
                        case "deletereceivedverse" -> {
                            int token = json.get("token").getAsInt();
                            String senderUsername = json.get("sender_username").getAsString();
                            String surah = json.get("surah").getAsString();
                            int ayah = json.get("ayah").getAsInt();
                            String theme = json.get("theme").getAsString();
                            String emotion = json.get("emotion").getAsString();
                            response = receivedVerseControll.DELETE(email, token, senderUsername, surah, ayah, theme, emotion);
                        }
                        case "generateapibasedverse" -> {
                            int token = json.get("token").getAsInt();
                            String text = json.get("text").getAsString();
                            response = apiBasedVerseGenerator.generate(email, token, text);
                        }
                        case "getallusers" -> response = adminController.getAllusers(email, json.get("token").getAsInt());
                        case "getallverses" -> response = adminController.getAllVerses(email, json.get("token").getAsInt());
                        case "getallduas" -> response = adminController.getAllDuas(email, json.get("token").getAsInt());
                        case "getallpendingrecitations" -> response = adminController.getAllPendingRecitations(email, json.get("token").getAsInt());
                        case "uploadmp3" -> {
                            TokenValidator tokenValidator = new TokenValidator();
                            IsAdmin isAdmin = new IsAdmin();
                            JsonObject res = new JsonObject();
                            res.addProperty("email", email);

                            int token = json.get("token").getAsInt();
                            String filename = json.get("filename").getAsString();
                            long filesize = json.get("filesize").getAsLong();
                            String reciterName = json.get("reciter_name").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();

                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketWrapper.getSocket().getOutputStream()));
                            Socket socket = socketWrapper.getSocket();

                            if (tokenValidator.VALIDATE(email, token)) {
                                writer.write("READY_TO_RECEIVE");
                                writer.newLine();
                                writer.flush();

                                try {
                                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                                    long fileSize = dis.readLong();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer = new byte[4096];
                                    long bytesReceived = 0;
                                    int read;
                                    while (bytesReceived < fileSize && (read = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize - bytesReceived))) != -1) {
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
                        }
                        case "listenapporvedrecitation" -> {
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            JsonObject res = new JsonObject();
                            int token = json.get("token").getAsInt();
                            Socket socket = socketWrapper.getSocket();

                            if (new TokenValidator().VALIDATE(email, token)) {
                                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                                    PreparedStatement ps = conn.prepareStatement(
                                            "SELECT audio_data FROM Recitations WHERE surah = ? AND ayah = ? ORDER BY RAND() LIMIT 1"
                                    );
                                    ps.setString(1, surah);
                                    ps.setString(2, ayah);
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
                                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                                        dos.writeLong(audioBytes.length); // first send length
                                        dos.write(audioBytes);            // then send actual audio
                                        dos.flush();
                                        System.out.println("Audio sent: " + audioBytes.length + " bytes");
                                    } else {
                                        res.addProperty("status", "404");
                                        socketWrapper.write(res.toString());
                                    }

                                } catch (Exception e) {
                                    res.addProperty("status", "500");
                                    res.addProperty("error", e.getMessage());
                                    socketWrapper.write(res.toString());
                                }
                            } else {
                                res.addProperty("status", "401");
                                socketWrapper.write(res.toString());
                            }
                        }
                        case  "listenpendingrecitation" ->{
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String recitername = json.get("recitername").getAsString();
                            JsonObject res = new JsonObject();
                            int token = json.get("token").getAsInt();
                            Socket socket = socketWrapper.getSocket();

                            if (new TokenValidator().VALIDATE(email, token)) {
                                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                                    PreparedStatement ps = conn.prepareStatement(
                                            "SELECT audio_data FROM PendingRecitations WHERE reciter_name = ? AND surah = ? AND ayah = ? ORDER BY RAND() LIMIT 1"
                                    );
                                    ps.setString(1, recitername);
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
                                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                                        dos.writeLong(audioBytes.length); // first send length
                                        dos.write(audioBytes);            // then send actual audio
                                        dos.flush();
                                        System.out.println("Audio sent: " + audioBytes.length + " bytes");
                                    } else {
                                        res.addProperty("status", "404");
                                        socketWrapper.write(res.toString());
                                    }

                                } catch (Exception e) {
                                    res.addProperty("status", "500");
                                    res.addProperty("error", e.getMessage());
                                    socketWrapper.write(res.toString());
                                }
                            } else {
                                res.addProperty("status", "401");
                                socketWrapper.write(res.toString());
                            }

                        }
                        case "banuser" -> {
                            String username = json.get("username").getAsString();
                            int token = json.get("token").getAsInt();
                            response = forumMaintenance.banUser(email,token,username);
                        }
                        case "unbanuser" -> {
                            String username = json.get("username").getAsString();
                            int token = json.get("token").getAsInt();
                            response = forumMaintenance.unbanUser(email,token,username);
                        }
                        case "registerchat" -> {
                            String receiver_username = json.get("receiver_username").getAsString();
                            String sender_username = json.get("sender_username").getAsString();
                            String body = json.get("body").getAsString();
                            int token = json.get("token").getAsInt();
                            String type = json.get("type").getAsString();
                            int reply_chat_id = json.get("reply_chat_id").getAsInt();
                            String surah = json.get("surah").getAsString();
                            int ayah = json.get("ayah").getAsInt();
                            //String text = json.get("text").getAsString();
                            response = forumMaintenance.registerChat(email, token,sender_username, receiver_username, body ,   type, reply_chat_id, surah, ayah);
                        }

                        case "clearchathistory" -> {
                            int token = json.get("token").getAsInt();
                            response = forumMaintenance.clearChatHistory(email, token);
                        }
                        case "deletelatest" -> {
                            int token = Integer.parseInt(json.get("token").getAsString());
                            System.out.println("token done");
                            int no = Integer.parseInt(json.get("number_of_chats").getAsString());
                            response = forumMaintenance.deleteLatest(email, token, no);
                        }

                        case "deletechatbyid" -> {
                            int id = json.get("id").getAsInt();
                            int token = json.get("token").getAsInt();
                            response = forumMaintenance.deleteChatById(email, token, id);
                        }

                        case "retrievechat" -> {
                            int token = json.get("token").getAsInt();
                            response = forumMaintenance.getChats(email, token);
                        }

                        case "askai" -> {
                            int token = json.get("token").getAsInt();
                            String question = json.get("question").getAsString();
                            response = forumMaintenance.askAI(email, token, question);
                        }


                        default -> {
                            JsonObject error = new JsonObject();
                            error.addProperty("status", "401");
                            response = gson.toJson(error);
                        }
                    }


                    socketWrapper.write(response);

                } catch (Exception innerEx) {
                    System.err.println("Processing error: " + innerEx.getMessage());
                    innerEx.printStackTrace();
                }
            }
            //logOut.Logout(email, json.get("token").getAsInt());
            System.out.println("Client disconnected.");

        } catch (IOException e) {
            System.err.println("I/O error in ReadThread: " + e.getMessage());
        } finally {
            try {
                connectedClients.remove(socketWrapper);
                socketWrapper.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}

