package server;

import dataaccess.*;
import server.handler.*;
import service.*;
import spark.*;

public class Server {
    private UserServices userService;
    private GameServices gameService;
    private ClearService clearService;

    public Server(IDAOsProvider provider) {
        UserDAO userDAO = provider.getUserDAO();
        AuthDAO authDAO = provider.getAuthDAO();
        GameDAO gameDAO = provider.getGameDAO();

        this.userService = new UserServices(userDAO, authDAO, gameDAO);
        this.gameService = new GameServices(authDAO, gameDAO);
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
    }
    public Server() {
        try {
            new Server(new MySqlDAOsProvider());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler(userService));
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(userService));
        Spark.get("/game", new ListGamesHandler(gameService));
        Spark.post("/game", new CreateGameHandler(gameService));
        Spark.put("/game", new JoinGameHandler(gameService));


        Spark.exception(DataAccessException.class, this::exceptionHandler);
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



}
