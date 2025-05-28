package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    void clear() throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void addGame(GameData data) throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    void joinGame(String color, GameData game, String username) throws DataAccessException;

    void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException;

    void setGame(int id, GameData game);

    GameData createGame(String gameName) throws DataAccessException;
}
