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
                return ResponseHelper.generateResponse(HttpStatus.OK, "Waiting for an opponent...");
            } else {
                isBattlePending = false;
                return startBattle(request);
            }
        }
    }

    private Response startBattle(Request secondRequest) {
        // Notify the waiting thread that a battle is pending
        synchronized (pendingBattleRequest) {
            pendingBattleRequest.notify();
        }

        // The thread has been notified, continue with the battle
        return battleService.startBattle(pendingBattleRequest, secondRequest);
    }
}
