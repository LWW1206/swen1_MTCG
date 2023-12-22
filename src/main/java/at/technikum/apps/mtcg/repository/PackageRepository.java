package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import at.technikum.apps.mtcg.entity.Card;

import java.sql.*;
import java.util.ArrayList;
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
        String query = "SELECT COUNT(*) FROM packages WHERE boughtBy IS NULL";

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

    public List<String> buyPackage(String user) {
        int packageId = updatePackageBoughtBy(user);
        if (packageId != -1) {
            return getBoughtCardIds(packageId);
        }
        return new ArrayList<>();
    }

    private int updatePackageBoughtBy(String user) {
        String updateQuery = "UPDATE packages SET boughtBy = ? WHERE id = (SELECT id FROM packages WHERE boughtBy IS NULL ORDER BY id LIMIT 1)";
        int packageId = -1;

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(updateQuery, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, user);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            packageId = generatedKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packageId;
    }

    private List<String> getBoughtCardIds(int packageId) {
        List<String> boughtCardIds = new ArrayList<>();
        String query = "SELECT card1, card2, card3, card4, card5 FROM packages WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement selectStatement = connection.prepareStatement(query)) {

                selectStatement.setInt(1, packageId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        boughtCardIds.add(resultSet.getString("card1"));
                        boughtCardIds.add(resultSet.getString("card2"));
                        boughtCardIds.add(resultSet.getString("card3"));
                        boughtCardIds.add(resultSet.getString("card4"));
                        boughtCardIds.add(resultSet.getString("card5"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boughtCardIds;
    }


}
