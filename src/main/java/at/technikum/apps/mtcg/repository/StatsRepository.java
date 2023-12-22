package at.technikum.apps.mtcg.repository;

import at.technikum.apps.database.databaseConnection;
import at.technikum.apps.mtcg.entity.UserData;
import at.technikum.apps.mtcg.entity.UserStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatsRepository {
    public UserStats getUserStats(String username) {
        String query = "SELECT username, games_played, games_won, games_lost, elo FROM stats WHERE username = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, username);

                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        String fetchedUsername = result.getString("username");
                        int gamesPlayed = result.getInt("games_played");
                        int gamesWon = result.getInt("games_won");
                        int gamesLost = result.getInt("games_lost");
                        int elo = result.getInt("elo");

                        return new UserStats(fetchedUsername, gamesPlayed, gamesWon, gamesLost, elo);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateUserStats(UserStats userStats) {
        String query = "UPDATE stats SET games_played = ?, games_won = ?, games_lost = ?, elo = ? WHERE username = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setInt(1, userStats.getGamesPlayed());
                ps.setInt(2, userStats.getGamesWon());
                ps.setInt(3, userStats.getGamesLost());
                ps.setInt(4, userStats.getElo());
                ps.setString(5, userStats.getUsername());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        public List<UserStats> retrieveScoreboard() {
            String query = "SELECT username, games_played, games_won, games_lost, elo FROM stats ORDER BY elo DESC";

            List<UserStats> scoreboard = new ArrayList<>();

            try (Connection connection = databaseConnection.getConnection()) {
                assert connection != null;
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    try (ResultSet result = ps.executeQuery()) {
                        while (result.next()) {
                            String username = result.getString("username");
                            int gamesPlayed = result.getInt("games_played");
                            int gamesWon = result.getInt("games_won");
                            int gamesLost = result.getInt("games_lost");
                            int elo = result.getInt("elo");

                            scoreboard.add(new UserStats(username, gamesPlayed, gamesWon, gamesLost, elo));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return scoreboard;
        }

    public void createUserStats(UserStats playerStats) {
        String query = "INSERT INTO stats (username, games_played, games_won, games_lost, elo) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, playerStats.getUsername());
                ps.setInt(2, playerStats.getGamesPlayed());
                ps.setInt(3, playerStats.getGamesWon());
                ps.setInt(4, playerStats.getGamesLost());
                ps.setInt(5, playerStats.getElo());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}


