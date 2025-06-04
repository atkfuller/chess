package client;

import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:"+port;
        facade= new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    @BeforeEach
     void clear() throws DataAccessException {
        facade.clear();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }
    @Test
    void register() throws Exception {
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void registerNegative() {
        assertThrows(Exception.class, () -> {
            var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
            facade.register(new RegisterRequest("player1", "pass", "invalid@email.com"));
        });
    }
    @Test
    void login() throws Exception {
        facade.register(new RegisterRequest("player2", "password", "p2@email.com"));
        var authData = facade.login(new LoginRequest("player2", "password"));
        assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void loginNegative() {
        assertThrows(Exception.class, () -> {
            facade.login(new LoginRequest("nonexistent", "wrongpass"));
        });
    }

    @Test
    void createGame() throws Exception {
        var authData = facade.register(new RegisterRequest("player3", "password", "p3@email.com"));
        var gameData = facade.createGame(new CreateGameRequest(authData.authToken(), "My Chess Game"));
        assertNotNull(gameData.gameID());
        assertEquals(1, gameData.gameID());
    }
    @Test
    void createGameNegative() {
        assertThrows(Exception.class, () -> {
            facade.createGame(new CreateGameRequest("invalid-token", "Should Fail"));
        });
    }
    @Test
    void listGames() throws Exception {
        var authData = facade.register(new RegisterRequest("player4", "password", "p4@email.com"));
        facade.createGame(new CreateGameRequest(authData.authToken(), "Game One"));
        facade.createGame(new CreateGameRequest(authData.authToken(), "Game Two"));
        var games = facade.listGames(new ListGameRequest(authData.authToken()));
        assertTrue(games.games().stream().anyMatch(g -> g.gameName().equals("Game One")));
        assertTrue(games.games().stream().anyMatch(g -> g.gameName().equals("Game Two")));
    }
    @Test
    void listGamesNegative() {
        assertThrows(Exception.class, () -> {
            facade.listGames(new ListGameRequest("invalid-token"));
        });
    }
    @Test
    void joinGame() throws Exception {
        var authData = facade.register(new RegisterRequest("player5", "password", "p5@email.com"));
        var gameData = facade.createGame(new CreateGameRequest(authData.authToken(), "Joinable Game"));
        assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest(authData.authToken(), "WHITE", gameData.gameID())));
    }
    @Test
    void joinGameNegative() {
        assertThrows(Exception.class, () -> {
            facade.joinGame(new JoinGameRequest("bad-token", "WHITE", 9999));
        });
    }
    @Test
    void logout() throws Exception {
        var authData = facade.register(new RegisterRequest("player6", "password", "p6@email.com"));
        facade.logout(new LogoutRequest(authData.authToken()));
        // Optionally, test that further action is denied after logout.
    }
    @Test
    void logoutNegative() {
        assertThrows(Exception.class, () -> {
            facade.logout(new LogoutRequest("invalid-token"));
        });
    }
    @Test
    void cleartest() throws Exception {
        var authData = facade.register(new RegisterRequest("player7", "password", "p7@email.com"));
        facade.createGame(new CreateGameRequest(authData.authToken(), "Temp Game"));
        facade.clear();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            facade.listGames(new ListGameRequest(authData.authToken()));
        });

        assertEquals("Error: unauthorized", ex.getMessage());

    }

}
