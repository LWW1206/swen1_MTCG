package at.technikum.apps.mtcg;

import at.technikum.apps.mtcg.controller.*;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.BattleService;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.server.ServerApplication;
import at.technikum.server.http.HttpContentType;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.ArrayList;
import java.util.List;

public class MtcgApp implements ServerApplication {

    private final List<Controller> controllers = new ArrayList<>();

    public MtcgApp() {
        controllers.add(new UserController(new UserService(new UserRepository())));
        controllers.add(new SessionController());
        controllers.add(new PackageController());
        controllers.add(new TransactionController());
        controllers.add(new CardsController());
        controllers.add(new DeckController());
        controllers.add(new BattleController(new BattleService()));
        controllers.add(new StatsController());
        controllers.add(new ScoreboardController());
        controllers.add((new TradingController()));
    }

    @Override
    public Response handle(Request request) {

        for (Controller controller: controllers) {
            if (!controller.supports(request.getRoute())) {
                continue;
            }

            return controller.handle(request);
        }

        Response response = new Response();
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setBody("Route " + request.getRoute() + " not found in app!");

        return response;
    }
}
