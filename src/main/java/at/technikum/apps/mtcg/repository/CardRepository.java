package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import at.technikum.apps.mtcg.entity.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardRepository extends Repository {

    public void saveCard(Card Card) {
        String query = "INSERT INTO card (card_id, name, damage, monster_type, element_type) VALUES (?,?,?,?,?)";
        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, Card.getId());
                ps.setString(2, Card.getName());
                ps.setFloat(3, Card.getDamage());
                ps.setBoolean(4, Card.getMonsterType());
                ps.setString(5, Card.getElementType());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> getAllCardDataById(List<String> cardIds) {
        List<Card> cards = new ArrayList<>();
        String query = "SELECT * FROM card WHERE card_id IN (";

        // Creating placeholders for IDs in the IN clause
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < cardIds.size(); i++) {
            placeholders.append("?");
            if (i < cardIds.size() - 1) {
                placeholders.append(", ");
            }
        }
        query += placeholders + ")";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                // Setting values for the placeholders in the IN clause
                for (int i = 0; i < cardIds.size(); i++) {
                    ps.setString(i + 1, cardIds.get(i));
                }

                // Executing the query and retrieving results
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        String cardId = resultSet.getString("card_id");
                        String name = resultSet.getString("name");
                        float damage = resultSet.getFloat("damage");
                        boolean monsterType = resultSet.getBoolean("monster_type");
                        String elementType = resultSet.getString("element_type");

                        Card card = new Card(cardId, name, damage, monsterType, elementType);
                        cards.add(card);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public Card getCardById(String cardId) {
        String query = "SELECT * FROM card WHERE card_id = ?";
        Card card = null;

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, cardId);

                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        float damage = resultSet.getFloat("damage");
                        boolean monsterType = resultSet.getBoolean("monster_type");
                        String elementType = resultSet.getString("element_type");

                        card = new Card(cardId, name, damage, monsterType, elementType);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return card;
    }

    public void updateOwner(String cardId, String newOwner) {
        String query = "UPDATE card SET owner = ? WHERE card_id = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, newOwner);
                ps.setString(2, cardId);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getCardIdByUser(String userName) {
        List<String> cardIds = new ArrayList<>();
        String query = "SELECT card_id FROM card WHERE owner = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, userName);

                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        String cardId = resultSet.getString("card_id");
                        cardIds.add(cardId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cardIds;
    }
}
