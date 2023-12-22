package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.entity.Card;

import java.util.List;

public class CardsService {
    private final CardRepository cardRepository;

    public CardsService() {
        this.cardRepository = new CardRepository();
    }
    public List <Card> getAllCardData(List<String> cardsIds) {
        return cardRepository.getAllCardDataById(cardsIds);
    }

    public Card getCardData(String cardId) {
        return cardRepository.getCardById(cardId);
    }

    public List <String> getUsersCards(String userName) {
        return cardRepository.getCardIdByUser(userName);
    }


}
