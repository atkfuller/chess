package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class WebSocketFacade {
    private final WebSocketClient client = new WebSocketClient();
    private Session session;
    private final Gson gson = new Gson();
    private NotificationHandler listener;

    public WebSocketFacade(String serverUri, NotificationHandler listener) throws Exception {
        this.listener = listener;
        client.start();
        client.connect(new ClientSocket(), new URI(serverUri)).get();
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
            session.getRemote().sendString(gson.toJson(command));
        }
    }

    private class ClientSocket extends org.eclipse.jetty.websocket.api.WebSocketAdapter {
        @Override
        public void onWebSocketConnect(Session sess) {
            session = sess;
        }

        @Override
        public void onWebSocketText(String message) {
            ServerMessage base = gson.fromJson(message, ServerMessage.class);
            switch (base.getServerMessageType()) {
                case ERROR -> {
                    ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                    listener.onError(error.getErrorMessage());
                }
                case NOTIFICATION -> {
                    NotifcationMessage note = gson.fromJson(message, NotifcationMessage.class);
                    listener.onNotification(note.getMessage());
                }
                case LOAD_GAME -> {
                    LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                    listener.onLoadGame(load.getGame());
                }
            }
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            listener.onError("WebSocket error: " + cause.getMessage());
        }
    }
}