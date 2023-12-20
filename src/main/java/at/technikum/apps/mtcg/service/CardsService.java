package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.entity.Card;

import java.util.List;

public class CardsService {
    private final CardRepository cardRepository;

    public CardsService() {
        this.cardRepository = new CardRepository();
    }
    public List <Card> getCardData(List<String> cardsIds) {
        return cardRepository.getDatabyId(cardsIds);
    }


}
