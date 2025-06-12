package server.websocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;

public record Connection(String username, int gameID, Session session){
    public Session session() {
        return session;
    }

    public String username() {
        return username;
    }

    public int gameID() {
        return gameID;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Connection that = (Connection) obj;
        return gameID == that.gameID &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, gameID);
    }
}
