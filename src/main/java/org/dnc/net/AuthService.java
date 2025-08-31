package org.dnc.net;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class AuthService {
    // URL de ton API
    private static final String AUTH_URL = "http://127.0.0.1:3001/api/v1/auth/access";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static CompletableFuture<LoginResult> login(String email, String password) {
        try {
            String body = MAPPER.writeValueAsString(Map.of(
                    "email", email,
                    "password", password
            ));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenApply(resp -> {
                        int code = resp.statusCode();
                        String respBody = resp.body();
                        if (code != 200) {
                            return new LoginResult(false, null, "HTTP " + code);
                        }
                        try {
                            // Réponse attendue :
                            // { "data": { "error": false, "accessToken": "..." } }
                            JsonNode root = MAPPER.readTree(respBody);
                            JsonNode data = root.path("data");
                            boolean error = data.path("error").asBoolean(false);
                            String token = data.path("accessToken").asText(null);
                            if (!error && token != null && !token.isBlank()) {
                                return new LoginResult(true, token, "OK");
                            }
                            return new LoginResult(false, null, "Réponse inattendue");
                        } catch (Exception ex) {
                            return new LoginResult(false, null, "JSON invalide");
                        }
                    })
                    .exceptionally(ex ->
                            new LoginResult(false, null, ex.getClass().getSimpleName() + ": " + ex.getMessage())
                    );

        } catch (Exception e) {
            CompletableFuture<LoginResult> cf = new CompletableFuture<>();
            cf.completeExceptionally(e);
            return cf;
        }
    }

    public record LoginResult(boolean success, String accessToken, String message) {}
}
