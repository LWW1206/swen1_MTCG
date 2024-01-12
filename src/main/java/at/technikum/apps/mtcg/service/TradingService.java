package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.TradingDeal;
import at.technikum.apps.mtcg.repository.TradingDealRepository;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;

public class TradingService {
    private final UserService userService;
    private final CardsService cardService;
    private final DeckService deckService;
    private final TradingDealRepository tradingDealRepository;
    ObjectMapper objectMapper = new ObjectMapper();


    public TradingService() {
        this.userService = new UserService(new UserRepository());
        this.cardService = new CardsService();
        this.deckService = new DeckService();
        this.tradingDealRepository = new TradingDealRepository();
    }

    public Response createDeal(Request request) {
        if (!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        String userName = AuthorizationHelper.getNameFromToken(request);
        if(request.getRoute().split("/").length != 3) // has a empty string [0] = empty
            return createTradeDeal(userName, request); //creates a deal with all info

        return createTradeRequest(request, userName, request.getRoute().split("/")[2]);  // only contains one card, trying to trade with a existing deal
    }

    private Response createTradeRequest(Request request, String username, String dealID ) {
        String requestBody = request.getBody();
        String cardId = requestBody.substring(1, requestBody.length() - 1);
        Card cardToBeTraded = cardService.getCardData(cardId);

        if(tradingDealRepository.getTradeCreator(dealID).equals(username))
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "you cannot trade with yourself");
        if(!tradingDealRepository.doesCardFitDeal(dealID, cardToBeTraded.getMonsterType(), cardToBeTraded.getDamage()))
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "the offered card did not fit the deal requirements");

        return processTrade(request, username, cardId, dealID);
    }

    private Response processTrade(Request request, String username, String cardId, String dealID) {
        try {
            String cardIdFromDeal = tradingDealRepository.getCardIdFromDeal(dealID);
            String usernameFromDeal = tradingDealRepository.getUserNameFromDeal(dealID);
            cardService.updateOwner(usernameFromDeal, cardId);
            cardService.updateOwner(username, cardIdFromDeal);
            deleteDeal(request);
        } catch (RuntimeException e) {
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "something went wrong trading");
        }

        return ResponseHelper.generateResponse(HttpStatus.OK, "sucessfully traded");
    }

    public Response createTradeDeal(String userName, Request request) {

        String cardToTrade;
        String dealId;
        String type;
        Float minimumDamage;

        try {
            TradingDeal tradingRequest = objectMapper.readValue(request.getBody(), TradingDeal.class);
            dealId = tradingRequest.getId();
            cardToTrade = tradingRequest.getCardToTrade();
            type = tradingRequest.getType();
            minimumDamage = tradingRequest.getMinimumDamage();
        } catch (Exception e) {
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format or missing fields");
        }

        if (!cardOwnedByUser(userName, cardToTrade) || !cardIsNotInDeck(userName, cardToTrade))// checks if card is not owned by user or is locked in the deck
            return ResponseHelper.generateResponse(HttpStatus.FORBIDDEN, "The deal contains a card that is not owned by the user or locked in the deck.");

        try{
            tradingDealRepository.createDeal(cardToTrade, dealId, type, minimumDamage, userName);
            return ResponseHelper.generateResponse(HttpStatus.OK, "Trading deal successfully created");
        } catch (SQLException e) {
            return ResponseHelper.generateResponse(HttpStatus.CONFLICT, "A deal with this deal ID already exist");
        }
    }

    private boolean cardOwnedByUser(String userName, String cardToTrade) {
        List<String> userCardsIds = cardService.getUsersCards(userName);
        for (String cardId : userCardsIds) {
            if (cardId.equals(cardToTrade)) {
                return true;
            }
        }
        return false;
    }

    private boolean cardIsNotInDeck(String username, String cardToTrade) {
        List<Card> usersDeck = deckService.getDeckAsList(username);
        for (Card card : usersDeck) {
            if (card.getId().equals(cardToTrade)) {
                return false; //Card is already in the user's deck
            }
        }
        return true; // Card is not in the user's deck
    }

    public Response getDeal(Request request) {
        if (!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        List <TradingDeal> retrievedDeals = tradingDealRepository.getAvailableDeals();
        if(retrievedDeals.isEmpty())
            return ResponseHelper.generateResponse(HttpStatus.OK, "The request was fine, but there are no trading deals available"); //TODO kommentiere 1 zeile aus bei abgabe
            //return ResponseHelper.generateResponse(HttpStatus.NO_CONTENT, "The request was fine, but there are no trading deals available");

        try {
            String dealsJson = objectMapper.writeValueAsString(retrievedDeals);
            return ResponseHelper.generateResponse(HttpStatus.OK, dealsJson);
        } catch (JsonProcessingException e) {
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "couldn't retrieve the Deals-Data");
        }
    }

    public Response deleteDeal(Request request) {
        if (!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        if(request.getRoute().split("/").length != 3) // has a empty string [0] = empty
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "dealid was not provided in the route");

        String dealIdtoBeDeleted = request.getRoute().split("/")[2];
        try{
            tradingDealRepository.deleteDeal(dealIdtoBeDeleted);
            return ResponseHelper.generateResponse(HttpStatus.OK, "Trading deal successfully deleted");
        } catch (SQLException e) {
            return ResponseHelper.generateResponse(HttpStatus.CONFLICT, "something went wrong deleting the deal");
        }
    }
}
