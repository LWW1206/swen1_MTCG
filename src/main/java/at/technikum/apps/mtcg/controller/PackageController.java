package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.apps.mtcg.service.PackageService;
import at.technikum.apps.mtcg.template.ResponseHelper;
import at.technikum.apps.mtcg.template.card;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
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

        if (!isAdmin(request)) {
            return ResponseHelper.generateResponse(HttpStatus.OK, "not admin");
        }
        if (isAdmin(request)) {
            return createPackage(request);
        }

        return null;
    }

    private Response createPackage(Request request) {
        String requestBody = request.getBody();
        requestBody = requestBody.substring(1, requestBody.length() - 1); //trim the [ ]

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<card> cardList = new ArrayList<>();

            String[] cardsArray = requestBody.split("\\s*,\\s*(?=\\{)"); // split

            for (String cardData : cardsArray) {
                card newCard = new card(cardData);
                cardList.add(newCard);
            }

            packageService.savePackage(cardList);
            return ResponseHelper.generateResponse(HttpStatus.CREATED, "Package created successfully");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isAdmin(Request request) {
        String token = request.getToken(request);

        return token != null && token.equals("admin-mtcgToken");

    }
}

