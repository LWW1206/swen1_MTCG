package at.technikum.apps.mtcg.controller.helpers;

import at.technikum.server.http.Request;

public class AuthorizationHelper {

    public static String extractName(String Token) {
        return Token.split("-")[0];
    }

    public static String getNameFromToken(Request request) {
        String token = request.getToken(request);
        return extractName(token);
    }

    public static boolean isAdmin(Request request) {

        String token = request.getToken(request);
        return token != null && token.equals("admin-mtcgToken");
    }

    public static String getNameFromRoute(Request request) {
        String[] path = request.getRoute().split("/");
        return path[2];
    }

}
