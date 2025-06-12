package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
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
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action.getVistorName(), session);
            case LEAVE -> leave(action.getVistorName());
            case RESIGN -> resign(action.getVistorName());
            case MAKE_MOVE -> {
                MakeMoveCommand moveAction = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(moveAction.getVistorName(), moveAction.getMove());
            }
            default -> {
                ErrorMessage errorMessage= new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Errpr: invlaid command");
                connections.broadcast(action.getVistorName(), errorMessage);
            }
        }
    }

    private void connect(String visitorName, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s has joined the game", visitorName);
        var notification = new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcast(visitorName, notification);
    }

    private void leave(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcast(visitorName, notification);
    }
    public void resign(String visitorName)throws IOException{
        connections.remove(visitorName);
        var message=String.format("%s has resigned the game", visitorName);
        var notifcation= new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION,message);
        connections.broadcast(visitorName, notifcation);
    }
    public void makeMove(String vistorName, ChessMove move) throws DataAccessException {
        try {
            var startPostion= move.getStartPosition();
            var endPosition= move.getEndPosition();
            var message = String.format("%s moved to position %s from  %s", vistorName, startPostion.toString(), endPosition.toString());
            var notification = new NotifcationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }
}
