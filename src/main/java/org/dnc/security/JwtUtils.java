package org.dnc.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class JwtUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtUtils() {}

    /** Renvoie le payload JSON du JWT (sans vérifier la signature). */
    public static JsonNode payload(String jwt) {
        if (jwt == null) throw new IllegalArgumentException("JWT null");
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("JWT invalide");

        String payloadB64 = pad(parts[1]); // Base64URL -> padding
        byte[] bytes = Base64.getUrlDecoder().decode(payloadB64);
        String json = new String(bytes, StandardCharsets.UTF_8);
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload JSON invalide", e);
        }
    }

    /** Récupère un claim String du payload, ou null s'il est absent. */
    public static String getStringClaim(String jwt, String claimName) {
        JsonNode node = payload(jwt).path(claimName);
        return node.isMissingNode() || node.isNull() ? null : node.asText();
    }

    /** Ajoute le padding '=' manquant (Base64URL exige un multiple de 4). */
    private static String pad(String s) {
        int m = s.length() % 4;
        return m == 0 ? s : s + "=".repeat(4 - m);
    }
}
