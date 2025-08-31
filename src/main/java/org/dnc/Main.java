package org.dnc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
        @Override
    public void start(Stage stage) throws Exception {

        // Main logo
        stage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResource("/images/dnc.png").toExternalForm()
                )
        );
        // Page login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
        Parent root = loader.load();

        // Login Controller
        LoginController controller = loader.getController();
        controller.setHostServices(getHostServices());

        Scene scene = new Scene(root, 780, 600);
        stage.setMinWidth(780);
        stage.setMinHeight(600);

        // CSS
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        // Base setup
        stage.setTitle("DnC - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
