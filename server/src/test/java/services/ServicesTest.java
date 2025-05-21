package services;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ServicesTest {
        private UserServices userService;
        private GameServices gameService;
        private  ClearService clearService;
        private UserDAO accessUser;
        private AuthDAO accessAuth;
        private GameDAO accessGame;
        private ArrayList<GameData> allGames;
    @BeforeEach
    void setUp() throws DataAccessException {
        // Create new DAO instances for each test
        accessUser = new UserDAO();
        accessAuth = new AuthDAO();
        accessGame = new GameDAO();

        // Inject shared DAOs into all services
        userService = new UserServices(accessUser, accessAuth, accessGame);
        gameService = new GameServices(accessAuth, accessGame);
        clearService = new ClearService(accessUser, accessAuth, accessGame);

        clearService.clearAll(); // Start clean before each test
    }

    /*test to implement
            create game
            create game unauthorized
            create game bad request
            join game
            join game unauthroized
         */
    @Test
    void registerU() throws DataAccessException {
        UserData user = new UserData("atfuller", "teddy", "good@gmail.com");
        RegisterRequest req = new RegisterRequest("atfuller", "teddy", "good@gmail.com");
        RegisterResult res = userService.register(req);
        var users = userService.getUsers();
        var auth = userService.getAuth();
        assertEquals(1, users.size());
        assertEquals(1, auth.size());
        assertTrue(users.contains(user));
    }
    void populateUsers() throws DataAccessException {
        userService.register(new RegisterRequest("atfuller", "teddy", "good@gmail.com"));
        userService.register(new RegisterRequest("jdoe", "secure456", "john@example.com"));
        userService.register(new RegisterRequest("msmith", "pass789", "mary@example.org"));
        userService.register(new RegisterRequest("knguyen", "dragon2024", "khoa@example.net"));
        userService.register(new RegisterRequest("aluna", "moonlight", "aluna@example.com"));
        userService.register(new RegisterRequest("rpatel", "india@321", "raj@example.in"));
        userService.register(new RegisterRequest("cjones", "purple!car", "carl@example.biz"));
        userService.register(new RegisterRequest("zli", "123Abc!", "zhen@example.cn"));
        userService.register(new RegisterRequest("sanders", "qwerty007", "sandy@example.co"));
        userService.register(new RegisterRequest("lwilson", "wilson@pass", "laura@example.us"));

        accessGame.addGame(new GameData(1, "alice", "bob", "Classic Match", new ChessGame()));
        accessGame.addGame(new GameData(2, "charlie", "diana", "Opening Practice", new ChessGame()));
        accessGame.addGame(new GameData(3, "edward", "fiona", "Blitz Showdown", new ChessGame()));
        accessGame.addGame(new GameData(4, "george", "harriet", "Endgame Tactics", new ChessGame()));
        accessGame.addGame(new GameData(5, "ivan", "julia", "Queen's Gambit", new ChessGame()));
        accessGame.addGame(new GameData(6, "kevin", "laura", "King's Defense", new ChessGame()));
        accessGame.addGame(new GameData(7, "maria", "nathan", "Rook Battle", new ChessGame()));
        accessGame.addGame(new GameData(8, "oliver", "paula", "Pawn Storm", new ChessGame()));
        accessGame.addGame(new GameData(9, "quentin", "rachel", "Checkmate Drill", new ChessGame()));
        accessGame.addGame(new GameData(10, "sam", "tina", "Training Match", new ChessGame()));
        accessGame.addGame(new GameData(120, null, "tina", "newGame", new ChessGame()));
        allGames = accessGame.listGames();
    }
    @Test
    void alreadyTaken() throws DataAccessException {
        populateUsers();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.register(new RegisterRequest("jdoe", "123", "wrong"));
        });

        assertEquals("Error: already taken username", ex.getMessage());
    }

    @Test
    void loginUser() throws DataAccessException{
        populateUsers();
        ArrayList<AuthData> auth= userService.getAuth();
        UserData user= new UserData("msmith", "pass789", "mary@example.org");
        AuthData author= accessAuth.getAuthByUsername("msmith");
        LoginResult res= userService.login(new LoginRequest("msmith", "pass789"));
        assertNotEquals(author.authToken(), res.authToken());
    }
    @Test
    void loginWrongPassword() throws DataAccessException{
        populateUsers();
        ArrayList<AuthData> auth= userService.getAuth();
        UserData user= new UserData("msmith", "pass789", "mary@example.org");
        AuthData author= accessAuth.getAuthByUsername("msmith");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.login(new LoginRequest("msmith", "pass8"));
        });

        assertEquals("Error: unauthorized", ex.getMessage());
    }
    @Test
    void logoutCorrect() throws DataAccessException{
        populateUsers();
        ArrayList<AuthData> auth = userService.getAuth();
        ArrayList<UserData> users = userService.getUsers();
        UserData user = users.get(4);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        userService.logout(new LogoutRequest(data.authToken()));
        assertFalse(auth.contains(data));

    }
    @Test
    void logoutWrong() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users= userService.getUsers();
        UserData user= users.get(4);
        AuthData data=accessAuth.getAuthByUsername(user.username());
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.logout(new LogoutRequest("wrongtoken"));
        });

        assertEquals("Error: unauthorized", ex.getMessage());
    }
    @Test
    void listGameCorrect() throws DataAccessException{
        populateUsers();
        ArrayList< AuthData> auth= userService.getAuth();
        ArrayList<UserData> users= userService.getUsers();
        UserData user= users.get(5);
        AuthData data=accessAuth.getAuthByUsername(user.username());
        ListGameResult list=gameService.listGames(new ListGameRequest(data.authToken()));
        assertEquals(list.games(), allGames);

    }
    @Test
    void listGameWrong() throws DataAccessException{
        populateUsers();
        ArrayList< AuthData> auth= userService.getAuth();
        ArrayList<UserData> users= userService.getUsers();
        UserData user= users.get(5);
        AuthData data=accessAuth.getAuthByUsername(user.username());
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.listGames(new ListGameRequest("wrong token"));
        });

        assertEquals("Error: unauthorized", ex.getMessage());

    }
    @Test
    void createGameCorrect() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = userService.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        CreateGameRequest request = new CreateGameRequest(data.authToken(), "myGame");
        CreateGameResult result= gameService.createGame(request);
        ArrayList<GameData> games= accessGame.listGames();
        for(GameData gData: games){
            if(gData.gameName()=="myGame"){
                assertSame("myGame", gData.gameName());
            }
        }
    }
    @Test
    void createGameBad() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = userService.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        CreateGameRequest request = new CreateGameRequest(data.authToken(), null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(request);
        });

        assertEquals("Error: bad request", ex.getMessage());

    }
    @Test
    void createGameUnAuthorized() throws DataAccessException{
        populateUsers();
        CreateGameRequest request = new CreateGameRequest("bad token", "myGame");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(request);
        });

        assertEquals("Error: unauthorized", ex.getMessage());

    }
    @Test
    void joinGameCorrect() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = userService.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        JoinGameRequest request= new JoinGameRequest(data.authToken(), "WHITE", 120);
        gameService.joinGame(request);
        ArrayList<GameData> games= accessGame.listGames();
        GameData game= accessGame.getGame(120);
        assertEquals(game.whiteUsername(), user.username());

    }
    @Test
    void joinGameBad()throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = userService.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        JoinGameRequest request= new JoinGameRequest(data.authToken(), "PURPLE", 120);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(request);
        });
        assertEquals("Error: bad request", ex.getMessage());
    }
    @Test
    void joinGameUnauthorized() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = userService.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        JoinGameRequest request= new JoinGameRequest("bad token", "WHITE", 120);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(request);
        });
        assertEquals("Error: unauthorized", ex.getMessage());
    }
    @Test
    void joinGameTaken()throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = userService.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        JoinGameRequest request= new JoinGameRequest(data.authToken(), "BLACK", 120);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(request);
        });
        assertEquals("Error: already taken", ex.getMessage());
    }

}
