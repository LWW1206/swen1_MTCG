package at.technikum.apps.mtcg.controller.helpers;

import at.technikum.server.http.Request;

public class authorizationHelper {

    public static String extractName(String Token) {
        return Token.split("-")[0];
    }

    public static String getUsername(Request request) {
        String token = request.getToken(request);
        return extractName(token);
    }

}
