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
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void addGame(GameData data) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
            var json = new Gson().toJson(data.game());
            executeUpdate(statement, data.whiteUsername(), data.blackUsername(),data.gameName(), json);

        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
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
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void joinGame(String color, GameData game, String username) throws DataAccessException {
            try (var conn = DatabaseManager.getConnection()) {
                // First, check if the color is already taken
                String query = "SELECT whiteUsername, blackUsername FROM games WHERE gameID = ?";
                try (var ps = conn.prepareStatement(query)) {
                    ps.setInt(1, game.gameID());
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String white = rs.getString("whiteUsername");
                            String black = rs.getString("blackUsername");

                            if ("WHITE".equalsIgnoreCase(color) && white != null) {
                                throw new DataAccessException(403, "Error: White player already assigned.");
                            } else if ("BLACK".equalsIgnoreCase(color) && black != null) {
                                throw new DataAccessException(403, "Error: Black player already assigned.");
                            }
                        } else {
                            throw new DataAccessException(404, "Error : Game not found.");
                        }
                    }
                }

                // If the color is not taken, proceed with the update
                String statement = "BLACK".equalsIgnoreCase(color)
                        ? "UPDATE games SET blackUsername=? WHERE gameID=?"
                        : "UPDATE games SET whiteUsername=? WHERE gameID=?";
                executeUpdate(statement, username, game.gameID());

            } catch (DataAccessException e) {
                throw e; // rethrow known DataAccessExceptions
            } catch (Exception e) {
                throw new DataAccessException(500, String.format("Unable to join game: %s", e.getMessage()));
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
                    throw new DataAccessException(404, "No game found with the given ID to update.");
                }
            }

        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to update game: %s", e.getMessage()));
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
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
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
