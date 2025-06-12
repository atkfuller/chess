package ui;
import com.google.gson.Gson;
import model.*;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class LoggedClient implements NotificationHandler {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNED_OUT;
    private String authToken=null;
    private Map<Integer, GameData> allGames= new HashMap<>();
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;

    public LoggedClient(String serverUrl, String authToken, String name) {
        visitorName= name;
        server = new ServerFacade(serverUrl);
        state=State.SIGNED_IN;
        this.serverUrl = serverUrl;
        this.authToken=authToken;
        this.notificationHandler = notificationHandler;
    }

    public ReplPhase eval(String input) throws Exception {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "logout" -> logout();
            case "create" -> {
                System.out.println(createGame(params));
                yield thisPhase();
            }
            case "list" -> {
                System.out.println(listGames(params));
                yield thisPhase();
            }
            case "play" -> playGame(params);
            case "observe" -> {
                System.out.println(observeGame(params));
                yield thisPhase();
            }
            default -> {
                System.out.println(help());
                yield thisPhase();
            }
        };
    }
    private ReplPhase logout() throws Exception {
        server.logout(new LogoutRequest(authToken));
        System.out.printf("%s logged out%n", visitorName);
        return new PreLoginUI(serverUrl);
    }
    public String createGame(String... params) throws Exception {
        assertSignedIn();
        if(params.length==1) {
            server.createGame(new CreateGameRequest(authToken, params[0]));
            return String.format("created game", params[0]);
        }
        throw new ClientException(400, "Expected: <gamename>");
    }
    public String listGames(String... params) throws Exception {
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
    public ReplPhase playGame(String... params) throws Exception{
        assertSignedIn();
        int index;
        listGames();
        try {
            index = Integer.valueOf(params[0]);
        } catch (NumberFormatException e) {
            throw new ClientException(400, "Error: Must be a valid number.");
        }

        if (index < 0 || index > allGames.size()) {
            throw new ClientException(400, "Error: Invalid game number.");
        }
        if(params.length==2) {
            GameData game=allGames.get(Integer.valueOf(params[0]));
            Integer gameID= game.gameID();
            String color= params[1].toUpperCase();
            server.joinGame(new JoinGameRequest(authToken, color, gameID));
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.enterPetShop(visitorName);
            displayGame(game, color);
            System.out.println(String.format("joined game", allGames.get(Integer.valueOf(params[0])).gameName()));
            return new GameUI(serverUrl, authToken, game, color);
        }
        throw new ClientException(400, "Expected: <number> <color>");
   }
    public String observeGame(String... params) throws Exception{
        if(params.length==2) {
            GameData game=allGames.get(Integer.valueOf(params[0]));
            Integer gameID= game.gameID();
            String color= params[1].toUpperCase();
            displayGame(game, color);
            return String.format("observe game", allGames.get(Integer.valueOf(params[0])).gameName());
        }
        throw new ClientException(400, "Expected: <number> <color>");
    }

    public String help() {
        if (state == State.SIGNED_OUT) {
            return SET_TEXT_COLOR_YELLOW+"""
                    - help
                    - logout
                    - create game(create) <game name>
                    - list games(list)
                    - play game(play) <number> <color>
                    - observe game(observe) <number> <color>
                    """;
        }
        return SET_TEXT_COLOR_YELLOW+"""
                - help
                - logout
                - create game(create) <game name>
                - list games(list)
                - play game(play) <number> <color>
                - observe game(observe) <number> <color>
                """;
    }

    private void assertSignedIn() throws ClientException{
        if (state == State.SIGNED_OUT) {
            throw new ClientException(400, "You must sign in");
        }
    }
    private void addToGames(Integer i, GameData game){
        allGames.put(i, game);
    }
    private void displayGame(GameData game, String color){
        if(Objects.equals(color, "WHITE")){
            BoardPrinter.printBoardWhiteView(game.game().getBoard(),null);
        }
        else{
            BoardPrinter.printBoardBlackView(game.game().getBoard(), null);
        }
    }
    private ReplPhase thisPhase() {
        return new PostLoginUI(serverUrl, authToken, visitorName);
    }
    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_RED + notification.getMessage());
        printPrompt();
    }
}

