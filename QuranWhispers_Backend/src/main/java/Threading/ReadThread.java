package Threading;

import com.example.server.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import shared.FilePacket;

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
                            String reciterName = json.get("reciter_name").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            response = adminController.approveRecitation(email, token, reciterName, surah, ayah);
                        }
                        case "disapproverecitation" -> {
                            int token = json.get("token").getAsInt();
                            String reciterName = json.get("reciter_name").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            response = adminController.disapproveRecitations(email, token, reciterName, surah, ayah);
                        }
                        case "deleteapprovedrecitation" -> {
                            int token = json.get("token").getAsInt();
                            String reciterName = json.get("reciter_name").getAsString();
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
                            JsonObject res = new JsonObject();
                            res.addProperty("email", email);

                            int token = json.get("token").getAsInt();
                            String filename = json.get("filename").getAsString();
                            String reciterName = json.get("reciter_name").getAsString();
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();

                            Socket socket = socketWrapper.getSocket();
                            PrintWriter writer = socketWrapper.getWriter();

                            if (tokenValidator.VALIDATE(email, token)) {
                                try {
                                    // Step 1: Send acknowledgment
                                    writer.println("READY_TO_RECEIVE");
                                    writer.flush();

                                    // Step 2: Receive FilePacket
                                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                                    Object obj = ois.readObject();

                                    if (obj instanceof FilePacket packet) {
                                        System.out.println("📦 Received file from " + packet.getEmail());

                                        // Step 3: Save to DB
                                        try (Connection conn = DriverManager.getConnection(DB_URL)) {

                                            // Step 1: Check for duplicate based on reciter_name, surah, and ayah
                                            PreparedStatement checkStmt = conn.prepareStatement(
                                                    "SELECT COUNT(*) FROM PendingRecitations WHERE reciter_name = ? AND surah = ? AND ayah = ?"
                                            );
                                            checkStmt.setString(1, packet.getReciterName());
                                            checkStmt.setString(2, packet.getSurah());
                                            checkStmt.setString(3, packet.getAyah());
                                            ResultSet rs = checkStmt.executeQuery();

                                            if (rs.next() && rs.getInt(1) > 0) {
                                                System.out.println("⚠️ Duplicate recitation already exists. Skipping insert.");
                                                socketWrapper.write("{\"status\":\"409\",\"message\":\"Recitation already exists for this reciter, surah, and ayah.\"}");
                                            } else {
                                                // Step 2: Insert new record
                                                PreparedStatement stmt = conn.prepareStatement(
                                                        "INSERT INTO PendingRecitations (uploader_email, reciter_name, surah, ayah, file_name, audio_data) VALUES (?, ?, ?, ?, ?, ?)"
                                                );
                                                stmt.setString(1, packet.getEmail());
                                                stmt.setString(2, packet.getReciterName());
                                                stmt.setString(3, packet.getSurah());
                                                stmt.setString(4, packet.getAyah());
                                                stmt.setString(5, packet.getFilename());
                                                stmt.setBinaryStream(6, new ByteArrayInputStream(packet.getFileData()), packet.getFileData().length);
                                                stmt.executeUpdate();

                                                System.out.println("✅ Recitation inserted successfully.");
                                                socketWrapper.write("{\"status\":\"200\",\"message\":\"Upload successful\"}");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            socketWrapper.write("{\"status\":\"500\",\"error\":\"" + e.getMessage() + "\"}");
                                        }

                                    } else {
                                        socketWrapper.write("{\"status\":\"400\",\"error\":\"Invalid object received\"}");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    socketWrapper.write("{\"status\":\"500\",\"error\":\"" + e.getMessage() + "\"}");
                                }

                            } else {
                                JsonObject err = new JsonObject();
                                err.addProperty("status", "unauthorized");
                                writer.println(err.toString());
                                writer.flush();
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
                            int reply_chat_id = json.get("reply_chat_id").getAsInt(); // 0
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
                        case "getayahrecitations" -> {
                            String token = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(token);
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            response = adminController.getAyahRecitations(email,valueOfToken,surah,ayah);
                        }
                        case "getpendingrecitations" -> {
                            String token = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(token);
                            response = adminController.getPendingRecitations(email,valueOfToken);
                        }
                        case "getapprovedrecitations" -> {
                            String token = json.get("token").getAsString();
                            int valueOfToken = Integer.parseInt(token);
                            response = adminController.getApprovedRecitations(email, valueOfToken);
                        }
                        case "listenpendingrecitation" -> {
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String reciterName = json.get("reciter_name").getAsString();
                            int token = json.get("token").getAsInt();
                            Socket socket = socketWrapper.getSocket();
                            PrintWriter writer = socketWrapper.getWriter();

                            if (!new TokenValidator().VALIDATE(email, token)) {
                                JsonObject res = new JsonObject();
                                res.addProperty("status", "401");
                                writer.println(res.toString());
                                writer.flush();
                                break;
                            }

                            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                                PreparedStatement ps = conn.prepareStatement(
                                        "SELECT file_name, audio_data, uploader_email, reciter_name, surah, ayah " +
                                                "FROM PendingRecitations WHERE reciter_name = ? AND surah = ? AND ayah = ? LIMIT 1"
                                );
                                ps.setString(1, reciterName);
                                ps.setString(2, surah);
                                ps.setString(3, ayah);
                                ResultSet rs = ps.executeQuery();

                                if (rs.next()) {
                                    // Step 1: Acknowledge ready to send
                                    writer.println("READY_TO_RECEIVE");
                                    writer.flush();

                                    // Step 2: Send the FilePacket
                                    String filename = rs.getString("file_name");
                                    String uploaderEmail = rs.getString("uploader_email");
                                    byte[] fileBytes;

                                    try (InputStream is = rs.getBinaryStream("audio_data");
                                         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                        byte[] buffer = new byte[4096];
                                        int read;
                                        while ((read = is.read(buffer)) != -1) {
                                            baos.write(buffer, 0, read);
                                        }
                                        fileBytes = baos.toByteArray();
                                    }

                                    FilePacket packet = new FilePacket(
                                            uploaderEmail, reciterName, surah, ayah, filename, fileBytes
                                    );

                                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                    oos.writeObject(packet);
                                    oos.flush();
                                    System.out.println("✅ FilePacket sent to client.");

                                    // Step 3: Send final confirmation JSON
                                    JsonObject finalAck = new JsonObject();
                                    finalAck.addProperty("status", "200");
                                    finalAck.addProperty("message", "File transfer complete");
                                    writer.println(finalAck.toString());
                                    writer.flush();

                                } else {
                                    JsonObject notFound = new JsonObject();
                                    notFound.addProperty("status", "404");
                                    writer.println(notFound.toString());
                                    writer.flush();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                JsonObject error = new JsonObject();
                                error.addProperty("status", "500");
                                error.addProperty("error", e.getMessage());
                                writer.println(error.toString());
                                writer.flush();
                            }
                        }
                        case "listenapprovedrecitation" -> {
                            String surah = json.get("surah").getAsString();
                            String ayah = json.get("ayah").getAsString();
                            String reciterName = json.get("reciter_name").getAsString();
                            int token = json.get("token").getAsInt();
                            Socket socket = socketWrapper.getSocket();
                            PrintWriter writer = socketWrapper.getWriter();

                            if (!new TokenValidator().VALIDATE(email, token)) {
                                JsonObject res = new JsonObject();
                                res.addProperty("status", "401");
                                writer.println(res.toString());
                                writer.flush();
                                break;
                            }

                            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                                PreparedStatement ps = conn.prepareStatement(
                                        "SELECT file_name, audio_data, uploader_email, reciter_name, surah, ayah " +
                                                "FROM Recitations WHERE reciter_name = ? AND surah = ? AND ayah = ? LIMIT 1"
                                );
                                ps.setString(1, reciterName);
                                ps.setString(2, surah);
                                ps.setString(3, ayah);
                                ResultSet rs = ps.executeQuery();

                                if (rs.next()) {
                                    // Step 1: Acknowledge ready to send
                                    writer.println("READY_TO_RECEIVE");
                                    writer.flush();

                                    // Step 2: Send the FilePacket
                                    String filename = rs.getString("file_name");
                                    String uploaderEmail = rs.getString("uploader_email");
                                    byte[] fileBytes;

                                    try (InputStream is = rs.getBinaryStream("audio_data");
                                         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                        byte[] buffer = new byte[4096];
                                        int read;
                                        while ((read = is.read(buffer)) != -1) {
                                            baos.write(buffer, 0, read);
                                        }
                                        fileBytes = baos.toByteArray();
                                    }

                                    FilePacket packet = new FilePacket(
                                            uploaderEmail, reciterName, surah, ayah, filename, fileBytes
                                    );

                                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                    oos.writeObject(packet);
                                    oos.flush();
                                    System.out.println("✅ FilePacket sent to client.");

                                    // Step 3: Send final confirmation JSON
                                    JsonObject finalAck = new JsonObject();
                                    finalAck.addProperty("status", "200");
                                    finalAck.addProperty("message", "File transfer complete");
                                    writer.println(finalAck.toString());
                                    writer.flush();

                                } else {
                                    JsonObject notFound = new JsonObject();
                                    notFound.addProperty("status", "404");
                                    writer.println(notFound.toString());
                                    writer.flush();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                JsonObject error = new JsonObject();
                                error.addProperty("status", "500");
                                error.addProperty("error", e.getMessage());
                                writer.println(error.toString());
                                writer.flush();
                            }
                        }

                        default -> {
                            JsonObject error = new JsonObject();
                            error.addProperty("status", "401");
                            response = gson.toJson(error);
                        }
                    }
                    System.out.println(response);
                    socketWrapper.write(response);

                } catch (Exception innerEx) {
                    System.err.println("Processing error: " + innerEx.getMessage());
                    innerEx.printStackTrace();
                }
            }
            //logOut.Logout(email, json.get("token").getAsInt());
            //System.out.println("Client disconnected.");

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

