package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.service.ScoreboardService;
import at.technikum.apps.mtcg.service.StatsService;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class ScoreboardController implements Controller{

    private final StatsService statsService;
    private final ScoreboardService scoreboardService;

    public ScoreboardController() {

        this.statsService = new StatsService();
        this.scoreboardService = new ScoreboardService();
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/scoreboard");
    }

    @Override
    public Response handle(Request request) {

        if (!request.getMethod().equals("GET")) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route scoreboard only takes get requests");
        }

        return scoreboardService.getScoreboard(request);
    }
}
