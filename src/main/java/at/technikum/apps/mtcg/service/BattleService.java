package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.entity.Card;
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
    private List<Card> deck_1;
    private List<Card> deck_2;
    private final StringBuilder battleLogger;
    private boolean winnerSet;
    private final Map<String, String> spellEffectiveness;
    private final Map<String, String> monsterSpecialties;

    public BattleService() {
        this.userService = new UserService();
        this.deckService = new DeckService();
        this.battleLogger = new StringBuilder();
        this.winnerSet = false;
        this.spellEffectiveness = initializeEffectivenessMap();
        this.monsterSpecialties = initializeMonsterSpecialtiesMap();
    }

    private Map<String, String> initializeEffectivenessMap() {
        Map<String, String> effectiveness = new HashMap<>();
        effectiveness.put("water->fire", "effective");
        effectiveness.put("fire->water", "not effective");
        effectiveness.put("fire->normal", "effective");
        effectiveness.put("normal->fire", "not effective");
        effectiveness.put("normal->water", "effective");
        effectiveness.put("water->normal", "not effective");
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

    public Response startBattle(Request firstRequest, Request secondRequest) {
        if (!userService.tokenExists(firstRequest) || !userService.tokenExists(secondRequest))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        String player_1 = AuthorizationHelper.getNameFromToken(firstRequest);
        String player_2 = AuthorizationHelper.getNameFromToken(secondRequest);

        this.deck_1 = deckService.getDeckAsList(player_1);
        this.deck_2 = deckService.getDeckAsList(player_2);

        String battleLog = conductBattle();

        return ResponseHelper.generateResponse(HttpStatus.OK, "got 2 players now");
    }

    private String conductBattle() {

        int roundCnt = 0;
        do {
            checkIfWin();
            if(winnerSet) break;

            Card card1 = getRandomCard(deck_1);
            Card card2 = getRandomCard(deck_2);

            fight(card1, card2);

            roundCnt++;
        } while(roundCnt != 100 && !winnerSet);
        System.out.println(getBattleLog());
        return null;
    }

    private void fight(Card card1, Card card2) {
        if(card1.getMonsterType() && card2.getMonsterType())
            monsterVSMonster(card1, card2);
        else
            atleastOneSpellcardBattle(card1, card2);
    }

    private void monsterVSMonster(Card card1, Card card2) {
        if(checkSpecialties(card1, card2)) // check specialities first
            return;
        if (card1.getDamage() > card2.getDamage()) {
            deck_1.add(card2);
            deck_2.remove(card2);
            battleLogger.append("Player 1 won this round, Card: ").append(card2.getName()).append( " gets moved to Player 1's deck\n");
        }
        else if (card1.getDamage() < card2.getDamage()) {
            deck_1.add(card2);
            deck_2.remove(card2);
            battleLogger.append("Player 2 won this round, Card: ").append(card1.getName()).append( " gets moved to Player 2's deck\n");
        }
        else // card1.dmg == card2.dmg
            battleLogger.append("The 2 monsters dealt the same damage, it is a draw\n");
    }
    private void atleastOneSpellcardBattle(Card card1, Card card2) {
        if(checkSpecialties(card1, card2)) //check specialities first
            return;
        // Determine the effectiveness of the spells
        float modifiedDamage1 = modifySpellDamage(card1.getElementType(), card2.getElementType(), card1.getDamage());
        float modifiedDamage2 = modifySpellDamage(card2.getElementType(), card1.getElementType(), card2.getDamage());

        if (modifiedDamage1 > modifiedDamage2) {
            deck_1.add(card2);
            deck_2.remove(card2);
            battleLogger.append("Player 1's spell wins this round\n").append(card2.getName()).append( " gets moved to Player 1's deck\n");
        } else if (modifiedDamage1 < modifiedDamage2) {
            deck_1.remove(card1);
            deck_2.add(card1);
            battleLogger.append("Player 2's spell wins this round\n").append(card1.getName()).append( " gets moved to Player 2's deck\n");
        } else {
            battleLogger.append("The spells had dealt damage, it is a draw\n");
        }
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

    private boolean checkSpecialties(Card card1, Card card2) {
        String name1 = card1.getName();
        String name2 = card2.getName();

        for (Map.Entry<String, String> entry : monsterSpecialties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (name1.contains(key) && name2.contains(value)) {
                // Handle autolose scenario
                battleLogger.append(name1).append(" auto-loses to ").append(name2).append("\n");
                deck_1.remove(card1);
                return true;
            } else if (name2.contains(key) && name1.contains(value)) {
                battleLogger.append(name2).append(" auto-loses to ").append(name1).append("\n");
                deck_2.remove(card2);
                return true;
            }
        }
        return false;
    }

    void checkIfWin() {
        if(deck_1.isEmpty()) {
            battleLogger.append("Player 2 has won!\n");
            winnerSet = true;
        }
        if(deck_2.isEmpty()){
            battleLogger.append("Player 1 has won!\n");
            winnerSet = true;
        }

    }

    private Card getRandomCard(List<Card> deck) {
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        return deck.get(randomIndex);
    }

    public String getBattleLog() {
        return battleLogger.toString();
    }

}
