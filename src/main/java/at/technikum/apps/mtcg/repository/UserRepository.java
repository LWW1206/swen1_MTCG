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
        String query = "INSERT INTO usertable (username, password) VALUES (?, ?)";

        try (Connection connection = databaseConnection.getConnection()) {
            assert connection != null;
            try (PreparedStatement ps = connection.prepareStatement(query)) {

                    ps.setString(1, username);
                    ps.setString(2,password);

                int rowsAffected = ps.executeUpdate(); //execute the query and get the number of affected rows
                return rowsAffected > 0; //return true if atleast one row was affected

                }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
