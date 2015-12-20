package io.yawp.commons.utils;

public class TestLoginManager {

    private static ThreadLocal<String> loggedUsername = new ThreadLocal<String>();

    public static void login(String username) {
        loggedUsername.set(username);
    }

    public static boolean isLogged(String username) {
        return isLogged() && getLoggedUsername().equals(username);
    }

    private static boolean isLogged() {
        return getLoggedUsername() != null;
    }

    public static String getLoggedUsername() {
        return loggedUsername.get();
    }

    public static void logout() {
        login(null);
    }

}
