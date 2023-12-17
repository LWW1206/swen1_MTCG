package at.technikum.apps.mtcg.controller.helpers;

import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Response;

public class responseHelper {

    public static Response generateResponse(HttpStatus status, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setBody(body);
        return response;
    }
}
