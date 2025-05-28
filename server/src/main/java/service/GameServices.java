package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Objects;

public class GameServices {
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public GameServices(AuthDAO authDAO, GameDAO gameDAO) {
        this.authAccess = authDAO;
        this.gameAccess = gameDAO;
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


}