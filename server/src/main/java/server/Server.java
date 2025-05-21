package server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.handler.*;
import services.*;
import spark.*;

import javax.xml.crypto.Data;
import model.UserData;

import java.util.Map;

public class Server {
    private final UserServices userService;
    private final GameServices gameService;
    private final ClearService clearService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        this.userService = new UserServices(userDAO, authDAO, gameDAO);
        this.gameService = new GameServices(authDAO, gameDAO);
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
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
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }



}
