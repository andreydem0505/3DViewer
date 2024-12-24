module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;
    requires com.google.gson;


    opens com.cgvsu to javafx.fxml;
    exports com.cgvsu;
    exports com.cgvsu.model;
    opens com.cgvsu.model to javafx.fxml;
    exports com.cgvsu.gui;
    opens com.cgvsu.gui to javafx.fxml;
}