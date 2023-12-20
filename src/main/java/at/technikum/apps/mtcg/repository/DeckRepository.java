package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeckRepository {

    private static final Logger logger = Logger.getLogger(DeckRepository.class.getName());
    public List<String> getDeckFromUser(String name) {
        String query = "SELECT card1id, card2id, card3id, card4id FROM deck WHERE username = ?";
        List<String> deckCardIds = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Retrieve card IDs and add them to the deckCardIds list
                        deckCardIds.add(rs.getString("card1id"));
                        deckCardIds.add(rs.getString("card2id"));
                        deckCardIds.add(rs.getString("card3id"));
                        deckCardIds.add(rs.getString("card4id"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching deck for user: " + name, e);
        }
        return deckCardIds;
    }

    public void createDeck(List<String> cardIds, String name) {
        String query = "INSERT INTO deck (username, card1id, card2id, card3id, card4id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, name);
                for (int i = 0; i < 4; i++) {
                    ps.setString(i + 2, cardIds.get(i)); // Set card IDs in parameters (starting from index 2)
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating deck for user: " + name, e);
        }
    }
}
