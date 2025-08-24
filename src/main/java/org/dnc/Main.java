package org.dnc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        // Page login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
        Parent root = loader.load();

        // Login Controller
        LoginController controller = loader.getController();
        controller.setHostServices(getHostServices());

        Scene scene = new Scene(root, 480, 360);

        // CSS
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        // Base setup
        stage.setTitle("DnC - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
