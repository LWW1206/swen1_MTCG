package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.entity.User;
import at.technikum.apps.mtcg.entity.UserStats;
import at.technikum.apps.mtcg.repository.StatsRepository;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;

public class ScoreboardService {

    private final StatsRepository statsRepository;
    private final UserService userService;

    public ScoreboardService() {
        this.statsRepository = new StatsRepository();
        this.userService = new UserService();
    }


    public Response getScoreboard(Request request) {
        if (!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");
        return retrieveScoreboard();
    }

    private Response retrieveScoreboard() {
        try {
            List<UserStats> retrievedUserstats = statsRepository.retrieveScoreboard();
            return ResponseHelper.generateResponse(HttpStatus.OK, retrievedUserstats.toString());
        } catch (RuntimeException e) {
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "something went wrong retrieving the scoreboard");
        }

    }
}
