package org.dnc;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private HostServices hostServices;

    void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private void onLogin() {
        errorLabel.setText("");
        String login = loginField.getText();
        String pass  = passwordField.getText();

        if ("admin".equals(login) && "admin".equals(pass)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/home_view.fxml"));
                Parent homeRoot = loader.load();

                HomeController homeController = loader.getController();
                homeController.setHostServices(hostServices);

                Scene scene = loginField.getScene();
                scene.setRoot(homeRoot);

                Stage stage = (Stage) scene.getWindow();
                stage.setTitle("DnC - Home");
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Unable to load home view");
            }
        } else {
            errorLabel.setText("Invalid credentials");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    @FXML
    private void onForgotPassword() {
        if (hostServices != null) {
            hostServices.showDocument("https://google.fr");
        }
    }
}
