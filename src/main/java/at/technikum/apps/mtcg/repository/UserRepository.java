package at.technikum.apps.mtcg.repository;
import at.technikum.apps.database.databaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class UserRepository extends Repository {
    public String doesUserExist(String username) {
        String query = "SELECT username FROM usertable WHERE username = ?";

        try (Connection connection = databaseConnection.getConnection()) {

            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, username);

                try (ResultSet result = ps.executeQuery()) {
                    if (result.next())
                        return result.getString("username");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public boolean createUser(String username, String password) {
        if (doesUserExist(username) != null) {
            // User already exists, handle this situation as needed
            return false; // For example, return false to indicate failure
        }
        String query = "INSERT INTO usertable (username, password) VALUES (?, ?)";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, username);
                ps.setString(2, password);

                int rowsAffected = ps.executeUpdate(); //execute the query and get the number of affected rows
                return rowsAffected > 0; //return true if atleast one row was affected
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String generateSessionToken(String username) {
        return username + "-mtcgToken";
    }

    public boolean loginUser(String username, String password) {
        if(authentication(username, password)) {
            addToken(username, generateSessionToken(username));
            return true;
        }
        return false;
    }

    public void addToken(String username, String token) {
        String updateQuery = "UPDATE usertable SET token = ? WHERE username = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
                ps.setString(1, token);
                ps.setString(2, username);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authentication(String username, String password) {
        String query = "SELECT * FROM usertable WHERE username = ? AND password = ?";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet result = ps.executeQuery()) {
                    return result.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkToken(String username) {
        String query = "SELECT token FROM usertable WHERE username = ?";
        String expectedToken = username + "-mtcgToken";
        //System.out.println("expectedToken: " + expectedToken);

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, username);

                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        String storedToken = result.getString("token");
                        //System.out.println("storedToken: " + storedToken);
                        return expectedToken.equals(storedToken);
                    }
                    return false; // No token found for the user
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean atleastFiveCoins(String username) {
        String query = "SELECT coins FROM usertable WHERE username = ?";
        int minimumCoins = 5; //Minimum coins

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, username);

            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    int userCoins = result.getInt("coins");
                    return userCoins >= minimumCoins;
                }
                return false; //No user found
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean minusFiveCoins(String name) {
        String updateQuery = "UPDATE usertable SET coins = coins - 5 WHERE username = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(updateQuery)) {

            ps.setString(1, name);
            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0; // Returns true if coins were updated
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
