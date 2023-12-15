package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.repository.CardRepository;
import at.technikum.apps.mtcg.repository.PackageRepository;
import at.technikum.apps.mtcg.template.card;

import java.sql.SQLException;
import java.util.List;

public class PackageService {

    private final PackageRepository packageRepository;
    private final CardRepository cardRepository;

    public PackageService(PackageRepository packageRepository, CardRepository cardRepository) {

        this.packageRepository = packageRepository;
        this.cardRepository = cardRepository;
    }

    public void savePackage(List<card> cards) throws SQLException {
        for(card singleCard: cards) {
            cardRepository.saveCard(singleCard);
        }
        packageRepository.savePackage(cards);
    }

}
