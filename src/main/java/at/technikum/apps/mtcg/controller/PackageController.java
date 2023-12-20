package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.apps.mtcg.service.PackageService;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageController implements Controller {
    private final PackageService packageService;

    public PackageController() { this.packageService = new PackageService(new PackageRepository(), new CardRepository()); }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/packages");
    }

    @Override
    public Response handle(Request request) {

        if (!request.getMethod().equals("POST")) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route package only takes post requests");
        }

        if (!AuthorizationHelper.isAdmin(request)) {
            return ResponseHelper.generateResponse(HttpStatus.FORBIDDEN, "Provided user is not admin");
        }
        if (AuthorizationHelper.isAdmin(request)) {
            return createPackage(request);
        }

        return ResponseHelper.generateResponse(HttpStatus.NOT_FOUND, "smt went wrong");
    }

    private Response createPackage(Request request) {
        String requestBody = request.getBody();
        requestBody = requestBody.substring(1, requestBody.length() - 1); //trim the [ ]

        try {
            List<Card> cardList = new ArrayList<>();

            String[] cardsArray = requestBody.split("\\s*,\\s*(?=\\{)"); // split

            for (String cardData : cardsArray) {
                Card newCard = new Card(cardData);
                cardList.add(newCard);
            }

            packageService.savePackage(cardList);
            return ResponseHelper.generateResponse(HttpStatus.CREATED, "Package created successfully");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

