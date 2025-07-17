package com.example.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        new Thread(() -> runClient()).start();
    }

    public static void main(String[] args) {
        launch();
    }

    private void runClient() {
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        SessionManager sessionManager = new SessionManager();

        try {
            socket = new Socket("localhost", 42069);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            Scanner scanner = new Scanner(System.in);
            Gson gson = new Gson();

            while (true) {
                System.out.println("Choose action: [login/register/getinfo/addtofavourites/rmvfavverse/sendtofriend/getduaoftheday/adddua/addverse/generateemotionbasedverse/generatethemebasedverse/uploadmp3/approverecitation/listenrecitation/getallinfo]");
                String action = scanner.nextLine().trim().toLowerCase();
                HashMap<String, String> data = new HashMap<>();

                if (action.equals("login")) {
                    data.put("action", "login");
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    data.put("email", email);
                    data.put("password", password);
                } else if (action.equals("register")) {
                    data.put("action", "register");
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    data.put("email", email);
                    data.put("password", password);
                    data.put("username", username);
                } else if (action.equals("getinfo")) {
                    data.put("action", "getinfo");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                } else if (action.equals("addtofavourites")) {
                    data.put("action", "addtofavourites");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter emotion: ");
                    data.put("emotion", scanner.nextLine());
                    System.out.print("theme : ");
                    data.put("theme", scanner.nextLine());
                    System.out.print("ayah: ");
                    data.put("ayah", scanner.nextLine());
                    System.out.print("Surah: ");
                    data.put("surah", scanner.nextLine());
                } else if (action.equals("rmvfavverse")) {
                    data.put("action", "rmvfavverse");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter emotion: ");
                    data.put("emotion", scanner.nextLine());
                    System.out.print("theme : ");
                    data.put("theme", scanner.nextLine());
                    System.out.print("ayah: ");
                    data.put("ayah", scanner.nextLine());
                    System.out.print("Surah: ");
                    data.put("surah", scanner.nextLine());
                } else if (action.equals("sendtofriend")) {
                    data.put("action", "sendtofriend");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Friend Username: ");
                    data.put("friendusername", scanner.nextLine());
                    System.out.print("Enter emotion: ");
                    data.put("emotion", scanner.nextLine());
                    System.out.print("theme : ");
                    data.put("theme", scanner.nextLine());
                    System.out.print("ayah: ");
                    data.put("ayah", scanner.nextLine());
                    System.out.print("Surah: ");
                    data.put("surah", scanner.nextLine());
                } else if (action.equals("getduaoftheday")) {
                    data.put("action", "getduaoftheday");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                } else if (action.equals("adddua")) {
                    data.put("action", "adddua");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter Title: ");
                    data.put("title", scanner.nextLine());
                    System.out.print("English Body: ");
                    data.put("englishbody", scanner.nextLine());
                    System.out.print("Arabic Body: ");
                    data.put("arabicbody", scanner.nextLine());
                } else if (action.equals("addverse")) {
                    data.put("action", "addverse");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter emotion: ");
                    data.put("emotion", scanner.nextLine());
                    System.out.print("Enter Theme: ");
                    data.put("theme", scanner.nextLine());
                    System.out.print("ayah: ");
                    data.put("ayah", scanner.nextLine());
                    System.out.print("Surah: ");
                    data.put("surah", scanner.nextLine());
                } else if (action.equals("generateemotionbasedverse")) {
                    data.put("action", "generateemotionbasedverse");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter emotion: ");
                    data.put("emotion", scanner.nextLine());
                } else if (action.equals("generatethemebasedverse")) {
                    data.put("action", "generatethemebasedverse");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter Theme: ");
                    data.put("theme", scanner.nextLine());
                }
                else if(action.equalsIgnoreCase("getrecieveinfo")) {
                    data.put("action", "getrecieveinfo");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if (action.equals("uploadmp3")) {
                    data.put("action", "uploadmp3");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter full path of MP3 file: ");
                    File mp3File = new File(scanner.nextLine());
                    if (!mp3File.exists()) {
                        System.out.println(" File not found.");
                        continue;
                    }
                    data.put("filename", mp3File.getName());
                    data.put("reciter_name", "Qari Mishary");
                    data.put("surah", "Al-Fatiha");
                    data.put("ayah", "1");
                    data.put("filesize", String.valueOf(mp3File.length()));



                    String json = gson.toJson(data);
                    bufferedWriter.write(json);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    //  Wait for server to say READY_TO_RECEIVE
                    String ack = bufferedReader.readLine();
                    if (!"READY_TO_RECEIVE".equals(ack)) {
                        System.out.println("Server did not acknowledge. Got: " + ack);
                        continue;
                    }

                    FileInputStream fis = new FileInputStream(mp3File);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    try {
                        byte[] buffer = new byte[4096];
                        int read;
                        dos.writeLong(mp3File.length());
                        while ((read = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, read);
                        }
                        dos.flush();
                        System.out.println(" MP3 file sent.");
                    } catch (IOException e) {
                        System.out.println(" Error sending file: " + e.getMessage());
                    } finally {
                        fis.close();  // only close file stream, NOT socket streams
                    }


                    // Read final JSON response from server
                    String response = bufferedReader.readLine();
                    System.out.println("Server: " + response);
                    continue;
                }

                else if (action.equals("approverecitation")) {
                    data.put("action", "approverecitation");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter reciter name: ");
                    data.put("recitername", scanner.nextLine());
                    System.out.print("Enter Surah: ");
                    data.put("surah", scanner.nextLine());
                    System.out.print("Enter ayah: ");
                    data.put("ayah", scanner.nextLine());
                } else if (action.equals("listenapprovedrecitation")) {
                    data.put("action", "listenapprovedrecitation");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter Surah: ");
                    data.put("surah", scanner.nextLine());
                    System.out.print("Enter ayah: ");
                    data.put("ayah", scanner.nextLine());

                    String json = gson.toJson(data);
                    bufferedWriter.write(json);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    long audioSize = dis.readLong();  // 🔒 Waits for exact file size
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    long bytesRead = 0;

                    while (bytesRead < audioSize) {
                        int read = dis.read(buffer, 0, (int)Math.min(buffer.length, audioSize - bytesRead));
                        if (read == -1) break;
                        baos.write(buffer, 0, read);
                        bytesRead += read;
                    }

                    File mp3File = new File("recitation_" + System.currentTimeMillis() + ".mp3"); // ✅ Save in project root
                    try (FileOutputStream fos = new FileOutputStream(mp3File)) {
                        fos.write(baos.toByteArray());
                    }
                    System.out.println("✅ MP3 file saved to: " + mp3File.getAbsolutePath());
                    System.out.println("MP3 file size = " + mp3File.length() + " bytes");

                    javafx.application.Platform.runLater(() -> {
                        try {
                            String uri = mp3File.toURI().toString();
                            Media media = new Media(uri);
                            MediaPlayer mediaPlayer = new MediaPlayer(media);

                            mediaPlayer.setOnEndOfMedia(() -> {
                                mediaPlayer.dispose(); // Only dispose player
                                System.out.println("Audio playback finished.");
                            });

                            mediaPlayer.setOnError(() -> {
                                System.err.println("Media error: " + mediaPlayer.getError().getMessage());
                            });

                            mediaPlayer.play();
                            System.out.println("🔊 Playing audio...");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    System.out.print("Enter reciter_name: ");

                    continue;
                }
                else if(action.equals("listenpendingrecitation")) {
                    data.put("action", "listenpendingrecitation");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Enter reciter_name: ");
                    data.put("recitername", scanner.nextLine());
                    System.out.print("Enter Surah: ");
                    data.put("surah", scanner.nextLine());
                    System.out.print("Enter ayah: ");
                    data.put("ayah", scanner.nextLine());

                    String json = gson.toJson(data);
                    bufferedWriter.write(json);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    long audioSize = dis.readLong();  // 🔒 Waits for exact file size
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    long bytesRead = 0;

                    while (bytesRead < audioSize) {
                        int read = dis.read(buffer, 0, (int)Math.min(buffer.length, audioSize - bytesRead));
                        if (read == -1) break;
                        baos.write(buffer, 0, read);
                        bytesRead += read;
                    }

                    File mp3File = new File("recitation_" + System.currentTimeMillis() + ".mp3"); // ✅ Save in project root
                    try (FileOutputStream fos = new FileOutputStream(mp3File)) {
                        fos.write(baos.toByteArray());
                    }
                    System.out.println("✅ MP3 file saved to: " + mp3File.getAbsolutePath());
                    System.out.println("MP3 file size = " + mp3File.length() + " bytes");

                    javafx.application.Platform.runLater(() -> {
                        try {
                            String uri = mp3File.toURI().toString();
                            Media media = new Media(uri);
                            MediaPlayer mediaPlayer = new MediaPlayer(media);

                            mediaPlayer.setOnEndOfMedia(() -> {
                                mediaPlayer.dispose(); // Only dispose player
                                System.out.println("Audio playback finished.");
                            });

                            mediaPlayer.setOnError(() -> {
                                System.err.println("Media error: " + mediaPlayer.getError().getMessage());
                            });

                            mediaPlayer.play();
                            System.out.println("🔊 Playing audio...");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    System.out.print("Enter reciter_name: ");

                    continue;
                }
                else if (action.equals("getallinfo")) {
                    data.put("action", "getallinfo");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }else if (action.equals("disapproverecitation")) {
                    data.put("action", "disapproverecitation");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Reciter Name: ");
                    data.put("recitername", scanner.nextLine());
                    System.out.print("Surah: ");
                    data.put("surah", scanner.nextLine());
                    System.out.print("Ayah: ");
                    data.put("ayah", scanner.nextLine());
                }
                else if (action.equals("deleteapprovedrecitation")) {
                    data.put("action", "deleteapprovedrecitation");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.print("Reciter Name: ");
                    data.put("recitername", scanner.nextLine());
                    System.out.print("Surah: ");
                    data.put("surah", scanner.nextLine());
                    System.out.print("Ayah: ");
                    data.put("ayah", scanner.nextLine());
                }
                else if(action.equals("logout")) {
                    data.put("action", "logout");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if (action.equals("getlist")) {
                    data.put("action", "getlist");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if (action.equals("getprofileinfo")) {
                    data.put("action", "getprofileinfo");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if(action.equals("generateapibasedverse")) {
                    data.put("action", "generateapibasedverse");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    String text = scanner.nextLine();
                    data.put("text", text);
                }
                else if(action.equals("getallusers")) {
                    data.put("action", "getallusers");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if(action.equals("getallverses")) {
                    data.put("action", "getallverses");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if(action.equalsIgnoreCase("getAllPendingRecitations")) {
                    data.put("action", "getAllPendingRecitations");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if(action.equalsIgnoreCase("banuser")) {
                    data.put("action", "banuser");
                    data.put("token", sessionManager.getToken());
                    System.out.println("Give username");
                    String username = scanner.nextLine();
                    data.put("email", sessionManager.getEmail());
                    data.put("username", username);
                }
                else if(action.equalsIgnoreCase("unbanuser")) {
                    data.put("action", "banuser");
                    data.put("token", sessionManager.getToken());
                    System.out.println("Give username");
                    String username = scanner.nextLine();
                    data.put("username", username);
                }
                else if(action.equalsIgnoreCase("registerchat")) {
                    data.put("action", "registerchat");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                    System.out.println("Give senderusername");
                    String sender_username = scanner.nextLine();
                    System.out.println("Give receiverusername");
                    String receiverusername =  scanner.nextLine();
                    data.put("receiver_username", receiverusername);
                    data.put("sender_username", sender_username);
                    System.out.println("Body");
                    String body = scanner.nextLine();
                    System.out.println("type");
                    String type = scanner.nextLine();
                    System.out.println("reply_chat_id");
                    String reply_chat_id = scanner.nextLine();
                    //scanner.nextLine();
                    System.out.println("Surah");
                    String surah = scanner.nextLine();
                    System.out.println("Ayah");
                    String ayah = scanner.nextLine();

                    data.put("body", body);
                    data.put("type", type);
                    data.put("surah", surah);
                    data.put("reply_chat_id", reply_chat_id);
                    data.put("ayah", ayah);
                }
                else if(action.equalsIgnoreCase("clearchathistory")) {
                    data.put("action", "clearchathistory");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if(action.equalsIgnoreCase("deletechatbyid")) {
                    data.put("action", "deletechatbyid");
                    String id = scanner.nextLine();
                    data.put("id", id);
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if(action.equalsIgnoreCase("deletelatest")) {
                    String id = scanner.nextLine();
                    data.put("action", "deletelatest");
                    data.put("number_of_chats", id);
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());

                }
                else if(action.equalsIgnoreCase("retrievechat")) {
                    data.put("action", "retrievechat");
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else if(action.equalsIgnoreCase("askai")) {
                    String question = scanner.nextLine();
                    data.put("action", "askai");
                    data.put("question", question);
                    data.put("token", sessionManager.getToken());
                    data.put("email", sessionManager.getEmail());
                }
                else {
                    continue;
                }
                // deleteuser baki

                String json = gson.toJson(data);
                bufferedWriter.write(json);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String response = bufferedReader.readLine();
                System.out.println("Server: " + response);

                JsonObject json2 = gson.fromJson(response, JsonObject.class);
                if (action.equals("login")) {
                    sessionManager.setToken(json2.get("token").getAsString());
                    sessionManager.setEmail(json2.get("email").getAsString());
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
    }
}
// /Users/fayeem/IdeaProjects/Client/src/main/resources/com/example/client/001.mp3