package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.Player;
import at.technikum.apps.mtcg.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BattleServiceTest {

    @Mock
    private Card card1;

    @Mock
    private Card card2;

    @InjectMocks
    private BattleService battleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        assertEquals(95f, battleService.player2.getHp()); // Assuming the deduction logic is correct

        // Verify that certain methods were called
        verify(card1, times(1)).getName();
        verify(card1, times(1)).getDamage();
        verify(card2, times(1)).getName();
        verify(card2, times(1)).getDamage();
    }
    @Test
    void atleastOneSpellcardBattle_ModifiedDamage_Player1Wins() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Mocking cards for the test
        Card spellCard = mock(Card.class);
        Card monsterCard = mock(Card.class);

        // Setting up behavior for the cards
        when(spellCard.getName()).thenReturn("WaterSpell");
        when(spellCard.getElementType()).thenReturn("Water");
        when(spellCard.getDamage()).thenReturn(10f);

        when(monsterCard.getName()).thenReturn("Dragon");
        when(monsterCard.getElementType()).thenReturn("Fire");
        when(monsterCard.getDamage()).thenReturn(15f);

        // Mocking the players
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");

        // Creating decks for players
        List<Card> deck1 = new ArrayList<>();
        deck1.add(spellCard);

        List<Card> deck2 = new ArrayList<>();
        deck2.add(monsterCard);

        // Setting up the battle service
        BattleService battleService = new BattleService();
        battleService.player1 = player1;
        battleService.player2 = player2;
        battleService.deck1 = deck1;
        battleService.deck2 = deck2;

        // Accessing the private method using reflection
        Method privateMethod = BattleService.class.getDeclaredMethod("atleastOneSpellcardBattle", Card.class, Card.class);
        privateMethod.setAccessible(true);

        // Executing the private method and getting the result
        String result = (String) privateMethod.invoke(battleService, spellCard, monsterCard);

        // Assertions
        assertEquals("\nPlayer 1's Card wins this round\nWaterSpell gets moved to Player 1's deck\n", result);
        assertEquals(100f, battleService.player1.getHp()); // Update with expected player1 HP after the battle
        assertEquals(90f, battleService.player2.getHp()); // Update with expected player2 HP after the battle

    }
    // Add more tests for other scenarios and methods as needed
}
