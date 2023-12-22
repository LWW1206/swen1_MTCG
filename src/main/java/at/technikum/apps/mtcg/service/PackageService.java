package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.apps.mtcg.entity.Card;

import java.sql.SQLException;
import java.util.List;

public class PackageService {

    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;

    public PackageService(PackageRepository packageRepository, CardRepository cardRepository) {

        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
    }

    public void savePackage(List<Card> cards) throws SQLException {
        for(Card singleCard: cards) {
            cardRepository.saveCard(singleCard);
        }
        packageRepository.savePackage(cards);
    }

    public boolean packageAvailable() {
        return packageRepository.allPackagesBought();
    }

    public boolean buyPackage(String name) {
        List <String> boughtCardIds = packageRepository.buyPackage(name);
        for (String cardId : boughtCardIds) {
            cardRepository.updateOwner(cardId, name);
        }

        return true;
    }
}
