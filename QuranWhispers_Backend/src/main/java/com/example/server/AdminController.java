package com.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.sql.*;

public class AdminController {
    private static final String DB_URL = "jdbc:h2:file:./data/usersdb;INIT=RUNSCRIPT FROM 'classpath:users.sql'";
    public String DELETE_USER(String email, int valueOfToken, String userMail) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                PreparedStatement qs = connection.prepareStatement("DELETE FROM USERS WHERE email = ?");
                qs.setString(1, userMail);
                int check = qs.executeUpdate();
                if(check > 0) {
                    data.addProperty("status", "200");
                }
                else{
                    data.addProperty("status", "404");
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
    public String DELETE_DUA(String email, int valueOfToken, String title) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                PreparedStatement qs = connection.prepareStatement("DELETE FROM DUA WHERE title = ?");
                qs.setString(1, title);
                int check = qs.executeUpdate();
                if(check > 0) {
                    data.addProperty("status", "200");
                }
                else{
                    data.addProperty("status", "404");
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
    public String DELETE_VERSE(String email, int valueOfToken, String emotion, String theme,String ayah, String surah) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        try(Connection connection = DriverManager.getConnection(DB_URL)) {
            if(tokenValidator.VALIDATE(email, valueOfToken) && isAdmin.isAdmin(email)) {
                PreparedStatement qs = connection.prepareStatement("DELETE FROM MOOD_VERSES WHERE emotion = ? AND theme = ? AND ayah = ? AND surah = ?");
                qs.setString(1, emotion);
                qs.setString(2, theme);
                qs.setString(3, ayah);
                qs.setString(4, surah);
                int check = qs.executeUpdate();
                if(check > 0) {
                    data.addProperty("status", "200");
                }
                else{
                    data.addProperty("status", "404");
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

    public String approveRecitation(String email, int token, String reciterName, String surah, String ayah) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject res = new JsonObject();
        res.addProperty("email", email);

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (tokenValidator.VALIDATE(email, token) && isAdmin.isAdmin(email)) {
                // Select from PendingRecitations
                PreparedStatement select = conn.prepareStatement(
                        "SELECT uploader_email, file_name, audio_data FROM PendingRecitations WHERE reciter_name = ? AND surah = ? AND ayah = ?"
                );
                select.setString(1, reciterName);
                select.setString(2, surah);
                select.setString(3, ayah);

                ResultSet rs = select.executeQuery();
                if (rs.next()) {
                    String uploader = rs.getString("uploader_email");
                    String fileName = rs.getString("file_name");
                    InputStream audioStream = rs.getBinaryStream("audio_data");

                    // Insert into Recitations
                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO Recitations (uploader_email, reciter_name, surah, ayah, file_name, audio_data) VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    insert.setString(1, uploader);
                    insert.setString(2, reciterName);
                    insert.setString(3, surah);
                    insert.setString(4, ayah);
                    insert.setString(5, fileName);
                    insert.setBlob(6, audioStream);
                    insert.executeUpdate();

                    // Delete from PendingRecitations
                    PreparedStatement delete = conn.prepareStatement(
                            "DELETE FROM PendingRecitations WHERE reciter_name = ? AND surah = ? AND ayah = ? AND file_name = ?"
                    );
                    delete.setString(1, reciterName);
                    delete.setString(2, surah);
                    delete.setString(3, ayah);
                    delete.setString(4, fileName);
                    delete.executeUpdate();

                    res.addProperty("status", "approved");
                } else {
                    res.addProperty("status", "not_found");
                }
            } else {
                res.addProperty("status", "unauthorized");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.addProperty("status", "error");
        }

        return gson.toJson(res);
    }


    private JsonArray getAllAsJsonArray(Connection conn, String query) throws SQLException {
        JsonArray array = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                for (int i = 1; i <= colCount; i++) {
                    obj.addProperty(meta.getColumnName(i), rs.getString(i));
                }
                array.add(obj);
            }
        }
        return array;
    }

    private JsonArray getFilteredRecitations(Connection conn, String table) throws SQLException {
        JsonArray array = new JsonArray();

        // Choose correct timestamp column
        String timeColumn = table.equalsIgnoreCase("Recitations") ? "approved_time" : "upload_time";

        String query = "SELECT id, uploader_email, reciter_name, surah, ayah, file_name, " + timeColumn + " FROM " + table;
        System.out.println("extracting ");
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println("first");
                JsonObject obj = new JsonObject();
                obj.addProperty("id", rs.getInt("id"));
                obj.addProperty("uploader_email", rs.getString("uploader_email"));
                obj.addProperty("reciter_name", rs.getString("reciter_name"));
                obj.addProperty("surah", rs.getString("surah"));
                obj.addProperty("ayah", rs.getString("ayah"));
                obj.addProperty("file_name", rs.getString("file_name"));
                obj.addProperty("timestamp", rs.getString(timeColumn));  // unified key
                array.add(obj);
            }
        }

        return array;
    }


