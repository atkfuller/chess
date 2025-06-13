package websocket;

import model.GameData;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void onNotification(String message);

}