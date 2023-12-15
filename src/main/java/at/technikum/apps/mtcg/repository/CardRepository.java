package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import at.technikum.apps.mtcg.template.card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CardRepository extends Repository {

    public void saveCard(card Card) throws SQLException {
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
}
