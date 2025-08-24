package org.dnc;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene; // (utile si tu fais d'autres choses)
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dnc.model.FoodTruckDto;
import org.dnc.ui.FoodTruckCard;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.List;

public class HomeController {

    @FXML private TabPane tabPane;
    @FXML private TilePane cardsPane;
    @FXML private ScrollPane trucksScroll;   // <-- ajouté

    private Node listContent;                // on mémorise la vue “liste”
    private HostServices hostServices;
    void setHostServices(HostServices hostServices) { this.hostServices = hostServices; }

    @FXML
    private void initialize() {
        // Défaut, afficher l’onglet "Les Foodtrucks"
        if (tabPane != null && tabPane.getTabs().size() >= 3) {
            tabPane.getSelectionModel().select(2);
        }

        // Mémorise le contenu "liste" initial du ScrollPane
        if (trucksScroll != null) {
            listContent = trucksScroll.getContent();
        }

        // Remplit la liste depuis le JSON
        populateFromJson();
    }

    /** Charge /data/foodtrucks.json et remplit le TilePane. */
    private void populateFromJson() {
        cardsPane.getChildren().clear();
        try (InputStream is = getClass().getResourceAsStream("/data/foodtrucks.json")) {
            if (is == null) {
                System.err.println("/data/foodtrucks.json introuvable — ajout d'un exemple par défaut.");
                addClickableCard("Error Truck", "Error 404", "Burger", "€€", "/images/truck1.jpg");
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            List<FoodTruckDto> trucks = mapper.readValue(is, new TypeReference<List<FoodTruckDto>>() {});
            for (FoodTruckDto t : trucks) {
                String location = (t.getCity() != null ? t.getCity() : "") +
                        (t.getPostal() != null && !t.getPostal().isBlank() ? " " + t.getPostal() : "");
                addClickableCard(t.getName(), location, t.getType(), t.getPrice(), t.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            addClickableCard("Error Truck", "Error 404", "Burger", "€€", "/images/truck1.jpg");
        }
    }

    /** Carte cliquable */
    private void addClickableCard(String name, String location, String specialty, String price, String imageUrl) {
        FoodTruckCard card = new FoodTruckCard();
        card.setName(name);
        card.setLocation(location);
        card.setSpecialty(specialty);
        card.setPrice(price);
        card.setImageUrl(imageUrl);

        card.setFocusTraversable(true);
        final String slug = slugify(name);

        card.setOnMouseClicked(e -> openMenuInPlace(name, slug));
        card.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                openMenuInPlace(name, slug);
            }
        });

        cardsPane.getChildren().add(card);
    }

    private void openMenuInPlace(String truckName, String slug) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu_view.fxml"));
            Parent menuRoot = loader.load();
            MenuController controller = loader.getController();
            controller.init(truckName, slug);

            // Barre de navigation locale (Retour + titre)
            Button back = new Button("← Retour");
            back.setOnAction(ae -> showList());
            Label title = new Label("Menu — " + truckName);
            title.getStyleClass().add("title");

            HBox topBar = new HBox(12, back, title);
            topBar.setPadding(new Insets(0, 0, 8, 0));

            VBox wrapper = new VBox(12, topBar, menuRoot);
            wrapper.setPadding(new Insets(16));
            VBox.setVgrow(menuRoot, Priority.ALWAYS);

            trucksScroll.setFitToWidth(true);
            trucksScroll.setContent(wrapper);

            // ESC
            trucksScroll.getScene().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) showList();
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void showList() {
        if (listContent != null) {
            trucksScroll.setContent(listContent);
            // On peut enlever le handler ESC si tu veux :
            if (trucksScroll.getScene() != null) {
                trucksScroll.getScene().setOnKeyPressed(null);
            }
        }
    }

    /** "Pizza Wheels" -> "pizza-wheels" (pour retrouver le fichier JSON du menu). */
    // Fonction qui permet de formater le nom du foodtruck afin de retrouver le JSON
    // Sera surement mené à être supprimé/modifié pour les call API
    private String slugify(String input) {
        if (input == null) return "";
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase().replaceAll("[^a-z0-9\\-]", "");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent loginRoot = loader.load();
            LoginController loginController = loader.getController();
            loginController.setHostServices(hostServices);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("DnC - Login");
            stage.getScene().setRoot(loginRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
