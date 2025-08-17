package org.dnc.ui;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class FoodTruckCard extends VBox {

    @FXML private ImageView photo;
    @FXML private Label photoPlaceholder;
    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label priceLabel;

    // Model carte de foodtruck > FXML
    private final StringProperty name = new SimpleStringProperty(this, "name", "");
    private final StringProperty location = new SimpleStringProperty(this, "location", "");
    private final StringProperty specialty = new SimpleStringProperty(this, "specialty", "");
    private final StringProperty price = new SimpleStringProperty(this, "price", "");
    private final StringProperty imageUrl = new SimpleStringProperty(this, "imageUrl", "");

    public FoodTruckCard() {
        FXMLLoader loader = new FXMLLoader(FoodTruckCard.class.getResource("food_truck_card.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FoodTruckCard FXML", e);
        }
    }

    @FXML
    private void initialize() {
        // Model carte de foodtruck
        nameLabel.textProperty().bind(name);
        locationLabel.textProperty().bind(location);
        specialtyLabel.textProperty().bind(specialty);
        priceLabel.textProperty().bind(price);

        imageUrl.addListener((obs, oldV, newV) -> loadImage(newV));
        loadImage(getImageUrl());
    }

    private void loadImage(String url) {
        if (url == null || url.isBlank()) {
            photo.setImage(null);
            photo.setVisible(false);
            photoPlaceholder.setVisible(true);
            return;
        }
        try {
            String resolved = url.startsWith("http")
                    ? url
                    : getClass().getResource(url).toExternalForm();
            Image img = new Image(resolved, true);
            photo.setImage(img);
            photo.setVisible(true);
            photoPlaceholder.setVisible(false);
        } catch (Exception e) {
            photo.setImage(null);
            photo.setVisible(false);
            photoPlaceholder.setVisible(true);
        }
    }


    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
    public StringProperty nameProperty() { return name; }

    public String getLocation() { return location.get(); }
    public void setLocation(String v) { location.set(v); }
    public StringProperty locationProperty() { return location; }

    public String getSpecialty() { return specialty.get(); }
    public void setSpecialty(String v) { specialty.set(v); }
    public StringProperty specialtyProperty() { return specialty; }

    public String getPrice() { return price.get(); }
    public void setPrice(String v) { price.set(v); }
    public StringProperty priceProperty() { return price; }

    public String getImageUrl() { return imageUrl.get(); }
    public void setImageUrl(String v) { imageUrl.set(v); }
    public StringProperty imageUrlProperty() { return imageUrl; }
}
