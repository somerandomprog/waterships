module by.bsu.waterships.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires shared;

    opens by.bsu.waterships.client to javafx.fxml;
    exports by.bsu.waterships.client;
    exports by.bsu.waterships.client.controllers;
    opens by.bsu.waterships.client.controllers to javafx.fxml;
}