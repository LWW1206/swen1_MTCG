package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import at.technikum.apps.mtcg.template.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PackageRepository {

    public void savePackage(List<Card> cards) {
        String query = "INSERT INTO packages (card1, card2, card3, card4, card5) values (?, ? , ?, ?, ?)";
        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                for (int cardsnum = 0; cardsnum < 5; cardsnum++) {
                    ps.setString(cardsnum + 1, cards.get(cardsnum).getId());
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean allPackagesBought() {
        String query = "SELECT COUNT(*) FROM packages WHERE available = true";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        int availablePackages = result.getInt(1);
                        return availablePackages != 0; // If no available packages, all are bought
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean buyPackage() {
        String updateQuery = "UPDATE packages SET available = false WHERE id = (SELECT id FROM packages WHERE available = true ORDER BY id LIMIT 1)";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
