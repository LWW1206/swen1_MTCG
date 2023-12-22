package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.entity.UserStats;
import at.technikum.apps.mtcg.repository.StatsRepository;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StatsService {

    private final UserService userService;
    private final StatsRepository statsRepository;
    private final ObjectMapper objectMapper;

    public StatsService() {
        this.userService = new UserService();
        this.statsRepository = new StatsRepository();
        this.objectMapper = new ObjectMapper();
    }

    public Response getStatsOfUser(Request request) {
        if (!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        return getStatsFomDB(AuthorizationHelper.getNameFromToken(request));
    }

    private Response getStatsFomDB(String username) {
        try {
            UserStats retrivedUserStats = statsRepository.getUserStats(username);
            String userStatsJson = objectMapper.writeValueAsString(retrivedUserStats);
            return ResponseHelper.generateResponse(HttpStatus.OK, userStatsJson);
        } catch (JsonProcessingException e) {
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "something went wrong retrieving userstats");
        }
    }

    private void updateStats(UserStats userStats) {
        try {
            statsRepository.updateUserStats(userStats);
            ResponseHelper.generateResponse(HttpStatus.OK, "The stats were updated sucessfully");
            //TODO no response needed
        } catch (RuntimeException e) {
            ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "something went wrong updating userstats");
            //TODO logger maybe
        }
    }

    void updateUserStats(String winningPlayer, String losingPlayer) {
        // Fetch existing stats for the players
        UserStats userStatsWin = checkUser(statsRepository.getUserStats(winningPlayer), winningPlayer); // checkUser initializes the DB row for player if not done yet
        UserStats userStatsLose = checkUser(statsRepository.getUserStats(losingPlayer), losingPlayer);

        //update stats
        //winning player
        userStatsWin.setGamesPlayed(userStatsWin.getGamesPlayed() + 1);
        userStatsWin.setGamesWon(userStatsWin.getGamesWon() + 1);
        userStatsWin.setElo(userStatsLose.getElo() + 3);
        //losing player
        userStatsLose.setGamesPlayed(userStatsLose.getGamesPlayed() + 1);
        userStatsLose.setGamesLost(userStatsLose.getGamesLost() + 1);
        userStatsLose.setElo(userStatsLose.getElo() - 5);

        updateStats(userStatsWin);
        updateStats(userStatsLose);
    }
    private UserStats checkUser(UserStats playerStats, String playerName) {
        if(playerStats == null) {
            playerStats = new UserStats(playerName, 0 , 0, 0, 100);
            statsRepository.createUserStats(playerStats);
        }
        return playerStats;
    }


}
