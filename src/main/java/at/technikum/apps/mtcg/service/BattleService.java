package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.entity.Card;
import at.technikum.apps.mtcg.entity.Player;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BattleService {
    private final UserService userService;
    private final DeckService deckService;
    private Player player1;
    private Player player2;
    private List<Card> deck1;
    private List<Card> deck2;
    private boolean winnerSet;
    private final Map<String, String> spellEffectiveness;
    private final Map<String, String> monsterSpecialties;

    public BattleService() {
        this.userService = new UserService();
        this.deckService = new DeckService();
        this.winnerSet = false;
        this.spellEffectiveness = initializeEffectivenessMap();
        this.monsterSpecialties = initializeMonsterSpecialtiesMap();
    }

    private Map<String, String> initializeEffectivenessMap() {
        Map<String, String> effectiveness = new HashMap<>();
        effectiveness.put("Water->Fire", "effective");
        effectiveness.put("Fire->Water", "not effective");
        effectiveness.put("Fire->Regular", "effective");
        effectiveness.put("Regular->Fire", "not effective");
        effectiveness.put("Regular->Water", "effective");
        effectiveness.put("Water->Regular", "not effective");
        return effectiveness;
    }

    private Map<String, String> initializeMonsterSpecialtiesMap() { //autowins
        Map<String, String> specialties = new HashMap<>();
        specialties.put("Dragon", "Goblin");
        specialties.put("Wizard", "Orc");
        specialties.put("WaterSpell", "Knight");
        specialties.put("Kraken", "Spell");
        specialties.put("FireElf", "Dragon");
        return specialties;
    }

    public void setPlayersAndDeck(String player_1, String player_2) {
        this.player1 = new Player(player_1);
        this.deck1 = deckService.getDeckAsList(player_1);
        this.player2 = new Player(player_2);
        this.deck2 = deckService.getDeckAsList(player_2);
    }

    public Response startBattle(Request firstRequest, Request secondRequest) {
        if (!userService.tokenExists(firstRequest) || !userService.tokenExists(secondRequest))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        setPlayersAndDeck(AuthorizationHelper.getNameFromToken(firstRequest), AuthorizationHelper.getNameFromToken(secondRequest));
        System.out.println(conductBattle());

        return ResponseHelper.generateResponse(HttpStatus.OK, "got 2 players now");
    }

    private String conductBattle() {
        StringBuilder battleLogger = new StringBuilder();

        int roundCnt = 0;
        do {
            battleLogger.append(addPlayerStatusToLog(roundCnt + 1));

            battleLogger.append(checkIfWin());
            if(winnerSet) break;

            Card card1 = getRandomCard(deck1);
            battleLogger.append("\nPlayer1 choose ").append(card1.getName()).append(" - Dmg: ").append(card1.getDamage());
            Card card2 = getRandomCard(deck2);
            battleLogger.append("\nPlayer2 choose ").append(card2.getName()).append(" - Dmg: ").append(card2.getDamage());

            battleLogger.append(fight(card1, card2));

            roundCnt++;
        } while(roundCnt != 100 && !winnerSet);

        return battleLogger.toString();
    }

    private String fight(Card card1, Card card2) {
        if(card1.getMonsterType() && card2.getMonsterType())
            return monsterVSMonsterBattle(card1, card2);
        else
            return atleastOneSpellcardBattle(card1, card2);
    }

    private String monsterVSMonsterBattle(Card card1, Card card2) {
        if(checkSpecialties(card1, card2) != null) // check specialities first
            return checkSpecialties(card1, card2);
        if (card1.getDamage() > card2.getDamage()) {
            player2.deductHp(card1.getDamage() - card2.getDamage());
            return moveCardBetweenDecks(card2, deck2, deck1, "\nPlayer 1 won this round, Card: " + card2.getName() + " gets moved to Player1's deck.\n");
        }
        else if (card1.getDamage() < card2.getDamage()) {
            player1.deductHp(card2.getDamage() - card1.getDamage());
            return moveCardBetweenDecks(card1, deck1, deck2, "\nPlayer 2 won this round, Card: "+ card1.getName() + " gets moved to Player2's deck.\n");
        }
        else // card1.dmg == card2.dmg
            return "The 2 monsters dealt the same damage, it is a draw\n";
    }

    private String atleastOneSpellcardBattle(Card card1, Card card2) {
        if(checkSpecialties(card1, card2) != null) // check specialities first
            return checkSpecialties(card1, card2);

        StringBuilder Logger = new StringBuilder();

        // Determine the effectiveness of the spells
        float modifiedDamage1 = modifySpellDamage(card1.getElementType(), card2.getElementType(), card1.getDamage());
        Logger.append("\n").append(card1.getName()).append(" modified DMG: ").append(modifiedDamage1);
        float modifiedDamage2 = modifySpellDamage(card2.getElementType(), card1.getElementType(), card2.getDamage());
        Logger.append("\n").append(card2.getName()).append(" modified DMG: ").append(modifiedDamage2);

        if (modifiedDamage1 > modifiedDamage2) {
            player2.deductHp(modifiedDamage1 - modifiedDamage2);
            Logger.append(moveCardBetweenDecks(card2, deck2, deck1, "\nPlayer 1's Card wins this round\n" + card2.getName() + " gets moved to Player 1's deck\n"));
            return Logger.toString();
        } else if (modifiedDamage1 < modifiedDamage2) {
            player1.deductHp(modifiedDamage2 - modifiedDamage1);
            Logger.append(moveCardBetweenDecks(card1, deck1, deck2, "\nPlayer 2's Card wins this round\n" + card1.getName() + " gets moved to Player 2's deck\n"));
            return Logger.toString();
        } else {
            return "\nThe Cards dealt same damage, it is a draw\n";
        }
    }
    private String moveCardBetweenDecks(Card card, List<Card> sourceDeck, List<Card> destinationDeck, String winMessage) {
        sourceDeck.remove(card);
        destinationDeck.add(card);
        return winMessage;
    }

    private float modifySpellDamage(String elementType1, String elementType2, float damage) {
        String key = elementType1 + "->" + elementType2;

        if (spellEffectiveness.containsKey(key)) {
            String effect = spellEffectiveness.get(key);
            if (effect.equals("effective")) {
                return damage * 2;
            } else if (effect.equals("not effective")) {
                return damage / 2;
            }
        }
        return damage;
    }

    private String checkSpecialties(Card card1, Card card2) {
        String name1 = card1.getName();
        String name2 = card2.getName();

        for (Map.Entry<String, String> entry : monsterSpecialties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (name1.contains(key) && name2.contains(value)) { // Handle autolose scenario
                player2.deductHp(card1.getDamage());
                return moveCardBetweenDecks(card1, deck1, deck2, "\n" + name1 + " auto-loses to " + name2);
            } else if (name2.contains(key) && name1.contains(value)) {
                player1.deductHp(card2.getDamage());
                return moveCardBetweenDecks(card2, deck2, deck1,  "\n"+ name2 + " auto-loses to " + name1);
            }
        }
        return null;
    }

    public String checkIfWin() {
        if(deck1.isEmpty() || player1.isDead()) {
            winnerSet = true;
            return "Player 2 has won!\n";
        }
        if(deck2.isEmpty() || player2.isDead()){
            winnerSet = true;
            return "Player 1 has won!\n";
        }
        return "";
    }

    private Card getRandomCard(List<Card> deck) {
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        return deck.get(randomIndex);
    }
    private String addPlayerStatusToLog(int roundCnt) {
        StringBuilder statusLog = new StringBuilder();
        statusLog.append("\033[1m\nRound: ").append(roundCnt).append("\033[0m\n");

        // Append Player 1 status
        statusLog.append("Player 1 ").append(player1.getName()).append(" Deck: [");
        for (Card card : deck1) {
            statusLog.append(card.getName()).append(" ");
        }
        statusLog.append("] | HP: ").append(player1.getHp()).append("\n");

        // Append Player 2 status
        statusLog.append("Player 2 ").append(player2.getName()).append(" Deck: ["); // <-- Corrected line here
        for (Card card : deck2) {
            statusLog.append(card.getName()).append(" ");
        }
        statusLog.append("] | HP: ").append(player2.getHp()).append("\n");

        return statusLog.toString();
    }

}
