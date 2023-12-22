package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.apps.mtcg.service.CardsService;
import at.technikum.apps.mtcg.service.PackageService;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CardsController implements Controller {

    private final UserService userService;
    private final CardsService cardsService;
    private final PackageService packageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CardsController() {
        this.userService = new UserService();
        this.cardsService = new CardsService();
        this.packageService = new PackageService(new PackageRepository(), new CardRepository());
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/cards");
    }

    @Override
    public Response handle(Request request) {
        if (!request.getMethod().equals("GET")) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route cards only takes get requests");
        }
        if(!userService.tokenExists(request)) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "no token set/not logged in");
        }
        return getCards(request);
    }

    private Response getCards(Request request) {
        String username = AuthorizationHelper.getNameFromToken(request);

        try {
            List <Card> retrivedCards;
            List<String> cardsIds = cardsService.getUsersCards(username);
            retrivedCards = cardsService.getAllCardData(cardsIds);
            String cardsJson = objectMapper.writeValueAsString(retrivedCards);
            if(retrivedCards.isEmpty()) {
                return ResponseHelper.generateResponse(HttpStatus.NO_CONTENT, "The request was fine, but the user doesn't have any cards");
            }
            return ResponseHelper.generateResponse(HttpStatus.OK, cardsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
