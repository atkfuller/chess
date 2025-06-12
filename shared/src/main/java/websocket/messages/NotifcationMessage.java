package websocket.messages;

public class NotifcationMessage extends ServerMessage{
    private final String message;
    public NotifcationMessage(ServerMessageType type, String message) {
        super(type);
        this.message=message;
    }
    public String getMessage(){return message;}
}
