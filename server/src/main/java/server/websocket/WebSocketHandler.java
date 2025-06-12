package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotifcationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command);
                case LEAVE -> leave(command);
                case RESIGN -> resign(command);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(moveCommand);
                }
                default -> connections.sendError(command.getVistorName(), "Invalid command");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorUser = new Gson().fromJson(message, UserGameCommand.class).getVistorName();
            connections.sendError(errorUser, "Error processing command: " + e.getMessage());
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException, DataAccessException {
        String username = command.getVistorName();
        int gameID = command.getGameID();

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            connections.sendError(username, "Game not found.");
            return;
        }

        boolean isPlayer = username.equals(game.whiteUsername()) || username.equals(game.blackUsername());
        String color = username.equals(game.whiteUsername()) ? "WHITE" : username.equals(game.blackUsername()) ? "BLACK" : null;

        Connection connection = new Connection(username, gameID, isPlayer, color, session);
        connections.add(connection);

        connections.sendMessage(username, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));

        String joinMsg = isPlayer ? username + " joined as " + color : username + " joined as observer";
        connections.broadcast(gameID, new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinMsg), username);
    }

    private void leave(UserGameCommand command) throws IOException {
        String username = command.getVistorName();
        int gameID = command.getGameID();

        connections.remove(username);

        String leaveMsg = username + " has left the game.";
        connections.broadcast(gameID, new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, leaveMsg), username);
    }

    private void resign(UserGameCommand command) throws IOException {
        String username = command.getVistorName();
        int gameID = command.getGameID();

        String resignMsg = username + " has resigned the game.";
        connections.broadcast(gameID, new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, resignMsg), username);
    }

    private void makeMove(MakeMoveCommand command) throws IOException, DataAccessException {
        String username = command.getVistorName();
        ChessMove move = command.getMove();
        int gameID = command.getGameID();

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            connections.sendError(username, "Game not found.");
            return;
        }

        // Normally you'd apply the move to the game here

        String moveMsg = String.format("%s moved from %s to %s", username,
                move.getStartPosition(), move.getEndPosition());
        connections.broadcast(gameID, new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMsg), username);

        // Reload updated game to all clients
        connections.broadcastAll(gameID, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));
    }
}