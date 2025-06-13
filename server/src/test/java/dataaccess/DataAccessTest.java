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
import org.junit.jupiter.params.provider.ValueSource;
import server.IDAOsProvider;
import server.MemoryDAOsProvider;
import server.MySqlDAOsProvider;

import java.util.List;

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
    void negCreateserDAO(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(dbClass);
        userDAO.clear();
        UserData user = new UserData("user1", "pass", "email");
        userDAO.createUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posGetUser(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(dbClass);
        userDAO.clear();
        UserData user = new UserData("user1", "pass", "email");
        userDAO.createUser(user);
        UserData fetched = userDAO.getUser("user1");
        Assertions.assertEquals("user1", fetched.username());
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negGetUser(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(dbClass);
        userDAO.clear();
        UserData user = new UserData("user1", "pass", "email");
        userDAO.createUser(user);
        Assertions.assertNull(userDAO.getUser("ghost"));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posGetUsers(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(dbClass);
        userDAO.clear();
        UserData user = new UserData("user1", "pass", "email");
        userDAO.createUser(user);
        Assertions.assertEquals(1, userDAO.getUsers().size());
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posCreateUser(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        UserDAO userDAO = getUserDAO(dbClass);
        userDAO.clear();

        UserData user = new UserData("user1", "pass", "email");

        userDAO.createUser(user);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negDuplicateToken(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(dbClass);
        authDAO.clear();
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posGetAuth(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(dbClass);
        authDAO.clear();
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        AuthData fetched = authDAO.getAuth("token1");
        Assertions.assertEquals("user1", fetched.username());
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negBadToken(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(dbClass);
        authDAO.clear();
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        Assertions.assertNull(authDAO.getAuth("bad_token"));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posDeleteAuth(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(dbClass);
        authDAO.clear();
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        authDAO.deleteAuth(auth);
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negDeleteAuth(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(dbClass);
        authDAO.clear();
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(auth));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void testCreateAuth(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO authDAO = getAuthDAO(dbClass);
        authDAO.clear();
        AuthData auth = new AuthData("token1", "user1");
        authDAO.createAuth(auth);
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negCreateGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posGetGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        GameData game = gameDAO.getGame(id);
        Assertions.assertEquals("game1", game.gameName());
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negGetGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        Assertions.assertNull(gameDAO.getGame(-1));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posJoinGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        GameData game = gameDAO.getGame(id);
        gameDAO.joinGame("WHITE", game, "user1");
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negJoinGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        GameData game = gameDAO.getGame(id);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.joinGame("WHITE", null, "user3"));

    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posUpdateGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        ChessGame updatedGame = new ChessGame();
        try {
            updatedGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        gameDAO.updateGame(id, updatedGame);
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void negUpdateGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        ChessGame updatedGame = new ChessGame();
        try {
            updatedGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(-1, updatedGame));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class})
    void posListGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();
        List<GameData> games = gameDAO.listGames();
        Assertions.assertEquals(1, games.size());
    }
    @ParameterizedTest
    @ValueSource(classes = { MySqlDAOsProvider.class})
    void testCreateGame(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO gameDAO = getGameDAO(dbClass);
        gameDAO.clear();
        int id = gameDAO.createGame("game1").gameID();

    }
}


