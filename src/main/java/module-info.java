module org.dnc {
    requires javafx.controls;
    requires javafx.fxml;

    exports org.dnc;
    opens org.dnc to javafx.fxml;

    exports org.dnc.ui;
    opens org.dnc.ui to javafx.fxml;
}
