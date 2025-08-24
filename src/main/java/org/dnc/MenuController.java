package org.dnc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.dnc.model.MenuItemDto;

import java.io.InputStream;
import java.util.List;

public class MenuController {

    @FXML private Label titleLabel;
    @FXML private TableView<MenuItemDto> menuTable;
    @FXML private TableColumn<MenuItemDto, String> colName;
    @FXML private TableColumn<MenuItemDto, String> colPrice;
    @FXML private TableColumn<MenuItemDto, String> colDesc;

    private String truckName;
    private String slug;

    @FXML
    private void initialize() {
        // Pas de PropertyValueFactory -> pas besoin d'ouvrir le module vers javafx.base
        colName.setCellValueFactory(cd -> new SimpleStringProperty(safe(cd.getValue().getName())));
        colPrice.setCellValueFactory(cd -> new SimpleStringProperty(safe(cd.getValue().getPrice())));
        colDesc.setCellValueFactory(cd -> new SimpleStringProperty(safe(cd.getValue().getDescription())));
    }

    private String safe(String s) { return s == null ? "" : s; }

    /** Appelé depuis HomeController juste après le load du FXML. */
    public void init(String truckName, String slug) {
        this.truckName = truckName;
        this.slug = slug;
        titleLabel.setText("Menu — " + truckName);
        loadMenuJson();
    }

    private void loadMenuJson() {
        String path = "/data/menus/" + slug + ".json";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                // pas de fichier → table vide, titre conservé
                System.err.println("⚠️ Menu JSON introuvable: " + path);
                menuTable.getItems().clear();
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            List<MenuItemDto> items = mapper.readValue(is, new TypeReference<List<MenuItemDto>>() {});
            menuTable.getItems().setAll(items);
        } catch (Exception e) {
            e.printStackTrace();
            menuTable.getItems().clear();
        }
    }
}
