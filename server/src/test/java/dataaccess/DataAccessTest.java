package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.AuthData;
import model.GameData;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import server.IDAOsProvider;
import server.MemoryDAOsProvider;
import server.MySqlDAOsProvider;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    private IDAOsProvider getDataAccess(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException {
        IDAOsProvider db;
        if (databaseClass.equals(MySqlDAOsProvider.class)) {
            db = new MySqlDAOsProvider();
        } else {
            db = new MemoryDAOsProvider();
        }
        return db;
    }
    private UserDAO getUserDAO(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException{
        IDAOsProvider provider=getDataAccess(databaseClass);
        return provider.getUserDAO();
    }
    private AuthDAO getAuthDAO(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException{
        IDAOsProvider provider=getDataAccess(databaseClass);
        return provider.getAuthDAO();
    }
    private GameDAO getGameDAO(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException{
        IDAOsProvider provider=getDataAccess(databaseClass);
        return provider.getGameDAO();
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void testUserDAO(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(dbClass);
        userDAO.clear();

        UserData user = new UserData("user1", "pass", "email");

        // Positive: create user
        userDAO.createUser(user);

        // Negative: duplicate user
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user));

        // Positive: get user
        UserData fetched = userDAO.getUser("user1");
        Assertions.assertEquals("user1", fetched.username());

        // Negative: get nonexistent user
        Assertions.assertNull(userDAO.getUser("ghost"));

        // Positive: get users
        Assertions.assertEquals(1, userDAO.getUsers().size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void testAuthDAO(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(dbClass);
        authDAO.clear();

        AuthData auth = new AuthData("token1", "user1");

        // Positive: create auth
        authDAO.createAuth(auth);

        // Negative: duplicate token
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));

        // Positive: get auth
        AuthData fetched = authDAO.getAuth("token1");
        Assertions.assertEquals("user1", fetched.username());

        // Negative: bad token
        Assertions.assertNull(authDAO.getAuth("bad_token"));

        // Positive: delete auth
        authDAO.deleteAuth(auth);

        // Negative: delete nonexistent
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(auth));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDAOsProvider.class, MySqlDAOsProvider.class})
    void testGameDAO(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();

        // Positive: create game
        int id = gameDAO.createGame("game1").gameID();

        // Negative: create null game
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));

        // Positive: get game
        GameData game = gameDAO.getGame(id);
        Assertions.assertEquals("game1", game.gameName());

        // Negative: bad ID
        Assertions.assertNull(gameDAO.getGame(-1));

        // Positive: joinGame
        gameDAO.joinGame("WHITE", game, "user1");

        // Negative: invalid color
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.joinGame("PURPLE", game, "user2"));

        // Negative: slot already taken
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.joinGame("WHITE", game, "user3"));

        // Positive: updateGame
        ChessGame updatedGame = new ChessGame();
        try {
            updatedGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        gameDAO.updateGame(id, updatedGame);

        // Negative: update with bad ID
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(-1, updatedGame));

        // Positive: list games
        List<GameData> games = gameDAO.listGames();
        Assertions.assertEquals(1, games.size());
    }
}


