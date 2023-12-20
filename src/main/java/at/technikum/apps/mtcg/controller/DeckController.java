package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.service.DeckService;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class DeckController implements Controller {

    private final DeckService deckService;

    public DeckController() {
        this.deckService = new DeckService();
    }
    @Override
    public boolean supports(String route) {
        return route.startsWith("/deck");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().startsWith("/deck")) {
            switch (request.getMethod()) {
                case "GET" -> {
                    return getDeck(request);
                }
                case "PUT" -> {
                    return configureDeck(request);
                }
            }
            }
            return ResponseHelper.generateResponse(HttpStatus.OK, "in deckcontroller");
    }

    private Response configureDeck(Request request) {
        return deckService.configureDeck(request);
    }

    private Response getDeck(Request request) {
        return deckService.getDeck(request);
    }


}
