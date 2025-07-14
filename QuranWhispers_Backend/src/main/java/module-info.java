module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires google.genai;


    opens com.example.server to javafx.fxml;
    exports com.example.server;
}