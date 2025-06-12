package websocket;

import model.GameData;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void onError(String message);
    void onNotification(String message);
    void onLoadGame(GameData game);
}