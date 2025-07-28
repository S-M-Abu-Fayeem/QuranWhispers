package controllers;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static com.example.server.HelloApplication.isAdmin;
import static com.example.server.HelloApplication.tokenValidator;
import static java.lang.Math.min;


public class ForumMaintenance {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public synchronized String registerChat(String email, int token,String senderusername, String receiver_username, String text, String type, Integer reply_chat_id, String surah, Integer ayah) {
        Gson gson = new Gson();
        JsonObject root = new JsonObject();
        //TokenValidator tokenValidator = new TokenValidator();
        root.addProperty("email", email);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (!tokenValidator.VALIDATE(email, token)) {
                root.addProperty("status", "403");
                return gson.toJson(root);
            }
            PreparedStatement getUser = conn.prepareStatement(
                    "SELECT username, active_user FROM USERS WHERE email = ?"
            );
            getUser.setString(1, email);
            ResultSet rs = getUser.executeQuery();

            if (!rs.next()) {
                root.addProperty("status", "404");
                //root.addProperty("message", "Sender user not found.");
                return gson.toJson(root);
            }

            String sender_username = senderusername;

            if (!rs.getBoolean("active_user")) {
                root.addProperty("status", "403");
                root.addProperty("status_message", "You have been banned from the forum page.");
                return gson.toJson(root);
            }




            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO CHATS(sender_username, receiver_username, body, type, reply_chat_id, surah, ayah) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            insert.setString(1, sender_username);
            insert.setString(2, receiver_username);
            insert.setString(3, text);
            insert.setString(4, type);
            if(reply_chat_id != 0) {
                insert.setInt(5, reply_chat_id);
            }
            else{
                insert.setNull(5, java.sql.Types.BIGINT);
            }
            insert.setString(6, surah);
            insert.setInt(7, ayah);
            insert.executeUpdate();
            PreparedStatement countStmt = conn.prepareStatement("SELECT COUNT(*) FROM CHATS");
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next() && countRs.getInt(1) > 100) {

                int deleteCount = countRs.getInt(1) - 100;
                PreparedStatement selectOldest = conn.prepareStatement(
                        "SELECT id FROM CHATS ORDER BY timestamp ASC LIMIT ?"
                );
                selectOldest.setInt(1, deleteCount);
                ResultSet idsToDelete = selectOldest.executeQuery();

                List<Integer> ids = new ArrayList<>();
                while (idsToDelete.next()) {
                    ids.add(idsToDelete.getInt("id"));
                }

                if (!ids.isEmpty()) {
                    String placeholders = ids.stream().map(i -> "?").collect(Collectors.joining(","));
                    PreparedStatement deleteStmt = conn.prepareStatement(
                            "DELETE FROM CHATS WHERE id IN (" + placeholders + ")"
                    );
                    for (int i = 0; i < ids.size(); i++) {
                        deleteStmt.setInt(i + 1, ids.get(i));
                    }
                    deleteStmt.executeUpdate();
                }
            }


