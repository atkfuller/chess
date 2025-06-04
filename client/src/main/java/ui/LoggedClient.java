package ui;
import com.google.gson.Gson;
import model.*;
import server.ServerFacade;
import dataaccess.DataAccessException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoggedClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken=null;
    private Map<Integer, GameData> allGames= new HashMap<>();

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
                case "play" -> playGame(params);
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
        Integer key=1;
        for (var game : games) {
            addToGames(key, game);
            String gameline=String.format("%d) Game name: %s Players(white, black): %s, %s", key,game.gameName(), game.whiteUsername(), game.blackUsername());
            result.append(gson.toJson(gameline)).append('\n');
            key++;
        }
        return result.toString();
    }
    public String playGame(String... params) throws DataAccessException{
        assertSignedIn();
        if(params.length==2) {
            Integer gameID= allGames.get(Integer.valueOf(params[0])).gameID();
            String color= params[1].toUpperCase();
            server.joinGame(new JoinGameRequest(authToken, color, gameID));
            return String.format("joined game", allGames.get(Integer.valueOf(params[0])).gameName());
        }
        throw new DataAccessException(400, "Expected: <number> <color>");
   }
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
                - play game(play) <number> <color>
                - signOut
                - quit
                """;
    }

    private void assertSignedIn() throws DataAccessException {
        if (state == State.SIGNEDOUT) {
            throw new DataAccessException(400, "You must sign in");
        }
    }
    private void addToGames(Integer i, GameData game){
        allGames.put(i, game);
    }
}

