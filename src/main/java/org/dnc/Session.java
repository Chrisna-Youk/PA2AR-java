package org.dnc;

public final class Session {
    private static String accessToken;

    private Session() {}

    public static void setAccessToken(String token) { accessToken = token; }
    public static String getAccessToken() { return accessToken; }
    public static boolean isAuthenticated() { return accessToken != null && !accessToken.isBlank(); }
    public static void clear() { accessToken = null; }
}
