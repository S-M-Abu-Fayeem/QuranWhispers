module org.example.quranwhispers_frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires org.json;


    opens quranwhispers_frontend to javafx.fxml;
    opens controller to javafx.fxml;

    exports quranwhispers_frontend;
    exports controller;
}