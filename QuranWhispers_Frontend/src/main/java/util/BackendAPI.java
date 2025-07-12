package util;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class BackendAPI {
    public static JSONObject fetch(String action, JSONObject request) {
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            socket = new Socket("172.29.74.104", 42069);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Choose action: [login/register/getinfo/addtofavourites/rmvfavverse/sendtofriend/getduaoftheday/adddua/addverse/generatemoodbasedverse/generatethemebasedverse/uploadmp3/approverecitation/listenrecitation/getallinfo]");
                action = action.toLowerCase();
                HashMap<String, String> data = new HashMap<>();

                if (action.equals("login")) {
                    data.put("action", "login");
                    data.put("email", request.getString("email"));
                    data.put("password", request.getString("password"));
                } else if (action.equals("register")) {
                    data.put("action", "register");
                    data.put("email", request.getString("email"));
                    data.put("password", request.getString("password"));
                    data.put("username", request.getString("username"));

                } else if (action.equals("getinfo")) {
                    data.put("action", "getinfo");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                } else if (action.equals("addtofavourites")) {
                    data.put("action", "addtofavourites");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                    data.put("emotion", request.getString("emotion"));
                    data.put("theme", request.getString("theme"));
                    data.put("ayah", request.getString("ayah"));
                    data.put("surah", request.getString("surah"));
                } else if (action.equals("rmvfavverse")) {
                    data.put("action", "rmvfavverse");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                    data.put("emotion", request.getString("emotion"));
                    data.put("theme", request.getString("theme"));
                    data.put("ayah", request.getString("ayah"));
                    data.put("surah", request.getString("surah"));
                } else if (action.equals("sendtofriend")) {
                    data.put("action", "sendtofriend");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                    data.put("friendusername", request.getString("receiver_username"));
                    data.put("emotion", request.getString("emotion"));
                    data.put("theme", request.getString("theme"));
                    data.put("ayah", request.getString("ayah"));
                    data.put("surah", request.getString("surah"));
                } else if (action.equals("getduaoftheday")) {
                    data.put("action", "getduaoftheday");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                } else if (action.equals("adddua")) {
                    data.put("action", "adddua");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                    data.put("title", request.getString("title"));
                    data.put("arabicbody", request.getString("arabicbody"));
                    data.put("englishbody", request.getString("englishbody"));
                } else if (action.equals("addverse")) {
                    data.put("action", "addverse");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                    data.put("emotion", request.getString("emotion"));
                    data.put("theme", request.getString("theme"));
                    data.put("ayah", request.getString("ayah"));
                    data.put("surah", request.getString("surah"));
                } else if (action.equals("generateemotionbasedverse")) {
                    data.put("action", "generateemotionbasedverse");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                    data.put("emotion", request.getString("emotion"));
                } else if (action.equals("generatethemebasedverse")) {
                    data.put("action", "generatethemebasedverse");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                    data.put("theme", request.getString("theme"));
                } else if (action.equals("getlist")) {
                    data.put("action", "getlist");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                } else if (action.equals("getprofileinfo")) {
                    data.put("action", "getprofileinfo");
                    data.put("token", SessionManager.getToken());
                    data.put("email", SessionManager.getEmail());
                }









//                else if (action.equals("uploadmp3")) {
//                    data.put("action", "uploadmp3");
//                    data.put("token", SessionManager.getToken());
//                    data.put("email", SessionManager.getEmail());
//                    System.out.print("Enter full path of MP3 file: ");
//                    File mp3File = new File(scanner.nextLine());
//                    if (!mp3File.exists()) {
//                        System.out.println("File not found.");
//                        continue;
//                    }
//                    data.put("filename", mp3File.getName());
//                    data.put("reciter_name", "Qari Mishary2");
//                    data.put("surah", "Al-Fatih2a");
//                    data.put("ayat", "2");
//                    data.put("filesize", String.valueOf(mp3File.length()));
//
//                    String json = new JSONObject(data).toString();
//                    bufferedWriter.write(json);
//                    bufferedWriter.newLine();
//                    bufferedWriter.flush();
//
//                    try (FileInputStream fis = new FileInputStream(mp3File); DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
//                        dos.writeLong(mp3File.length());
//                        byte[] buffer = new byte[4096];
//                        int read;
//                        while ((read = fis.read(buffer)) != -1) {
//                            dos.write(buffer, 0, read);
//                        }
//                        dos.flush();
//                        System.out.println("MP3 file sent.");
//                    } catch (IOException e) {
//                        System.out.println("Error sending file: " + e.getMessage());
//                    }
//
//                    continue;
//                } else if (action.equals("approverecitation")) {
//                    data.put("action", "approverecitation");
//                    data.put("token", SessionManager.getToken());
//                    data.put("email", SessionManager.getEmail());
//                    System.out.print("Enter reciter name: ");
//                    data.put("recitername", scanner.nextLine());
//                    System.out.print("Enter Surah: ");
//                    data.put("surah", scanner.nextLine());
//                    System.out.print("Enter Ayat: ");
//                    data.put("ayat", scanner.nextLine());
//                } else if (action.equals("listenrecitation")) {
//                    data.put("action", "listenrecitation");
//                    data.put("token", SessionManager.getToken());
//                    data.put("email", SessionManager.getEmail());
//                    System.out.print("Enter Reciter Name: ");
//                    data.put("reciter", scanner.nextLine());
//                    System.out.print("Enter Surah: ");
//                    data.put("surah", scanner.nextLine());
//                    System.out.print("Enter Ayat: ");
//                    data.put("ayat", scanner.nextLine());
//
//                    String json = new JSONObject(data).toString();
//                    bufferedWriter.write(json);
//                    bufferedWriter.newLine();
//                    bufferedWriter.flush();
//
//                    DataInputStream dis = new DataInputStream(socket.getInputStream());
//                    long audioSize = dis.readLong();
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    byte[] buffer = new byte[4096];
//                    long bytesRead = 0;
//                    while (bytesRead < audioSize) {
//                        int read = dis.read(buffer, 0, (int) Math.min(buffer.length, audioSize - bytesRead));
//                        if (read == -1) break;
//                        baos.write(buffer, 0, read);
//                        bytesRead += read;
//                    }
//                    byte[] audioBytes = baos.toByteArray();
//                    File tempMp3 = File.createTempFile("recitation", ".mp3");
//                    try (FileOutputStream fos = new FileOutputStream(tempMp3)) {
//                        fos.write(audioBytes);
//                    }
//                    String uri = tempMp3.toURI().toString();
//                    Media media = new Media(uri);
//                    MediaPlayer mediaPlayer = new MediaPlayer(media);
//                    mediaPlayer.setOnEndOfMedia(() -> tempMp3.delete());
//                    mediaPlayer.play();
//                    System.out.println("🔊 Playing audio using MediaPlayer...");
//                    continue;
//                } else if (action.equals("getallinfo")) {
//                    data.put("action", "getallinfo");
//                    data.put("token", SessionManager.getToken());
//                    data.put("email", SessionManager.getEmail());
//                }
//
                else {
                    System.out.println("EVERYTHING RUNNING SM0OTHLY");
                }

                String json = new JSONObject(data).toString();
                bufferedWriter.write(json);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String response = bufferedReader.readLine();

                JSONObject jsonResponse = new JSONObject(response);
                if (action.equals("login")) {
                    SessionManager.setToken(jsonResponse.getString("token"));
                    SessionManager.setEmail(jsonResponse.getString("email"));
                }
                return jsonResponse;
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
