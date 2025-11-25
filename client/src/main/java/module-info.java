module by.bsu.waterships.client {
    requires by.bsu.waterships.shared;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.media;

    opens by.bsu.waterships.client to javafx.fxml;
    opens by.bsu.waterships.client.controllers to javafx.fxml;
    opens by.bsu.waterships.client.state to javafx.fxml;
    exports by.bsu.waterships.client;
    exports by.bsu.waterships.client.controllers;
    exports by.bsu.waterships.client.state;
}