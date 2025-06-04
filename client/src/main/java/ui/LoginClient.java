package ui;
import server.ServerFacade;
import dataaccess.DataAccessException;
import java.util.Arrays;


public class LoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    public LoginClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "signin" -> signIn(params);
                case "rescue" -> rescuePet(params);
                case "list" -> listPets();
                case "signout" -> signOut();
                case "adopt" -> adoptPet(params);
                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }
    public String register(String... params) throws DataAccessException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            return String.format("You signed in as %s", visitorName);
        }
        throw new DataAccessException(400, "Expected: <yourname>");
    }
    public String login(String... params) throws DataAccessException {
        if(params.length>=1) {
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            return String.format("You logged in as %s", visitorName);
        }
        throw new DataAccessException(400, "Expected: <yourname>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                - login <username> <password>
                - register <username> <password> <email>
                - quit
                """;
    }

    private void assertSignedIn() throws DataAccessException {
        if (state == State.SIGNEDOUT) {
            throw new DataAccessException(400, "You must sign in");
        }
    }
}
}
