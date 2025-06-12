package server;

import dataaccess.*;
import server.handler.*;
import server.websocket.WebSocketHandler;
import service.*;
import spark.*;

public class Server {
    private UserServices userService;
    private GameServices gameService;
    private ClearService clearService;
    private WebSocketHandler webSocketHandler;

    public Server(IDAOsProvider provider) {
        UserDAO userDAO = provider.getUserDAO();
        AuthDAO authDAO = provider.getAuthDAO();
        GameDAO gameDAO = provider.getGameDAO();

        this.userService = new UserServices(userDAO, authDAO, gameDAO);
        this.gameService = new GameServices(authDAO, gameDAO);
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
        webSocketHandler=new WebSocketHandler(authDAO, gameDAO);
    }
    public Server() {
        try {
            IDAOsProvider provider= new MySqlDAOsProvider();
            UserDAO userDAO = provider.getUserDAO();
            AuthDAO authDAO = provider.getAuthDAO();
            GameDAO gameDAO = provider.getGameDAO();

            this.userService = new UserServices(userDAO, authDAO, gameDAO);
            this.gameService = new GameServices(authDAO, gameDAO);
            this.clearService = new ClearService(userDAO, authDAO, gameDAO);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketHandler);
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler(userService));
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(userService));
        Spark.get("/game", new ListGamesHandler(gameService));
        Spark.post("/game", new CreateGameHandler(gameService));
        Spark.put("/game", new JoinGameHandler(gameService));


        Spark.exception(DataAccessException.class, this::exceptionHandler);
        Spark.exception(Exception.class, this::genericExceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        res.body(ex.toJson());
    }
    private void genericExceptionHandler(Exception ex, Request req, Response res) {
        res.status(500);
        res.type("application/json");
        res.body("{\"message\":\"Internal server error\"}");
    }



}
