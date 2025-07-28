package util;

import controller.RecitationController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import org.json.JSONObject;
import shared.FilePacket;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;

public class BackendAPI {
    public static JSONObject continuousFetch() {
            Socket socket = null;
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;
            try {
                socket = new Socket(GlobalState.BACKEND_API_IP_ADDRESS, GlobalState.BACKEND_API_PORT);
                System.out.println("Database Connection OK ✅");

                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                JSONObject data = new JSONObject();
                data.put("action", "retrievechat");
                data.put("token", SessionManager.getToken());
                data.put("email", SessionManager.getEmail());

                bufferedWriter.write(data.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String response = bufferedReader.readLine();
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    return jsonResponse;
                } else {
                    System.out.println("No data received, server may have closed the connection.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) socket.close();
                    if (bufferedReader != null) bufferedReader.close();
                    if (bufferedWriter != null) bufferedWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return null;
    }

    public static JSONObject fetch(String action, JSONObject request) {
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            socket = new Socket(GlobalState.BACKEND_API_IP_ADDRESS, GlobalState.BACKEND_API_PORT);
            System.out.println("📡 Connected to backend");

            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            action = action.toLowerCase();
            HashMap<String, String> data = new HashMap<>();

            if (action.equals("uploadmp3")) {
                File mp3File = new File(request.getString("mp3file"));

                if (!mp3File.exists() || !mp3File.isFile()) {
                    System.out.println("❌ File not found or is not a valid file.");
                    return null;
                }

                data.put("action", "uploadmp3");
                data.put("token", SessionManager.getToken());
                data.put("email", SessionManager.getEmail());
                data.put("filename", mp3File.getName());
                data.put("reciter_name", request.getString("reciter_name"));
                data.put("surah", request.getString("surah"));
                data.put("ayah", request.getString("ayah"));
                data.put("filesize", String.valueOf(mp3File.length()));

                String json = new JSONObject(data).toString();
                bufferedWriter.write(json);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("📤 Metadata sent.");

                String ack = bufferedReader.readLine();
                if (!"READY_TO_RECEIVE".equals(ack)) {
                    System.out.println("❌ Server did not acknowledge. Got: " + ack);
                    return null;
                }
                System.out.println("✅ Server is ready to receive file.");

                byte[] fileBytes = Files.readAllBytes(mp3File.toPath());
                FilePacket packet = new FilePacket(
                        SessionManager.getEmail(),
                        request.getString("reciter_name"),
                        request.getString("surah"),
                        request.getString("ayah"),
                        mp3File.getName(),
                        fileBytes
                );

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(packet);
                oos.flush();
                System.out.println("✅ FilePacket sent.");

                String response = bufferedReader.readLine();
                if (response != null) {
                    System.out.println("📨 Server: " + response);
                    return new JSONObject(response);
                } else {
                    System.out.println("⚠️ No final response received from server.");
                }

                return null;
            }

            if (action.equals("listenpendingrecitation") || action.equals("listenapprovedrecitation")) {
                data.put("action", action);
                data.put("token", SessionManager.getToken());
                data.put("email", SessionManager.getEmail());
                for (String key : request.keySet()) {
                    data.put(key, request.getString(key));
                }

                String json = new JSONObject(data).toString();
                bufferedWriter.write(json);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String ack = bufferedReader.readLine();
                if (ack == null || !ack.trim().equals("READY_TO_RECEIVE")) {
                    System.out.println("❌ Server not ready to send file. Got: " + ack);
                    return null;
                }

                System.out.println("✅ Server ready to send FilePacket.");

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                FilePacket receivedPacket = (FilePacket) ois.readObject();

                String filename = receivedPacket.getSurah() + "_" + receivedPacket.getAyah() + "_" + receivedPacket.getReciterName() + ".mp3";
                URL folderURL = RecitationController.class.getResource("/data/recitations_audio/");
                if (folderURL == null) {
                    System.out.println("❌ Folder not found in resources. Please create /resources/data/recitations_audio");
                    return null;
                }
                File outputFile = new File(folderURL.toURI().getPath(), filename);
                Files.write(outputFile.toPath(), receivedPacket.getFileData());
                System.out.println("💾 Audio saved at: " + outputFile.getAbsolutePath());


                BufferedReader finalReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = finalReader.readLine();
                if (response != null) {
                    System.out.println("📨 Server: " + response);
                    JSONObject ob = new JSONObject(response);
                    ob.put("audio_path", outputFile.getAbsolutePath());
                    return ob;
                } else {
                    System.out.println("⚠️ No response after file packet received.");
                    return null;
                }
            }


            data.put("action", action);
            if (SessionManager.getToken() != null) data.put("token", SessionManager.getToken());
            if (SessionManager.getEmail() != null) data.put("email", SessionManager.getEmail());
            for (String key : request.keySet()) {
                data.put(key, request.getString(key));
            }

            String json = new JSONObject(data).toString();
            bufferedWriter.write(json);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String response = bufferedReader.readLine();
            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);
                System.out.println(jsonResponse.toString());
                if (action.equals("login")) {
                    if (jsonResponse.getString("status").equals("200")) {
                        SessionManager.setToken(jsonResponse.getString("token"));
                        SessionManager.setEmail(jsonResponse.getString("email"));
                        SessionManager.setUsername(jsonResponse.getString("username"));
                    }
                }
                return jsonResponse;
            } else {
                System.out.println("No response received for action: " + action);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (inputStreamReader != null) inputStreamReader.close();
                if (outputStreamWriter != null) outputStreamWriter.close();
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static JSONObject sendForumMessage(JSONObject payload) {
        Socket socket = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            socket = new Socket(GlobalState.BACKEND_API_IP_ADDRESS, GlobalState.BACKEND_API_PORT);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JSONObject data = new JSONObject();
            data.put("action", "registerchat");
            for (String key : payload.keySet()) {
                data.put(key, payload.get(key));
            }

            writer.write(data.toString());
            writer.newLine();
            writer.flush();

            String temp = reader.readLine();
            JSONObject response = null;
            if (temp != null) {
                response = new JSONObject(temp);
            }
            if (response != null && response.getString("status").equals("200")) {
                return response;
            } else if (response != null && response.getString("status").equals("403")) {
                final JSONObject finalResponse = response;
                Platform.runLater(() -> {
                    BackendAPI.alertGenerator("Send failed", "ACCESS DENIED", finalResponse.getString("status_message"), "error", "/images/denied.png");
                });
            } else if (response != null && response.getString("status").equals("500")) {
                final JSONObject finalResponse = response;
                Platform.runLater(() -> {
                    BackendAPI.alertGenerator("Send failed", "SERVER ERROR", finalResponse.getString("status_message"), "error", "/images/denied.png");
                });
            } else {
                Platform.runLater(() -> {
                    BackendAPI.alertGenerator("Error", "UNKNOWN PROBLEM", "Something went wrong :(", "error", "/images/denied.png");
                });
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (writer != null) writer.close();
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ButtonType alertGenerator(String title, String header, String content, String type, String iconPath) {
        Alert alert;
        if ("error".equals(type)) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else if ("warning".equals(type)) {
            alert = new Alert(Alert.AlertType.WARNING);
        } else if ("confirmation".equals(type)) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }

        alert.setTitle(title);

        if (iconPath != null && !iconPath.isEmpty()) {
            URL iconURL = BackendAPI.class.getResource(iconPath);
            if (iconURL != null) {
                alert.getDialogPane().setGraphic(new ImageView(new Image(iconURL.toExternalForm())));
            }
        }

        alert.getDialogPane().setStyle("-fx-font-family: 'Century Gothic'; -fx-font-size: 14px; -fx-text-fill: #000000;");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMinWidth(400);
        alert.getDialogPane().setMaxHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setMaxWidth(600);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().get();
    }

}
