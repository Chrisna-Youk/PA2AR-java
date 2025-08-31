package org.dnc;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import org.dnc.model.UserDto;
import org.dnc.net.UserService;
import org.dnc.security.JwtUtils;
import org.dnc.util.QrCodeUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoyaltyController {

    @FXML private Label lastNameLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label createdLabel;
    @FXML private Label pointsLabel;
    @FXML private ImageView avatarImage;
    @FXML private Label avatarPlaceholder;

    @FXML private ImageView qrImage;
    @FXML private BorderPane cardRoot;

    private int currentPoints = 0;

    @FXML
    private void initialize() {
        loadFromApi();
    }

    // Infos utilisateur
    private void loadFromApi() {
        if (!Session.isAuthenticated()) {
            setTextSafe(lastNameLabel, "Nom : (non connecté)");
            setTextSafe(firstNameLabel, "Prénom :");
            showPlaceholder(true);
            return;
        }

        final String token = Session.getAccessToken();
        final String uuid  = JwtUtils.getStringClaim(token, "uuid");
        if (uuid == null || uuid.isBlank()) {
            setTextSafe(lastNameLabel, "Nom : (uuid manquant)");
            return;
        }

        UserService.getUserByUuid(uuid, token).thenAccept(res ->
                Platform.runLater(() -> {
                    if (res.success() && res.user() != null) {
                        applyUser(res.user());
                    } else {
                        setTextSafe(lastNameLabel, "Nom : (erreur)");
                        setTextSafe(firstNameLabel, "Prénom :");
                    }
                })
        );
    }

    private void applyUser(UserDto u) {
        setTextSafe(lastNameLabel,  "Nom : " + safe(u.getLastName()));
        setTextSafe(firstNameLabel, "Prénom : " + safe(u.getFirstName()));
        if (u.getCreatedAt() != null) {
            setTextSafe(createdLabel, "Date de création : " + u.getCreatedAt());
        }

        currentPoints = (u.getLoyaltyPts() == null) ? 0 : u.getLoyaltyPts();
        setTextSafe(pointsLabel, "Nb points : " + currentPoints);

        String photo = u.getPhoto();
        if (photo != null && !photo.isBlank()) {
            try {
                String url = photo.startsWith("http") ? photo : getClass().getResource(photo).toExternalForm();
                avatarImage.setImage(new Image(url, true));
                showPlaceholder(false);
            } catch (Exception e) {
                showPlaceholder(true);
            }
        } else {
            showPlaceholder(true);
        }

        if (u.getUuid() != null && !u.getUuid().isBlank()) {
            showQr(u.getUuid());
        }
    }

    private void showQr(String uuid) {
        try {
            Image img = QrCodeUtil.fromText(uuid, 380);
            qrImage.setImage(img);
            qrImage.setVisible(true);
            qrImage.setManaged(true);
            qrImage.toFront();
        } catch (Exception e) {
            e.printStackTrace();
            qrImage.setVisible(false);
        }
    }

    private void showPlaceholder(boolean on) {
        avatarPlaceholder.setVisible(on);
        avatarImage.setVisible(!on);
    }

    // Export PDF
    @FXML
    private void onDownloadPdf() {
        try {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.WHITE);
            var fxImg = cardRoot.snapshot(params, null);
            var img   = SwingFXUtils.fromFXImage(fxImg, null);

            FileChooser fc = new FileChooser();
            fc.setTitle("Enregistrer la carte en PDF");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            fc.setInitialFileName("ma-carte.pdf");
            var file = fc.showSaveDialog(cardRoot.getScene().getWindow());
            if (file == null) return;

            try (PDDocument doc = new PDDocument()) {
                PDRectangle rect = new PDRectangle(img.getWidth(), img.getHeight());
                PDPage page = new PDPage(rect);
                doc.addPage(page);

                var pdImg = LosslessFactory.createFromImage(doc, img);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.drawImage(pdImg, 0, 0, rect.getWidth(), rect.getHeight());
                }
                doc.save(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Boutons promo : -10 et -20
    @FXML
    private void onPromo10() { tryRedeem(10, 100); }

    @FXML
    private void onPromo20() { tryRedeem(20, 200); }

    // Check sold en interne
    private void tryRedeem(int discount, int cost) {
        if (!Session.isAuthenticated()) {
            System.out.println("Utilisateur non connecté.");
            return;
        }
        if (currentPoints < cost) {
            System.out.printf("Pas assez de points (%d/%d), aucune requête envoyée.%n",
                    currentPoints, cost);
            return;
        }

        System.out.println("POST http://localhost:3001/api/v1/users/promocode");

        sendPromoAsync(discount, Session.getAccessToken());

        currentPoints -= cost;
        setTextSafe(pointsLabel, "Nb points : " + currentPoints);
    }

    /** Call API */
    private void sendPromoAsync(int amount, String bearerToken) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = "{\"amount\":" + amount + "}"; // adapte si ton backend attend un autre payload

            HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:3001/api/v1/users/promocode"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + bearerToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(resp -> {
                        System.out.println("Réponse promo: status=" + resp.statusCode());
                        System.out.println(resp.body());
                    })
                    .exceptionally(ex -> {
                        System.out.println("Erreur envoi promo:");
                        ex.printStackTrace();
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextSafe(Label l, String t) { if (l != null) l.setText(t); }
    private String safe(String s) { return s == null ? "" : s; }
}
