package ui;
import com.google.gson.Gson;
import model.CreateGameRequest;
import model.ListGameRequest;
import model.LogoutRequest;
import server.ServerFacade;
import dataaccess.DataAccessException;
import java.util.Arrays;
public class LoggedClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken=null;

    public LoggedClient(String serverUrl, String authToken) {
        server = new ServerFacade(serverUrl);
        state=State.SIGNEDIN;
        this.serverUrl = serverUrl;
        this.authToken=authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
//                case "play game" -> playGame();
//                case "observe game" -> observeGame(params);
                default -> help();
            };
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }
    public String logout(String... params) throws DataAccessException{
        assertSignedIn();
        server.logout(new LogoutRequest(authToken));
        state = State.SIGNEDOUT;
        return String.format("%s logged out", visitorName);
    }
    public String createGame(String... params) throws DataAccessException {
        assertSignedIn();
        if(params.length==1) {
            server.createGame(new CreateGameRequest(authToken, params[0]));
            return String.format("created game", params[0]);
        }
        throw new DataAccessException(400, "Expected: <gamename>");
    }
    public String listGames(String... params) throws DataAccessException {
        assertSignedIn();
        var games = server.listGames(new ListGameRequest(authToken)).games();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            String gameline=String.format("Game name: %s Players(white, black): %s, %s", game.gameName(), game.whiteUsername(), game.blackUsername());
            result.append(gson.toJson(gameline)).append('\n');
        }
        return result.toString();
    }
//    public String playGame(String... params) throws DataAccessException{
//
//    }
//    public String observeGame(String... params) throws DataAccessException{
//
//    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - logout
                    - create game(create) <game name>
                    - list games(list)
                    """;
        }
        return """
                - logout
                - create game(create) <game name>
                - list games(list)
                - adoptAll
                - signOut
                - quit
                """;
    }

    private void assertSignedIn() throws DataAccessException {
        if (state == State.SIGNEDOUT) {
            throw new DataAccessException(400, "You must sign in");
        }
    }
}

