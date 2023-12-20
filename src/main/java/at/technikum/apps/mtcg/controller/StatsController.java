package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class StatsController implements Controller {
    @Override
    public boolean supports(String route) {
        return route.startsWith("/stats");
    }

    @Override
    public Response handle(Request request) {

        if (!request.getMethod().equals("GET")) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route stats only takes get requests");
        }

        //TODO make a service for this, stats service
        return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route stats only takes get requests");
    }
}
