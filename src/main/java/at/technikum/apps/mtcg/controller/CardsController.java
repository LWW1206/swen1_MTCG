package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.responseHelper;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class CardsController implements Controller {

    @Override
    public boolean supports(String route) {
        return route.startsWith("cards");
    }

    @Override
    public Response handle(Request request) {
        return responseHelper.generateResponse(HttpStatus.OK, "in cards controller");
    }
}
