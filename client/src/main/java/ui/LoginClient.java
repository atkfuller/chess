package ui;
import model.LoginRequest;
import model.RegisterRequest;
import dataaccess.DataAccessException;
import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;


public class LoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
   // private final NotificationHandler notificationHandler;
    //private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    public LoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        //this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }
    public String register(String... params) throws DataAccessException {
        if (params.length == 3) {
            var result=server.register(new RegisterRequest(params[0],params[1],params[2]));
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            System.out.println(String.format("You signed in as %s", visitorName));
            PostLoginUI newUI= new PostLoginUI(serverUrl, result.authToken(), visitorName);
            newUI.run();
            new PreLoginUI(serverUrl).run();
            return String.format("You signed in as %s", visitorName);
        }
        throw new DataAccessException(400, "Expected: <username> <password> <email>");
    }
    public String login(String... params) throws DataAccessException {
        if(params.length==2) {
            var result=server.login(new LoginRequest(params[0], params[1]));
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            System.out.println(String.format("You logged in as %s", visitorName));
            PostLoginUI newUI= new PostLoginUI(serverUrl, result.authToken(), visitorName);
            newUI.run();
            new PreLoginUI(serverUrl).run();
            return String.format("You logged in as %s", visitorName);
        }
        throw new DataAccessException(400, "Expected: <username> <password>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return SET_TEXT_COLOR_YELLOW+""" 
                    - login <username> <password>
                    - register <username> <password> <email>
                    - quit
                    """;
        }
        return SET_TEXT_COLOR_YELLOW+"""
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
    public void clear() throws DataAccessException {
        server.clear();
    }


}

