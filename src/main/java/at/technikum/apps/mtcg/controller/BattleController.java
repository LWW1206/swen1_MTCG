package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.service.BattleService;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class BattleController implements Controller {

    private final BattleService battleService;
    private Request pendingBattleRequest;
    private boolean isBattlePending;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
        this.isBattlePending = false;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/battle");
    }

    @Override
    public Response handle(Request request) {
        synchronized (this) {
            if (!isBattlePending) {
                isBattlePending = true;
                pendingBattleRequest = request;
                try {
                    wait(); // Release the lock and wait for notification
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return ResponseHelper.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error during battle initiation.");
                }
                return ResponseHelper.generateResponse(HttpStatus.OK, "Battle started!");
            } else {
                isBattlePending = false;
                Request firstRequest = pendingBattleRequest;
                pendingBattleRequest = null; // Reset the pending request

                // Release the lock immediately after retrieving the pending request
                notify();

                return startBattle(firstRequest, request);
            }
        }
    }

    Response startBattle(Request firstRequest, Request secondRequest) {
        return battleService.startBattle(firstRequest, secondRequest);
    }
}