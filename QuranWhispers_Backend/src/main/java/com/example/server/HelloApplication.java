package com.example.server;

import Threading.ReadThread;
import Threading.SocketWrapper;
import Validators.*;
import controllers.*;
import generators.APIBasedVerseGenerator;
import generators.GeneratingDuaOfTheDay;
import generators.RandomizedSelection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloApplication {
    public static final List<SocketWrapper> connectedClients = Collections.synchronizedList(new ArrayList<>());
    public static final ForumMaintenance forumMaintenance = new ForumMaintenance();
    public static final Login login = new Login();
    public static final Register register = new Register();
    public static final UserInfoGetter userInfoGetter = new UserInfoGetter();
    public static final AddFavVerse addFavVerse = new AddFavVerse();
    public static final RemoveVerse removeVerse = new RemoveVerse();
    public static final ReceivedVerseControll receivedVerseControll = new ReceivedVerseControll();
    public static final AddDua addDua = new AddDua();
    public static final GeneratingDuaOfTheDay generatingDuaOfTheDay = new GeneratingDuaOfTheDay();
    public static final AdminController adminController = new AdminController();
    public static final RandomizedSelection randomizedSelection = new RandomizedSelection();
    public static final LogOut logOut = new LogOut();
    public static final GetList getList = new GetList();
    public static final GetProfileInfo getProfileInfo = new GetProfileInfo();
    public static final GetRecieveInfo getRecieveInfo = new GetRecieveInfo();
    public static final APIBasedVerseGenerator apiBasedVerseGenerator = new APIBasedVerseGenerator();
    public static final TokenValidator tokenValidator = new TokenValidator();
    public static final IsAdmin isAdmin = new IsAdmin();
    public static final EmailValidator emailValidator = new EmailValidator();
    public static final PasswordValidator passwordValidator = new PasswordValidator();
    public static final UserNameValidator userNameValidator = new UserNameValidator();
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    serve(socket);
                } catch (Exception e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void serve(Socket socket) throws IOException {
        SocketWrapper socketWrapper = new SocketWrapper(socket);
        connectedClients.add(socketWrapper);
        new ReadThread(socketWrapper);
    }
}


 // /Users/fayeem/Library/Java/JavaVirtualMachines/openjdk-24/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=57990 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/fayeem/.m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-controls/24.0.1/javafx-controls-24.0.1.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-graphics/24.0.1/javafx-graphics-24.0.1.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-base/24.0.1/javafx-base-24.0.1.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-fxml/24.0.1/javafx-fxml-24.0.1.jar:/Users/fayeem/.m2/repository/com/dlsc/formsfx/formsfx-core/11.6.0/formsfx-core-11.6.0.jar:/Users/fayeem/.m2/repository/com/google/auth/google-auth-library-oauth2-http/1.33.0/google-auth-library-oauth2-http-1.33.0.jar:/Users/fayeem/.m2/repository/com/google/auto/value/auto-value-annotations/1.11.0/auto-value-annotations-1.11.0.jar:/Users/fayeem/.m2/repository/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar:/Users/fayeem/.m2/repository/com/google/auth/google-auth-library-credentials/1.33.0/google-auth-library-credentials-1.33.0.jar:/Users/fayeem/.m2/repository/com/google/http-client/google-http-client/1.46.2/google-http-client-1.46.2.jar:/Users/fayeem/.m2/repository/io/grpc/grpc-context/1.70.0/grpc-context-1.70.0.jar:/Users/fayeem/.m2/repository/io/grpc/grpc-api/1.70.0/grpc-api-1.70.0.jar:/Users/fayeem/.m2/repository/io/opencensus/opencensus-api/0.31.1/opencensus-api-0.31.1.jar:/Users/fayeem/.m2/repository/io/opencensus/opencensus-contrib-http-util/0.31.1/opencensus-contrib-http-util-0.31.1.jar:/Users/fayeem/.m2/repository/com/google/http-client/google-http-client-gson/1.46.2/google-http-client-gson-1.46.2.jar:/Users/fayeem/.m2/repository/com/google/guava/guava/33.4.0-android/guava-33.4.0-android.jar:/Users/fayeem/.m2/repository/com/google/guava/failureaccess/1.0.2/failureaccess-1.0.2.jar:/Users/fayeem/.m2/repository/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:/Users/fayeem/.m2/repository/org/checkerframework/checker-qual/3.43.0/checker-qual-3.43.0.jar:/Users/fayeem/.m2/repository/com/google/errorprone/error_prone_annotations/2.36.0/error_prone_annotations-2.36.0.jar:/Users/fayeem/.m2/repository/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar:/Users/fayeem/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/Users/fayeem/.m2/repository/commons-codec/commons-codec/1.11/commons-codec-1.11.jar:/Users/fayeem/.m2/repository/org/apache/httpcomponents/httpcore/4.4.16/httpcore-4.4.16.jar:/Users/fayeem/.m2/repository/com/google/auto/value/auto-value/1.11.0/auto-value-1.11.0.jar:/Users/fayeem/.m2/repository/com/google/api/api-common/2.47.0/api-common-2.47.0.jar:/Users/fayeem/.m2/repository/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar:/Users/fayeem/.m2/repository/com/google/j2objc/j2objc-annotations/3.0.0/j2objc-annotations-3.0.0.jar:/Users/fayeem/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.17.2/jackson-databind-2.17.2.jar:/Users/fayeem/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.17.2/jackson-annotations-2.17.2.jar:/Users/fayeem/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.17.2/jackson-core-2.17.2.jar:/Users/fayeem/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.17.2/jackson-datatype-jdk8-2.17.2.jar:/Users/fayeem/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.17.2/jackson-datatype-jsr310-2.17.2.jar:/Users/fayeem/.m2/repository/org/java-websocket/Java-WebSocket/1.6.0/Java-WebSocket-1.6.0.jar:/Users/fayeem/.m2/repository/org/jspecify/jspecify/1.0.0/jspecify-1.0.0.jar -p /Users/fayeem/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-base/24.0.1/javafx-base-24.0.1-mac-aarch64.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-graphics/24.0.1/javafx-graphics-24.0.1-mac-aarch64.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-controls/24.0.1/javafx-controls-24.0.1-mac-aarch64.jar:/Users/fayeem/IdeaProjects/QuranWhispers_Backend/target/classes:/Users/fayeem/.m2/repository/com/google/genai/google-genai/1.4.1/google-genai-1.4.1.jar:/Users/fayeem/.m2/repository/org/xerial/sqlite-jdbc/3.44.1.0/sqlite-jdbc-3.44.1.0.jar:/Users/fayeem/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar:/Users/fayeem/.m2/repository/org/openjfx/javafx-fxml/24.0.1/javafx-fxml-24.0.1-mac-aarch64.jar -m com.example.server/com.example.server.HelloApplication
