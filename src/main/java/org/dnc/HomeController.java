// HomeController.java
package org.dnc;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.dnc.ui.FoodTruckCard;

public class HomeController {

    @FXML private TilePane cardsPane;

    private HostServices hostServices;
    void setHostServices(HostServices hostServices) { this.hostServices = hostServices; }

    @FXML
    private void initialize() {
        // TODO : JSON TRANSFORM
        FoodTruckCard c1 = createCard("Chinese foodNtruck", "Paris 75001", "Asiatique", "€€", "/images/truck1.jpg");
        FoodTruckCard c2 = createCard("Pizza Wheels",       "Lyon 69002",  "Italien",   "€",  "/images/truck1.jpg");
        FoodTruckCard c3 = createCard("Taco Mobile",        "Marseille 13001", "Mexicain","€€€", "/images/truck1.jpg");

        cardsPane.getChildren().setAll(c1, c2, c3);
    }

    private FoodTruckCard createCard(String name, String location, String specialty, String price, String imageUrl) {
        FoodTruckCard card = new FoodTruckCard();
        card.setName(name);
        card.setLocation(location);
        card.setSpecialty(specialty);
        card.setPrice(price);
        card.setImageUrl(imageUrl);
        return card;
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent loginRoot = loader.load();

            LoginController loginController = loader.getController();
            loginController.setHostServices(hostServices);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("DnC - Login");
            stage.getScene().setRoot(loginRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