            root.addProperty("status", "200");
        } catch (Exception e) {
            e.printStackTrace();
            root.addProperty("status", "500");
            root.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers" );
        }

        return gson.toJson(root);
    }


    public synchronized String banUser(String email, int valueOfToken, String  username) {
        //TokenValidator tokenValidator = new TokenValidator();
        //IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                PreparedStatement qs = connection.prepareStatement("UPDATE USERS SET active_user =  ? WHERE username = ?");
                qs.setBoolean(1,false);
                qs.setString(2, username);
                int count = qs.executeUpdate();
                if(count > 0) {
                    data.addProperty("status", "200");
                }
                else{
                    data.addProperty("status", "500");
                }
            }
            else{
                data.addProperty("status", "401");
            }
        }
        catch(Exception e){
            data.addProperty("status", "500");
        }
        return gson.toJson(data);
    }

    public synchronized String unbanUser(String email, int valueOfToken, String username) {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {

                PreparedStatement qs = connection.prepareStatement(
                        "UPDATE USERS SET active_user = ? WHERE LOWER(username) = LOWER(?)"
                );
                qs.setBoolean(1, true);
                qs.setString(2, username.toLowerCase());

                int count = qs.executeUpdate();
                if (count > 0) {
                    data.addProperty("status", "200");
                    data.addProperty("status_message", "User unbanned successfully.");
                } else {
                    data.addProperty("status", "404");
                    data.addProperty("status_message", "No user found with that username.");
                }
            } else {
                data.addProperty("status", "401");
                data.addProperty("status_message", "Unauthorized.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "500");
            data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
        }

        return gson.toJson(data);
    }


    public synchronized String deleteChatById(String email, int token, int chatIdToDelete) {
        //TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (tokenValidator.VALIDATE(email, token)) {
                PreparedStatement nullifyReply = conn.prepareStatement(
                        "UPDATE CHATS SET reply_chat_id = NULL WHERE reply_chat_id = ?"
                );
                nullifyReply.setInt(1, chatIdToDelete);
                nullifyReply.executeUpdate();

                PreparedStatement deleteStmt = conn.prepareStatement(
                        "DELETE FROM CHATS WHERE id = ?"
                );
                deleteStmt.setInt(1, chatIdToDelete);
                int deletedRows = deleteStmt.executeUpdate();

                if (deletedRows > 0) {
                    data.addProperty("status", "200");
                } else {
                    data.addProperty("status", "404");
                }
            } else {
                data.addProperty("status", "401");
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.addProperty("status", "500");
            data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
        }

        return gson.toJson(data);
    }


    public synchronized String clearChatHistory(String email, int valueOfToken) {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                PreparedStatement deleteAll = connection.prepareStatement("DELETE FROM CHATS");
                int count = deleteAll.executeUpdate();

                PreparedStatement resetId = connection.prepareStatement("ALTER TABLE CHATS ALTER COLUMN ID RESTART WITH 1");
                resetId.executeUpdate();

                data.addProperty("status", "200");
                return gson.toJson(data);
            } else {
                data.addProperty("status", "401");
                return gson.toJson(data);
            }
        } catch (Exception e) {
            data.addProperty("status", "500");
            data.addProperty("status_message", "It is not your fault , it's our fault . Please contact the developers");
            //e.printStackTrace();
        }

        return gson.toJson(data);
    }


    public synchronized String deleteLatest(String email, int valueOfToken, int updateCount) {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {

                PreparedStatement selectIds = connection.prepareStatement(
                        "SELECT id FROM CHATS ORDER BY id DESC LIMIT ?"
                );
                selectIds.setInt(1, updateCount);
                ResultSet rs = selectIds.executeQuery();

                List<Integer> ids = new ArrayList<>();
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }

                if (!ids.isEmpty()) {
                    String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
                    String sql = "DELETE FROM CHATS WHERE id IN (" + placeholders + ")";
                    PreparedStatement delete = connection.prepareStatement(sql);
                    for (int i = 0; i < ids.size(); i++) {
                        delete.setInt(i + 1, ids.get(i));
                    }
                    int deleted = delete.executeUpdate();
                    data.addProperty("status", "200");

                } else {
                    data.addProperty("status", "200");
                }
            } else {
                data.addProperty("status", "401");
            }
        } catch (Exception e) {
            data.addProperty("status", "500");
            data.addProperty("status_message", "it is not your fault , it's our fault . Please contact the developers");
           // data.addProperty("error", e.getMessage());
           // e.printStackTrace();
        }

        return gson.toJson(data);
    }
    public synchronized String getChats(String email, int valueOfToken) {
        //TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) ) {
                PreparedStatement fetchAll = connection.prepareStatement("SELECT * FROM CHATS ORDER BY timestamp ASC");
                ResultSet allChats = fetchAll.executeQuery();

                JsonArray chatArray = new JsonArray();
                while (allChats.next()) {
                    JsonObject chatJson = new JsonObject();
                    chatJson.addProperty("id", allChats.getInt("id"));
                    chatJson.addProperty("sender_username", allChats.getString("sender_username"));
                    chatJson.addProperty("receiver_username", allChats.getString("receiver_username"));
                    chatJson.addProperty("timestamp", allChats.getTimestamp("timestamp").toString());
                    chatJson.addProperty("body", allChats.getString("body"));
                    chatJson.addProperty("type", allChats.getString("type"));

                    int replyId = allChats.getInt("reply_chat_id");
                    chatJson.addProperty("reply_chat_id", replyId);

                    String surah2 = allChats.getString("surah");
                    chatJson.addProperty("surah", surah2);

                    int ayah2 = allChats.getInt("ayah");
                    chatJson.addProperty("ayah", ayah2);
                    chatArray.add(chatJson);
                }
                JsonObject message = new JsonObject();
                message.add("chats", chatArray);
                data.add("chats", chatArray);
                data.addProperty("status", "200");
            }
            else{
                data.addProperty("status", "401");
            }
        }
        catch(Exception e){
            data.addProperty("status", "500");
        }
        return gson.toJson(data);
    }
    public synchronized String askAI(String email, int valueOfToken, String question) {
        //TokenValidator tokenValidator = new TokenValidator();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);

        if(tokenValidator.VALIDATE(email, valueOfToken)) {
            Client client = new Client();
            String prompt = "Read the following text carefully:\n\n" +
                    question + "\n\n" +
                    "Then, perform the following tasks with a Quranic and religious perspective:\n" +
                    "1. This a religional question.\n" +
                    "2. Answer the question from religional perspective\n" +
                    "3. Answer must not exceed 250 character and strictly follow this" +
                    "### Response Format\n" +
                    "Just return the answer nothing else like extra comment or anything";

            try {
                GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);
                data.addProperty("status", "200");
                data.addProperty("answer", response.text());
            }
            catch (Exception e) {
                data.addProperty("status", "500");
            }
        } else{
            data.addProperty("status", "401");
        }
        return gson.toJson(data);
    }
}
