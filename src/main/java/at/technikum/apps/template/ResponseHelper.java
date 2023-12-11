package at.technikum.apps.template;

import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Response;

public class ResponseHelper {

    public static Response generateResponse(HttpStatus status, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setBody(body);
        return response;
    }
}
