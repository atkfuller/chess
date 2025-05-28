package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.*;

import java.util.Objects;

public class GameServices {
    private final MemoryGameDAO gameAccess;
    private final MemoryAuthDAO authAccess;

    public GameServices(MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO) {
        this.authAccess = memoryAuthDAO;
        this.gameAccess = memoryGameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        if (request.gameName() == null) {
            throw new DataAccessException(400, "Error: bad request");
        }

        AuthData aData = authAccess.getAuth(request.authToken());
        if (aData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        GameData gData = gameAccess.createGame(request.gameName());
        return new CreateGameResult(gData.gameID());
    }

    public ListGameResult listGames(ListGameRequest request) throws DataAccessException {
        AuthData data = authAccess.getAuth(request.authToken());
        if (data == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        return new ListGameResult(gameAccess.listGames());
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        if (request.gameID() == null || request.playerColor() == null ||
                (!Objects.equals(request.playerColor(), "BLACK") && !Objects.equals(request.playerColor(), "WHITE"))) {
            throw new DataAccessException(400, "Error: bad request");
        }

        AuthData aData = authAccess.getAuth(request.authToken());
        if (aData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        GameData gData = gameAccess.getGame(request.gameID());
        gameAccess.joinGame(request.playerColor(), gData, aData.username());
    }

    public void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        gameAccess.updateGame(gameID, updatedGame);
    }
    public MemoryGameDAO getGameAccess(){
        return gameAccess;
    }

}