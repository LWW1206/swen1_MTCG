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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BattleServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private DeckService deckService;

    @Mock
    private StatsService statsService;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private Card card1;

    @Mock
    private Card card2;


    @InjectMocks
    private BattleService battleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        battleService = new BattleService();
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

    @Test
    void checkSpecialties_AutoWinScenario_Player1Wins() {
        // Mock the cards and their names
        when(card1.getName()).thenReturn("Dragon");
        when(card2.getName()).thenReturn("Goblin");

        // Arrange the battle service
        battleService.player1 = new Player("Alice");
        battleService.player2 = new Player("Bob");
        battleService.deck1 = mock(ArrayList.class);
        battleService.deck2 = mock(ArrayList.class);

        // Stub the behavior of player's HP and damage for cards
        when(card1.getName()).thenReturn("Dragon");
        when(card2.getName()).thenReturn("Goblin");
        when(card1.getDamage()).thenReturn(10f);
        when(card2.getDamage()).thenReturn(5f);

        // Invoke the method under test
        String result = battleService.checkSpecialties(card1, card2);

        // Assert the result and player stats
        assertEquals("\nDragon auto-wins against Goblin", result);
        assertEquals(100f, battleService.player1.getHp()); // Assuming the deduction logic is correct
        assertEquals(90f, battleService.player2.getHp()); // Assuming the deduction logic is correct

        // Verify that certain methods were called
        verify(card1, times(1)).getName();
        verify(card1, times(1)).getDamage();
        verify(card2, times(1)).getName();
    }

    @Test
    void checkSpecialties_AutoWinScenario_Player2Wins() {
        // Mock the cards and their names
        when(card1.getName()).thenReturn("Orc");
        when(card2.getName()).thenReturn("Wizard");

        // Arrange the battle service
        battleService.player1 = new Player("Alice");
        battleService.player2 = new Player("Bob");
        battleService.deck1 = mock(ArrayList.class);
        battleService.deck2 = mock(ArrayList.class);

        // Stub the behavior of player's HP and damage for cards
        when(card1.getName()).thenReturn("Orc");
        when(card2.getName()).thenReturn("Wizard");
        when(card1.getDamage()).thenReturn(8f);
        when(card2.getDamage()).thenReturn(12f);

        // Invoke the method under test
        String result = battleService.checkSpecialties(card1, card2);

        // Assert the result and player stats
        assertEquals("\nWizard auto-wins against Orc", result);
        assertEquals(88f, battleService.player1.getHp()); // Assuming the deduction logic is correct
        assertEquals(100f, battleService.player2.getHp()); // Assuming the deduction logic is correct

        // Verify that certain methods were called
        verify(card1, times(1)).getName();
        verify(card2, times(1)).getName();
    }

    @Test
    void noSpeciality_NoAutoWinScenario() {
        // Mock the cards and their names
        when(card1.getName()).thenReturn("Goblin");
        when(card2.getName()).thenReturn("Dragon");

        // Arrange the battle service
        battleService.player1 = new Player("Alice");
        battleService.player2 = new Player("Bob");
        battleService.deck1 = mock(ArrayList.class);
        battleService.deck2 = mock(ArrayList.class);

        // Stub the behavior of player's HP and damage for cards
        when(card1.getName()).thenReturn("Goblin");
        when(card2.getName()).thenReturn("Orc");
        when(card1.getDamage()).thenReturn(5f);
        when(card2.getDamage()).thenReturn(10f);

        // Invoke the method under test
        String result = battleService.fight(card1, card2);

        // Assert the result and player stats
        assertEquals("\n" +
                "Goblin modified DMG: 5.0\n" +
                "Orc modified DMG: 10.0\n" +
                "Player 2's Card wins this round\n" +
                "Goblin gets moved to Player 2's deck\n", result);
        assertEquals(95f, battleService.player1.getHp()); // Assuming the deduction logic is correct
        assertEquals(100f, battleService.player2.getHp()); // Assuming the deduction logic is correct

        // Verify that certain methods were called
        verify(card1, times(3)).getName();
        verify(card1, times(1)).getDamage();
        verify(card2, times(2)).getName();
    }

    @Test
    void sameDamage() {
        // Mock the cards and their names
        when(card1.getName()).thenReturn("Goblin");
        when(card2.getName()).thenReturn("Dragon");

        // Arrange the battle service
        battleService.player1 = new Player("Alice");
        battleService.player2 = new Player("Bob");
        battleService.deck1 = mock(ArrayList.class);
        battleService.deck2 = mock(ArrayList.class);

        // Stub the behavior of player's HP and damage for cards
        when(card1.getName()).thenReturn("Goblin");
        when(card2.getName()).thenReturn("Orc");
        when(card1.getDamage()).thenReturn(10f);
        when(card2.getDamage()).thenReturn(10f);

        // Invoke the method under test
        String result = battleService.fight(card1, card2);

        // Assert the result and player stats
        assertEquals("\n" +
                "The Cards dealt same damage, it is a draw\n", result);
        assertEquals(100f, battleService.player1.getHp()); // Assuming the deduction logic is correct
        assertEquals(100f, battleService.player2.getHp()); // Assuming the deduction logic is correct

        // Verify that certain methods were called
        verify(card1, times(2)).getName();
        verify(card1, times(1)).getDamage();
        verify(card2, times(2)).getName();
    }

    @Test
    void modifySpellDamage_EffectiveElement() {
        // Mock the cards and their elements
        when(card1.getElementType()).thenReturn("Water");
        when(card2.getElementType()).thenReturn("Fire");

        // Arrange the battle service
        battleService.player1 = new Player("Alice");
        battleService.player2 = new Player("Bob");

        // Stub the behavior of player's HP and damage for cards
        when(card1.getElementType()).thenReturn("Water");
        when(card2.getElementType()).thenReturn("Fire");
        when(card1.getDamage()).thenReturn(10f);

        // Invoke the method under test
        float result = battleService.modifySpellDamage(card1.getElementType(), card2.getElementType(), card1.getDamage());

        // Assert the result
        assertEquals(20f, result); // Assuming effective elemental damage doubles the damage

        // Verify that certain methods were called
        verify(card1, times(1)).getElementType();
        verify(card1, times(1)).getDamage();
        verify(card2, times(1)).getElementType();
    }

    @Test
    void modifySpellDamage_NotEffectiveElement() {
        // Mock the cards and their elements
        when(card1.getElementType()).thenReturn("Fire");
        when(card2.getElementType()).thenReturn("Water");

        // Arrange the battle service
        battleService.player1 = new Player("Alice");
        battleService.player2 = new Player("Bob");

        // Stub the behavior of player's HP and damage for cards
        when(card1.getElementType()).thenReturn("Fire");
        when(card2.getElementType()).thenReturn("Water");
        when(card1.getDamage()).thenReturn(10f);

        // Invoke the method under test
        float result = battleService.modifySpellDamage(card1.getElementType(), card2.getElementType(), card1.getDamage());

        // Assert the result
        assertEquals(5f, result); // Assuming not effective elemental damage halves the damage

        // Verify that certain methods were called
        verify(card1, times(1)).getElementType();
        verify(card1, times(1)).getDamage();
        verify(card2, times(1)).getElementType();
    }

    @Test
    void modifySpellDamage_NoEffectiveness() {

        // Arrange the battle service
        battleService.player1 = new Player("Alice");
        battleService.player2 = new Player("Bob");


        // Stub the behavior of player's HP and damage for cards
        when(card1.getElementType()).thenReturn("Regular");
        when(card2.getElementType()).thenReturn("Regular");
        when(card1.getDamage()).thenReturn(10f);

        // Invoke the method under test
        float result = battleService.modifySpellDamage(card1.getElementType(), card2.getElementType(), card1.getDamage());

        // Assert the result
        assertEquals(10f, result); // Assuming no elemental effectiveness, damage remains the same

        // Verify that certain methods were called
        verify(card1, times(1)).getElementType();
        verify(card1, times(1)).getDamage();
        verify(card2, times(1)).getElementType();
    }

}
