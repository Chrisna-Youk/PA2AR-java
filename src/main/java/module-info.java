module org.dnc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    exports org.dnc;
    opens org.dnc to javafx.fxml;

    exports org.dnc.ui;
    opens org.dnc.ui to javafx.fxml;

    requires com.fasterxml.jackson.databind;
    opens org.dnc.model to com.fasterxml.jackson.databind;

    requires com.google.zxing;

    requires org.apache.pdfbox;
    requires javafx.swing;
    requires java.desktop;

}
