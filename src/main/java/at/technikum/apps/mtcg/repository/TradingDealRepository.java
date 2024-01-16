package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import at.technikum.apps.mtcg.entity.TradingDeal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TradingDealRepository {
    public void createDeal(String cardToTrade, String dealId, String type, Float minimumDamage, String creator) throws SQLException {
        String query = "INSERT INTO tradingdeal (tradeid, cardid, type, minDmg, creator) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, dealId);
                ps.setString(2, cardToTrade);
                ps.setString(3, type);
                ps.setFloat(4, minimumDamage);
                ps.setString(5, creator);

                ps.executeUpdate();
            }
        }
    }

    public List<TradingDeal> getAvailableDeals() {
        List<TradingDeal> availableDeals = new ArrayList<>();
        String query = "SELECT * FROM tradingdeal";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String tradeId = rs.getString("tradeid");
                        String cardId = rs.getString("cardid");
                        String type = rs.getString("type");
                        Float minDamage = rs.getFloat("minDmg");
                        String creator = rs.getString("creator");

                        TradingDeal deal = new TradingDeal(tradeId, cardId, type, minDamage, creator);
                        availableDeals.add(deal);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return availableDeals;
    }

    public void deleteDeal(String dealIdtoBeDeleted) throws SQLException{
        String query = "DELETE FROM tradingdeal WHERE tradeid = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, dealIdtoBeDeleted);
                ps.executeUpdate();
            }
        }
    }
    public boolean doesCardFitDeal(String dealID, boolean monsterType, Float damage) {
        String query = "SELECT tradeid FROM tradingdeal WHERE tradeid = ? AND type = ? AND minDmg <= ? LIMIT 1";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, dealID);
                ps.setString(2, monsterType ? "monster" : "spell");
                ps.setFloat(3, damage);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // If a matching dealID is found, it means the conditions fit
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }



    public String getTradeCreator(String dealID) {
        String query = "SELECT creator FROM tradingdeal WHERE tradeid = ?";
        String creator = null;

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, dealID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        creator = rs.getString("creator");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return creator;
    }

    public String getCardIdFromDeal(String dealID) {
        String query = "SELECT cardid FROM tradingdeal WHERE tradeid = ?";
        String cardId = null;

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, dealID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cardId = rs.getString("cardid");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cardId;
    }


    public String getUserNameFromDeal(String dealID) {
        String query = "SELECT creator FROM tradingdeal WHERE tradeid = ?";
        String creator = null;

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, dealID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        creator = rs.getString("creator");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return creator;
    }
}
