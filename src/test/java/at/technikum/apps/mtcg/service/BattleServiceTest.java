package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.Player;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BattleServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private DeckService deckService;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private BattleService battleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartBattleWhenUserNotLoggedIn() {
        // Arrange
        Request firstRequest = mock(Request.class);
        Request secondRequest = mock(Request.class);
        when(userService.tokenExists(firstRequest)).thenReturn(false);

        // Act
        Response response = battleService.startBattle(firstRequest, secondRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
        assertEquals("user not logged in", response.getBody());
    }


}
