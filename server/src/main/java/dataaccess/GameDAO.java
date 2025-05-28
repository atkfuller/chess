package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    static int generateUniqueID() {
        int id;
        do {
            id = MemoryGameDAO.RAND.nextInt(9000);
        } while (MemoryGameDAO.gameIDs.contains(id));
        MemoryGameDAO.gameIDs.add(id);
        return id;
    }

    void clear();

    ArrayList<GameData> listGames();

    void addGame(GameData data);

    GameData getGame(int id);

    void joinGame(String color, GameData game, String username) throws DataAccessException;

    void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException;

    void setGame(int id, GameData game);

    GameData createGame(String gameName);
}
