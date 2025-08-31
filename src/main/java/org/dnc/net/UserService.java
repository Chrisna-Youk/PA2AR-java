package org.dnc.net;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dnc.model.UserDto;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public final class UserService {
    private static final String BASE = "http://localhost:3001/api/v1";
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private UserService() {}

    public static CompletableFuture<Result> getUserByUuid(String uuid, String accessToken) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users/" + uuid))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() != 200) {
                        return new Result(false, null, "HTTP " + resp.statusCode());
                    }
                    try {
                        // on attend un JSON du type: { "data": { "error": false, "user": { ... } } }
                        JsonNode root = MAPPER.readTree(resp.body());
                        JsonNode userNode = root.path("data").path("user");
                        if (userNode.isMissingNode() || userNode.isNull()) {
                            return new Result(false, null, "Réponse sans 'user'");
                        }
                        UserDto u = MAPPER.treeToValue(userNode, UserDto.class);
                        return new Result(true, u, "OK");
                    } catch (Exception e) {
                        return new Result(false, null, "JSON invalide");
                    }
                })
                .exceptionally(ex -> new Result(false, null, ex.getMessage()));
    }

    public record Result(boolean success, UserDto user, String message) {}
}
