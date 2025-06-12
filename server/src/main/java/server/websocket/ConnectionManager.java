package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConnectionManager {
    private final Map<String, Connection> userConnections = new ConcurrentHashMap<>();

    public void add(Connection connection) {
        userConnections.put(connection.username(), connection);
    }

    public void remove(String username) {
        userConnections.remove(username);
    }

    public void removeBySession(Session session) {
        userConnections.entrySet().removeIf(entry -> entry.getValue().session().equals(session));
    }

    public Connection get(String username) {
        return userConnections.get(username);
    }

    public Set<Connection> getAllInGame(int gameID) {
        return userConnections.values().stream()
                .filter(c -> c.gameID() == gameID)
                .collect(Collectors.toSet());
    }

    public void sendMessage(String username, ServerMessage message) throws IOException {
        Connection connection = userConnections.get(username);
        if (connection != null && connection.session().isOpen()) {
            connection.session().getRemote().sendString(messageToJson(message));
        }
    }

    public void broadcast(int gameID, ServerMessage message, String excludeUsername) throws IOException {
        for (Connection connection : getAllInGame(gameID)) {
            if (!connection.username().equals(excludeUsername) && connection.session().isOpen()) {
                connection.session().getRemote().sendString(messageToJson(message));
            }
        }
    }

    public void broadcastAll(int gameID, ServerMessage message) throws IOException {
        for (Connection connection : getAllInGame(gameID)) {
            if (connection.session().isOpen()) {
                connection.session().getRemote().sendString(messageToJson(message));
            }
        }
    }

    public void sendError(String username, String errorMsg) throws IOException {
        sendMessage(username, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMsg));
    }

    private String messageToJson(ServerMessage message) {
        return new com.google.gson.Gson().toJson(message);
    }
}
