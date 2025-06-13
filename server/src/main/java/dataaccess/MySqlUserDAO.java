package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO{
    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException{
        var statement="TRUNCATE TABLE users";
        executeUpdate(statement);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to find user: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            System.out.printf("Creating user: %s, %s, %s%n", user.username(), user.hashedPasword(), user.email());
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            executeUpdate(statement,user.username(), user.hashedPasword(), user.email());

        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to create user: %s", e.getMessage()));
        }
    }
    @Override
    public ArrayList<UserData> getUsers() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users";
            try (var ps = conn.prepareStatement(statement)) {
                ArrayList<UserData> returnList= new ArrayList<UserData>();
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        returnList.add(readUser(rs));
                    }
                }
                return returnList;
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to get user: %s", e.getMessage()));
        }
    }
    private void configureDatabase() throws DataAccessException {
        ConfigureDatabase.configureDatabase(createStatements);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        return ConfigureDatabase.executeUpdate(statement, params);
    }
    private UserData readUser(ResultSet rs) throws SQLException {
        var username= rs.getString("username");
        var hashedPassword= rs.getString("password");
        var email= rs.getString("email");
        UserData newUser=new UserData(username,hashedPassword,email);
        return newUser;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              username varchar(256) NOT NULL,
              password varchar(256) NOT NULL,
              email varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (username)
            ) 
            """
    };

}
