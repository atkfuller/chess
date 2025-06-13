package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthDAO implements AuthDAO{
    public MySqlAuthDAO() throws DataAccessException{
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException{
        var statement="TRUNCATE TABLE authentication";
        executeUpdate(statement);
    }

    @Override
    public void createAuth(AuthData autho) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO authentication (authToken, username) VALUES (?, ?)";
            executeUpdate(statement, autho.authToken(), autho.username());
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to create authentication: %s", e.getMessage()));
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authentication WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to find authentication: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authentication WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(2, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to find authentication: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData aData) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authentication WHERE authToken=?";
            executeUpdate(statement, aData.authToken());
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to delete authentication: %s", e.getMessage()));
        }
    }

    @Override
    public ArrayList<AuthData> getAuthencation() {
        return null;
    }

    private void configureDatabase() throws DataAccessException {
        ConfigureDatabase.configureDatabase(createStatements);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        return ConfigureDatabase.executeUpdate(statement, params);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken,username);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authentication (
              authToken varchar(256) NOT NULL,
              username varchar(256) NOT NULL,
              PRIMARY KEY(authToken)
            ) 
            """
    };
}
