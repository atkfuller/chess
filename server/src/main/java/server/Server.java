package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.handler.*;
import service.*;
import spark.*;

public class Server {
    private final UserServices userService;
    private final GameServices gameService;
    private final ClearService clearService;

    public Server() {
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

        this.userService = new UserServices(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        this.gameService = new GameServices(memoryAuthDAO, memoryGameDAO);
        this.clearService = new ClearService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
