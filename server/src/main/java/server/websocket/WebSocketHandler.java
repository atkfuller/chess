package server.websocket;

import chess.*;
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
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command);
                case LEAVE -> leave(session, command);
                case RESIGN -> resign(session, command);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(session, moveCommand);
                }
                default -> sendError(session, "error: invalid command");
            }
        } catch (Exception e) {
            sendError(session, "error:"+e.getMessage());
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException, DataAccessException {
       String username=null;
        try {
           username = authDAO.getAuth(command.getAuthToken()).username();
       } catch (Exception e) {
           sendError(session, "error: not authenticated");
           return;
       }
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            sendError(session, "error: game not found");
            return;
        }
        Connection connection = new Connection(username, gameID, session);
        connections.add(connection);

        boolean isPlayer = username.equals(game.whiteUsername()) || username.equals(game.blackUsername());
        String color = username.equals(game.whiteUsername()) ? "WHITE" : username.equals(game.blackUsername()) ? "BLACK" : null;
        connections.sendMessage(username, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));

        String joinMsg = isPlayer ? username + " joined as " + color : username + " joined as observer";
        connections.broadcast(gameID, new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, joinMsg), username);
    }

    private void leave(Session session, UserGameCommand command) throws IOException, DataAccessException {
        String username = authDAO.getAuth(command.getAuthToken()).username();
        int gameID = command.getGameID();
        GameData game=gameDAO.getGame(gameID);
        ChessGame currGame=game.game();
        String color = username.equals(game.whiteUsername()) ? "WHITE" : username.equals(game.blackUsername()) ? "BLACK" : null;
        if(color!=null) {
            gameDAO.joinGame(color, game, null);
        }

        String leaveMsg = username + " has left the game.";
        connections.broadcast(gameID, new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, leaveMsg), username);
        connections.remove(username);
    }

    private void resign(Session session, UserGameCommand command) throws IOException, DataAccessException {
        String username = authDAO.getAuth(command.getAuthToken()).username();
        int gameID = command.getGameID();
        GameData game=gameDAO.getGame(gameID);
        ChessGame currGame=game.game();
        boolean isPlayer = username.equals(game.whiteUsername()) || username.equals(game.blackUsername());
        if(!isPlayer){
            sendError(session,"error: nonplayer cannot resign");
            return;
        }
        if(currGame.isGameOver()){
            sendError(session,"error: already game over");
            return;
        }
        currGame.setGameOver();
        gameDAO.updateGame(gameID, currGame);
        String resignMsg = username + " has resigned the game.";
        connections.broadcastAll(gameID,  new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, resignMsg));
    }

    private void makeMove(Session session, MakeMoveCommand command) throws IOException, DataAccessException {
        String username = authDAO.getAuth(command.getAuthToken()).username();
        ChessMove move = command.getMove();
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        ChessGame currGame=game.game();
        ChessBoard board= currGame.getBoard();
        ChessPiece piece= board.getPiece(move.getStartPosition());
        if (game == null) {
            sendError(session, "error: game not found");
            return;
        }
        ChessGame.TeamColor playerColor = username.equals(game.whiteUsername())
                ? ChessGame.TeamColor.WHITE: username.equals(game.blackUsername())
                ? ChessGame.TeamColor.BLACK : null;
        if(playerColor!=piece.getTeamColor()){
            sendError(session, "error: cannot move other player piece");
            return;
        }
        if(currGame.isInCheckmate(ChessGame.TeamColor.WHITE)||currGame.isInCheckmate(ChessGame.TeamColor.BLACK)||currGame.isGameOver()){
            sendError(session, "error: game over or checkmate");
            return;
        }
        try{
            game.game().makeMove(move);
            gameDAO.updateGame(gameID,game.game());
        } catch (InvalidMoveException e) {
            sendError(session, "error:"+e.getMessage());
            return;
        }

        // Normally you'd apply the move to the game here

        String moveMsg = String.format("%s moved from %s to %s", username,
                move.getStartPosition(), move.getEndPosition());
        connections.broadcast(gameID, new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMsg), username);

        // Reload updated game to all clients
        connections.broadcastAll(gameID, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));
    }
    public void sendError(Session session, String errorMsg) throws IOException {
        sendMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMsg));
    }
    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(messageToJson(message));
    }
    private String messageToJson(ServerMessage message) {
        return new com.google.gson.Gson().toJson(message);
    }
}
