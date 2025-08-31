package org.dnc;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.dnc.net.AuthService;
import org.dnc.security.JwtUtils;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;

    private HostServices hostServices;
    void setHostServices(HostServices hostServices) { this.hostServices = hostServices; }

    @FXML
    private void onLogin() {
        errorLabel.setText("");

        final String email = loginField.getText().trim();
        final String pass  = passwordField.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Veuillez renseigner email et mot de passe.");
            return;
        }

        loginBtn.setDisable(true);

        // Call API asynchrone
        AuthService.login(email, pass).thenAccept(result ->
                Platform.runLater(() -> {
                    loginBtn.setDisable(false);
                    if (result.success()) {
                        Session.setAccessToken(result.accessToken());
                        System.out.println("Token présent ? " + Session.isAuthenticated());

                        // get uuid
                        String uuid = JwtUtils.getStringClaim(result.accessToken(), "uuid");
                        System.out.println("UUID utilisateur = " + uuid);

                        goToHome();
                    } else {
                        errorLabel.setText("Échec de connexion : " + result.message());
                    }
                })
        );
    }

    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home_view.fxml"));
            Parent root = loader.load();
            HomeController home = loader.getController();
            home.setHostServices(hostServices);

            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("DnC - Home");
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Impossible de charger la page Home.");
        }
    }

    @FXML
    private void onForgotPassword() {
        if (hostServices != null) {
            hostServices.showDocument("https://google.fr");
        }
    }
}
