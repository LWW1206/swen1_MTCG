package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.DeckRepository;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeckService {

    private final UserService userService;
    private final DeckRepository deckRepository;
    private final CardsService cardsService;
    private final PackageService packageService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public DeckService() {
        this.userService = new UserService();
        this.deckRepository = new DeckRepository();
        this.cardsService = new CardsService();
        this.packageService = new PackageService(new PackageRepository(), new CardRepository());
    }

    public Response configureDeck(Request request) {
        if (!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        List <String> CardIds = getCardsFromBody(request.getBody());
        if(CardIds.size() != 4)
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "The provided deck did not include the required amount of cards");

        String userName = AuthorizationHelper.getNameFromToken(request);
        if(!ownsCards(userName, CardIds))
            return getDeck(request); // curl says to show the deck from before if fail
            //return ResponseHelper.generateResponse(HttpStatus.FORBIDDEN, "At least one of the provided cards does not belong to the user or is not available");

        return createDeck(CardIds, userName);
    }

    private Response createDeck(List<String> cardIds, String userName) {
        deckRepository.createDeck(cardIds, userName);
        return ResponseHelper.generateResponse(HttpStatus.OK, "The deck has been sucessfully configured");
    }

    private boolean ownsCards(String userName, List <String> deckCardIds) {
        List<String> userCardIds = packageService.getCardIds(userName);

        for (String deckCardId : deckCardIds) {
            if (!userCardIds.contains(deckCardId)) {
                return false;
            }
        }
        return true;
    }

    private List<String> getCardsFromBody(String requestBody) {
        String content = requestBody.substring(1, requestBody.length() - 1);
        List <String> idStrings = Arrays.asList(content.split(","));
        for (String uuidString : idStrings) {
            idStrings.set(idStrings.indexOf(uuidString), uuidString.trim().replaceAll("\"", ""));
        }
        return idStrings;
    }

    public Response getDeck(Request request) {
        if (!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        String name = AuthorizationHelper.getNameFromToken(request);
        List<String> cardIds = getCardIdsFromDeck(name); //fromDeck
        if (cardIds.isEmpty())
            return ResponseHelper.generateResponse(HttpStatus.OK, "The request was fine, but the deck doesn't have any cards");
            //return ResponseHelper.generateResponse(HttpStatus.NO_CONTENT, "The request was fine, but the deck doesn't have any cards");

        if(isFormatPlain(request))
            return getDeckFromDBPlain(cardIds);

        return getDeckFromDBJson(cardIds);
    }

    private Response getDeckFromDBPlain(List<String> cardIds) {
        List<Card> retrievedCards;
        retrievedCards = cardsService.getAllCardData(cardIds);
        StringBuilder plainTextResponse = new StringBuilder();
        for (Card card : retrievedCards) {
            plainTextResponse.append(card.toString()).append("\n");
        }
        return ResponseHelper.generateResponse(HttpStatus.OK, plainTextResponse.toString());
    }

    private Response getDeckFromDBJson(List<String> cardIds) {
        List<Card> retrievedCards;
        retrievedCards = cardsService.getAllCardData(cardIds);
        try {
            String cardJson = objectMapper.writeValueAsString(retrievedCards);
            return ResponseHelper.generateResponse(HttpStatus.OK, cardJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List <String> getCardIdsFromDeck(String name) {
        return deckRepository.getDeckFromUser(name);
    }

    boolean isFormatPlain(Request request) {
        return request.getRoute().contains("?format=plain");
    }

    public List <Card> getDeckAsList(String name) {
        List<String> deckCardIds = deckRepository.getDeckFromUser(name);
        List<Card> deckCards = new ArrayList<>();
        for (String cardId : deckCardIds) {
            deckCards.add(cardsService.getCardData(cardId));
        }
        return deckCards;
    }
}
