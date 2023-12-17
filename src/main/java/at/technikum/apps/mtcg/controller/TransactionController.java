package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.PackageService;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;

public class TransactionController implements Controller {

    private final UserService userService = new UserService(new UserRepository());
    private final PackageService packageService = new PackageService(new PackageRepository(), new CardRepository());

    public TransactionController() {

    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/transactions");
    }

    @Override
    public Response handle(Request request) {

        if (!request.getMethod().equals("POST")) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route transaction only takes post requests");
        }

        return verifyToken(request);
    }

    private Response verifyToken(Request request) {
        if(!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.NOT_FOUND, "user not logged in");
        else
            return buyPackage(AuthorizationHelper.getNameFromToken(request));
    }

    private Response buyPackage(String name) {
        if(!packageService.packageAvailable()) {
            return ResponseHelper.generateResponse(HttpStatus.NOT_FOUND, "No card package available for buying");
        }
        else if(!userService.enoughCoins(name)) {
            return ResponseHelper.generateResponse(HttpStatus.FORBIDDEN, "Not enough money for buying a card package");
        }
        packageService.buyPackage(name);
        userService.buyPackage(name);
        return ResponseHelper.generateResponse(HttpStatus.OK, "A package has been successfully bought");
    }
}
