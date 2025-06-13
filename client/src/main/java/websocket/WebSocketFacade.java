package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.net.URI;
import javax.websocket.*;
import java.io.IOException;
import java.net.URISyntaxException;


public class WebSocketFacade {
    private Session session;
    private final Gson gson = new Gson();
    private NotificationHandler notificationHandler;

    public WebSocketFacade(String serverUrl, NotificationHandler notificationHandler) throws Exception {
        try {
            serverUrl = serverUrl.replace("http", "ws");
            URI socketURI = new URI(serverUrl + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.onNotification(notification.toString());
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    public void connect(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );
        send(command);
    }

    public void makeMove(String authToken, int gameID, String username, String color, ChessMove move) throws Exception {
        MakeMoveCommand command = new MakeMoveCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID,
                move
        );
        send(command);
    }

    public void leave(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        send(command);
    }

    public void resign(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        );
        send(command);
    }

    private void send(UserGameCommand command) throws Exception {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(gson.toJson(command));
        }
    }

    private class ClientSocket extends org.eclipse.jetty.websocket.api.WebSocketAdapter {
        public void onWebSocketConnect(Session sess) {
            session = sess;
        }

        @Override
        public void onWebSocketText(String message) {
            ServerMessage base = gson.fromJson(message, ServerMessage.class);
            switch (base.getServerMessageType()) {
                case ERROR -> {
                    ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                    notificationHandler.onError(error.getErrorMessage());
                }
                case NOTIFICATION -> {
                    NotifcationMessage note = gson.fromJson(message, NotifcationMessage.class);
                    notificationHandler.onNotification(note.getMessage());
                }
                case LOAD_GAME -> {
                    LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                    notificationHandler.onLoadGame(load.getGame());
                }
            }
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            notificationHandler.onError("WebSocket error: " + cause.getMessage());
        }
    }
}