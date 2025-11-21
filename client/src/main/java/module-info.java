module by.bsu.waterships.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens by.bsu.waterships.client to javafx.fxml;
    exports by.bsu.waterships.client;
}