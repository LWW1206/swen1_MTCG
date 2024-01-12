package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.service.BattleService;
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
    void handlePostRequestWrongMethod() {
        // Arrange
        BattleService battleServiceMock = mock(BattleService.class);
        BattleController battleController = new BattleController(battleServiceMock);
        Request requestMock = mock(Request.class);
        when(requestMock.getMethod()).thenReturn("PUT");

        // Act
        Response response = battleController.handle(requestMock);

        // Assert
        verify(requestMock, times(1)).getMethod();
        verify(battleServiceMock, never()).startBattle(any(Request.class), any(Request.class));
        assertEquals("route battle only takes post requests", response.getBody());
        assertEquals(UNAUTHORIZED, response.getStatus());
    }

    @Test
    void handlePostRequestWhenPendingBattle() {
        // Arrange
        BattleService battleServiceMock = mock(BattleService.class);
        BattleController battleController = new BattleController(battleServiceMock);
        Request requestMock = mock(Request.class);
        when(requestMock.getMethod()).thenReturn("POST");

        // Simulate a pending battle
        synchronized (battleController) {
            battleController.handle(requestMock); // This sets up a pending battle
        }

        // Act
        Response response = battleController.handle(requestMock);

        // Assert
        verify(requestMock, times(2)).getMethod(); // One from the first call and one from the second
        verify(battleServiceMock, times(1)).startBattle(any(Request.class), any(Request.class));
        assertEquals("Waiting for an opponent...", response.getBody());
        assertEquals(OK, response.getStatus());
    }
}
