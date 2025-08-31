package org.dnc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.dnc.model.MenuItemDto;
import org.dnc.ui.MenuItemCell;

import java.io.InputStream;
import java.util.List;

public class MenuController {

    @FXML private Label titleLabel;
    @FXML private ListView<MenuItemDto> menuList;

    private String truckName;
    private String slug;

    @FXML
    private void initialize() {
        menuList.setCellFactory(lv -> new MenuItemCell(this::onAddToCart));
    }

    public void init(String truckName, String slug) {
        this.truckName = truckName;
        this.slug = slug;
        titleLabel.setText("Menu — " + truckName);
        loadMenuJson();
    }

    private void loadMenuJson() {
        String path = "/data/menus/" + slug + ".json";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            ObjectMapper mapper = new ObjectMapper();
            List<MenuItemDto> items = mapper.readValue(is, new TypeReference<List<MenuItemDto>>() {});
            menuList.getItems().setAll(items);
        } catch (Exception e) {
            e.printStackTrace();
            menuList.getItems().clear();
        }
    }

    private void onAddToCart(MenuItemDto item) {
        System.out.println("Ajouté au panier : " + item.getName());
    }
}
