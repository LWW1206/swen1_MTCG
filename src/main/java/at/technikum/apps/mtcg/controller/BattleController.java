package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.apps.mtcg.service.*;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class BattleController implements Controller {

    private final BattleService battleService;
    private Request pendingBattleRequest;
    private boolean isBattlePending;
    public BattleController() {
        this.battleService = new BattleService();
        this.isBattlePending = false;
    }
    @Override
    public boolean supports(String route) {
        return route.startsWith("/battle");
    }

    @Override
    public Response handle(Request request) {
        if (!request.getMethod().equals("POST")) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route battle only takes post requests");
        }
        synchronized (this) {
            if (!isBattlePending) {
                isBattlePending = true;
                pendingBattleRequest = request;
                return ResponseHelper.generateResponse(HttpStatus.OK, "Waiting for an opponent...");
            } else {
                isBattlePending = false;
                return battleService.startBattle(pendingBattleRequest, request);
            }
        }
    }
}