    public String getAllInfo(String email, int token) {
        Gson gson = new Gson();
        JsonObject root = new JsonObject();
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // USERS info

            if (!tokenValidator.VALIDATE(email, token) || !isAdmin.isAdmin(email)){
                root.addProperty("status", "404");
                return gson.toJson(root);
            }
            JsonArray users = new JsonArray();
            PreparedStatement st = conn.prepareStatement(
                    "SELECT username, email, total_saved_verse, total_received_verse FROM USERS"
            );
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                JsonObject user = new JsonObject();
                user.addProperty("username", rs.getString("username"));
                user.addProperty("email", rs.getString("email"));
                user.addProperty("total_saved_verse", rs.getInt("total_saved_verse"));
                user.addProperty("total_received_verse", rs.getInt("total_received_verse"));
                users.add(user);
            }
            root.add("users", users);

            // DUA info
            JsonArray duas = getAllAsJsonArray(conn, "SELECT * FROM DUA");
            root.add("duas", duas);

            // MOOD_VERSES info
            JsonArray verses = getAllAsJsonArray(conn, "SELECT * FROM MOOD_VERSES");
            root.add("verses", verses);

            // PendingRecitations (without file_path)
            JsonArray pending = getFilteredRecitations(conn, "PendingRecitations");
            root.add("pending_recitations", pending);

            // Approved Recitations (without file_path)
            JsonArray approved = getFilteredRecitations(conn, "Recitations");
            root.add("approved_recitations", approved);
            root.addProperty("status", "200");

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\"}";
        }

        return gson.toJson(root);
    }
    public String DELETE_APPROVED_RECITATION(String email, int token, String reciterName, String surah, String ayah) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject res = new JsonObject();
        res.addProperty("email", email);

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (tokenValidator.VALIDATE(email, token) && isAdmin.isAdmin(email)) {
                PreparedStatement delete = conn.prepareStatement(
                        "DELETE FROM Recitations WHERE reciter_name = ? AND surah = ? AND ayah = ?"
                );
                delete.setString(1, reciterName);
                delete.setString(2, surah);
                delete.setString(3, ayah);

                int count = delete.executeUpdate();
                if (count > 0) {
                    res.addProperty("status", "200");
                } else {
                    res.addProperty("status", "404");
                }
            } else {
                res.addProperty("status", "401");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.addProperty("status", "500");
        }

        return gson.toJson(res);
    }
    public String DISAPPROVE_RECITATION(String email, int token, String reciterName, String surah, String ayah) {
        TokenValidator tokenValidator = new TokenValidator();
        IsAdmin isAdmin = new IsAdmin();
        Gson gson = new Gson();
        JsonObject res = new JsonObject();
        res.addProperty("email", email);

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (tokenValidator.VALIDATE(email, token) && isAdmin.isAdmin(email)) {
                PreparedStatement delete = conn.prepareStatement(
                        "DELETE FROM PendingRecitations WHERE reciter_name = ? AND surah = ? AND ayah = ?"
                );
                delete.setString(1, reciterName);
                delete.setString(2, surah);
                delete.setString(3, ayah);

                int count = delete.executeUpdate();
                if (count > 0) {
                    res.addProperty("status", "200");
                } else {
                    res.addProperty("status", "404");
                }
            } else {
                res.addProperty("status", "401");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.addProperty("status", "500");
        }

        return gson.toJson(res);
    }




}
