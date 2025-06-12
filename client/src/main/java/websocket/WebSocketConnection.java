package client.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.function.Consumer;

public class WebSocketConnection extends WebSocketAdapter {

    private final Consumer<String> messageHandler;

    public WebSocketConnection(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void onWebSocketText(String message) {
        if (message != null && messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    public void send(String message) {
        try {
            if (getSession() != null && getSession().isOpen()) {
                getRemote().sendString(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        return getSession() != null && getSession().isOpen();
    }
}
