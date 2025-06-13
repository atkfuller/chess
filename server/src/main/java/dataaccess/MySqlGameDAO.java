package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlGameDAO implements GameDAO{
    public MySqlGameDAO() throws DataAccessException{
        configureDatabase();
    }
    @Override
    public void clear() throws DataAccessException {
        var statement="TRUNCATE TABLE games";
        executeUpdate(statement);
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                ArrayList<GameData> returnList= new ArrayList<GameData>();
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        returnList.add(readGame(rs));
                    }
                }
                return returnList;
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to list games: %s", e.getMessage()));
        }
    }

    @Override
    public void addGame(GameData data) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
            var json = new Gson().toJson(data.game());
            executeUpdate(statement, data.whiteUsername(), data.blackUsername(),data.gameName(), json);

        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to add game: %s", e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int id) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to find game: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void joinGame(String color, GameData game, String username) throws DataAccessException {
            try (var conn = DatabaseManager.getConnection()) {
                String query = "SELECT whiteUsername, blackUsername FROM games WHERE gameID = ?";
                try (var ps = conn.prepareStatement(query)) {
                    ps.setInt(1, game.gameID());
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String white = rs.getString("whiteUsername");
                            String black = rs.getString("blackUsername");
                            alreadyAssignedCheck(color, username, white, black);
                        } else {
                            throw new DataAccessException(404, "Error : Game not found.");
                        }
                    }
                }

                String statement = "BLACK".equalsIgnoreCase(color)
                        ? "UPDATE games SET blackUsername=? WHERE gameID=?"
                        : "UPDATE games SET whiteUsername=? WHERE gameID=?";
                executeUpdate(statement, username, game.gameID());

            } catch (DataAccessException e) {
                throw e;
            } catch (Exception e) {
                throw new DataAccessException(500, String.format("Error: Unable to join game: %s", e.getMessage()));
            }
        }

    private static void alreadyAssignedCheck(String color, String username, String white, String black) throws DataAccessException {
        if ("WHITE".equalsIgnoreCase(color) && white != null && username !=null) {
            throw new DataAccessException(403, "Error: White player already assigned.");
        } else if ("BLACK".equalsIgnoreCase(color) && black != null && username !=null) {
            throw new DataAccessException(403, "Error: Black player already assigned.");
        }
    }


    @Override
    public void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET game=? WHERE gameID=?";
            var json = new Gson().toJson(updatedGame);

            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, json);
                ps.setInt(2, gameID);

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new DataAccessException(404, "Error: Unable to find game: %s");
                }
            }

        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to update game: %s", e.getMessage()));
        }
    }

    @Override
    public void setGame(int id, GameData game) {

    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games ( gameName, game) VALUES (?,?)";
            var chessGame=new ChessGame();
            var json = new Gson().toJson(chessGame);
            var gameID=executeUpdate(statement, gameName, json);
            return new GameData(gameID, null, null, gameName,chessGame);

        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Error: Unable to create game: %s", e.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessException {
        ConfigureDatabase.configureDatabase(createStatements);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
       return ConfigureDatabase.executeUpdate(statement, params);
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsernmae = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("game");
        var game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsernmae, gameName, game);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              gameID int NOT NULL AUTO_INCREMENT,
              whiteUsername varchar(256),
              blackUsername varchar(256),
              gameName varchar(256) NOT NULL,
              game TEXT NOT NULL,
              PRIMARY KEY(gameID)
            )
            """
    };

}
