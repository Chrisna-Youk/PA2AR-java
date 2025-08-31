package org.dnc.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.dnc.model.MenuItemDto;

import java.io.IOException;
import java.util.function.Consumer;

public class MenuItemCell extends ListCell<MenuItemDto> {

    @FXML private BorderPane root;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private Label descLabel;
    @FXML private ImageView photo;
    @FXML private Label placeholderLabel;
    @FXML private Button addBtn;

    private final Consumer<MenuItemDto> onAdd;

    public MenuItemCell(Consumer<MenuItemDto> onAdd) {
        this.onAdd = onAdd;
        FXMLLoader loader = new FXMLLoader(MenuItemCell.class.getResource("menu_item_cell.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load menu_item_cell.fxml", e);
        }
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(null);
    }

    @Override
    protected void updateItem(MenuItemDto item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        nameLabel.setText(nonNull(item.getName()));
        priceLabel.setText(nonNull(item.getPrice()));
        descLabel.setText(nonNull(item.getDescription()));

        // Image (ressource interne ou URL)
        photo.setImage(null);
        placeholderLabel.setVisible(true);
        String img = item.getImage();
        if (img != null && !img.isBlank()) {
            try {
                String url = img.startsWith("http")
                        ? img
                        : getClass().getResource(img).toExternalForm();
                Image image = new Image(url, true);
                photo.setImage(image);
                placeholderLabel.setVisible(false);
            } catch (Exception ignored) { /* garde le placeholder */ }
        }

        addBtn.setOnAction(ev -> {
            if (onAdd != null) onAdd.accept(item);
        });

        setGraphic(root);
    }

    private String nonNull(String s) { return s == null ? "" : s; }
}
