package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.service.BattleService;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static at.technikum.server.http.HttpStatus.OK;
import static at.technikum.server.http.HttpStatus.UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BattleControllerTest {

    @Test
    void handlePostRequestWhenNoPendingBattle() {
        // Arrange
        BattleController battleController = mock(BattleController.class);
        Request requestMock = Mockito.mock(Request.class);
        when(requestMock.getMethod()).thenReturn("POST");

        battleController.handle(requestMock);

        verify(battleController, never()).startBattle(any(Request.class), any(Request.class));

    }

    @Test
    void startBattle() {
        // Arrange
        BattleController battleController = mock(BattleController.class);
        Request firstRequestMock = Mockito.mock(Request.class);
        Request secondRequestMock = Mockito.mock(Request.class);

        battleController.startBattle(firstRequestMock, secondRequestMock);

        // Assert
        verify(battleController, times(1)).startBattle(firstRequestMock, secondRequestMock);
        // Add more assertions if needed
    }


}