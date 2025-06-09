package ui;
import model.LoginRequest;
import model.RegisterRequest;
import java.util.Arrays;

import static ui.EscapeSequences.*;


public class LoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
   // private final NotificationHandler notificationHandler;
    //private WebSocketFacade ws;
    private State state = State.SIGNED_OUT;

    public LoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        //this.notificationHandler = notificationHandler;
    }

    public ReplPhase eval(String input) throws Exception {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "quit" -> null;
            default -> {
                System.out.println(help());
                yield thisPhase();
            }
        };
    } catch (Exception e) {
        System.out.println(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
        return thisPhase();
    }

}

    private ReplPhase login(String... params) throws Exception {
        if (params.length == 2) {
            var result = server.login(new LoginRequest(params[0], params[1]));
            System.out.printf("You logged in as %s%n", params[0]);
            return new PostLoginUI(serverUrl, result.authToken(), params[0]);
        }
        throw new ClientException(400, "Expected: <username> <password>");
    }

    private ReplPhase register(String... params) throws Exception {
        if (params.length == 3) {
            var result = server.register(new RegisterRequest(params[0], params[1], params[2]));
            System.out.printf("You signed in as %s%n", params[0]);
            return new PostLoginUI(serverUrl, result.authToken(), params[0]);
        }
        throw new ClientException(400, "Expected: <username> <password> <email>");
    }

    private ReplPhase thisPhase() {
        return new PreLoginUI(serverUrl);
    }

    public String help() {
        return SET_TEXT_COLOR_YELLOW + """
            - login <username> <password>
            - register <username> <password> <email>
            - quit
            """;
    }
}




