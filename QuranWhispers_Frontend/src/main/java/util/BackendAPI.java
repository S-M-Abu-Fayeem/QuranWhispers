package util;

import controller.RecitationController;
import org.json.JSONArray;
import org.json.JSONObject;
import shared.FilePacket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;

public class BackendAPI {
    private static Thread fetchThread = null;
    private static boolean running = false;

    public static void continuousFetch(String command) {
        if ("start".equalsIgnoreCase(command)) {
            if (!running) {
                running = true;

                fetchThread = new Thread(() -> {
                    Socket socket = null;
                    InputStreamReader inputStreamReader = null;
                    OutputStreamWriter outputStreamWriter = null;
                    BufferedReader bufferedReader = null;
                    BufferedWriter bufferedWriter = null;

                    try {
                        socket = new Socket(GlobalState.BACKEND_API_IP_ADDRESS, GlobalState.BACKEND_API_PORT);
                        System.out.println("Database Connected Successfully");

                        inputStreamReader = new InputStreamReader(socket.getInputStream());
                        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                        bufferedReader = new BufferedReader(inputStreamReader);
                        bufferedWriter = new BufferedWriter(outputStreamWriter);

                        HashMap<String, String> data = new HashMap<>();
                        String json = new JSONObject(data).toString();
                        bufferedWriter.write(json);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        while (!Thread.currentThread().isInterrupted() && running) {
                            try {
                                String response = bufferedReader.readLine();
                                if (response != null) {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    System.out.println("Response received: " + jsonResponse.toString());
                                } else {
                                    System.out.println("No data received, server may have closed the connection.");
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
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
                });
                fetchThread.start();
            } else {
                System.out.println("Thread is already running.");
            }
        } else if ("stop".equalsIgnoreCase(command)) {
            if (running) {
                running = false;
                if (fetchThread != null) {
                    fetchThread.interrupt();
                    try {
                        fetchThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Thread stopped.");
            } else {
                System.out.println("No thread is running.");
            }
        } else {
            System.out.println("Invalid command. Use 'start' or 'stop'.");
        }
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

                // 🔽 Step 1: Wait for READY_TO_RECEIVE using bufferedReader
                String ack = bufferedReader.readLine();
                if (ack == null || !ack.trim().equals("READY_TO_RECEIVE")) {
                    System.out.println("❌ Server not ready to send file. Got: " + ack);
                    return null;
                }

                System.out.println("✅ Server ready to send FilePacket.");

                // 🔽 Step 2: Read FilePacket safely
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                FilePacket receivedPacket = (FilePacket) ois.readObject();

                // 🔽 Step 3: Build save path
                String filename = receivedPacket.getSurah() + "_" + receivedPacket.getAyah() + "_" + receivedPacket.getReciterName() + ".mp3";
                URL folderURL = RecitationController.class.getResource("/data/recitations_audio/");
                if (folderURL == null) {
                    System.out.println("❌ Folder not found in resources. Please create /resources/data/recitations_audio");
                    return null;
                }
                File outputFile = new File(folderURL.toURI().getPath(), filename);
                Files.write(outputFile.toPath(), receivedPacket.getFileData());
                System.out.println("💾 Audio saved at: " + outputFile.getAbsolutePath());

                // 🔽 Step 4: Read final JSON response using new reader (safe reuse)
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


            // 🔽 All other actions
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
                if (action.equals("login")) {
                    SessionManager.setToken(jsonResponse.getString("token"));
                    SessionManager.setEmail(jsonResponse.getString("email"));
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

}
