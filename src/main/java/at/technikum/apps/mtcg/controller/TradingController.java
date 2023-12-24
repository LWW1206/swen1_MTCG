package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.service.TradingService;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TradingController implements Controller {

    private final TradingService tradingService;

    public TradingController() {
        this.tradingService = new TradingService();
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/tradings");
    }

    @Override
    public Response handle(Request request) {
        return switch (request.getMethod()) {
            case "GET" -> getTradingDeal(request);
            case "POST" -> createTradingDeal(request);
            case "DELETE" -> deleteTradingDeal(request);
            default -> ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "only get, post & delete in trading route");
        };
    }


    private Response deleteTradingDeal(Request request) {
        return tradingService.deleteDeal(request);
    }

    private Response createTradingDeal(Request request) {
        return tradingService.createDeal(request);
    }

    private Response getTradingDeal(Request request) {
        return tradingService.getDeal(request);
    }
}
