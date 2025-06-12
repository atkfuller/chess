package server.websocket;

public record ClientInfo(String username, int gameID, boolean isPlayer, String color) {}