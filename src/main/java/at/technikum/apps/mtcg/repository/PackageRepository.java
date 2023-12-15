package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import at.technikum.apps.mtcg.template.card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PackageRepository {

    public boolean savePackage(List<card> cards) {
        String query = "INSERT INTO packages (card1, card2, card3, card4, card5) values (?, ? , ?, ?, ?)";
        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                for (int cardsnum = 0; cardsnum < 5; cardsnum++) {
                    ps.setString(cardsnum + 1, cards.get(cardsnum).getId());
                }
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
