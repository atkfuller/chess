package server;

import dataaccess.DataAccessException;
import com.google.gson.Gson;
import services.*;
import spark.*;

import javax.xml.crypto.Data;
import model.UserData;

import java.util.Map;

public class Server {
    private final UserServices service;
    public Server(){
        service=new UserServices();
    }
    public Server(UserServices serv){
        this.service=serv;
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);


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
    private Object registerUser (Request req, Response res) throws DataAccessException{
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result=service.register(registerRequest);
        return new Gson().toJson(result);
    }
    private Object loginUser(Request req, Response res) throws DataAccessException{
        LoginRequest loginRequest= new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result=service.login(loginRequest);
        return new Gson().toJson(result);
    }
    private Object clear(Request req, Response res){
        service.clear();
        return new Gson().toJson(Map.of("message", "Database cleared."));
    }
    private Object logoutUser(Request req, Response res) throws DataAccessException{
        LogoutRequest request= new Gson().fromJson(req.body(), LogoutRequest.class);
        service.logout(request);
        return new Gson().toJson(Map.of("message", "loggedOut User"));
    }
    private Object listGames(Request req, Response res) throws DataAccessException{
        ListGameRequest request= new Gson().fromJson(req.body(), ListGameRequest.class);
        ListGameResult result= service.listGame(request);
        return new Gson().toJson(result);
    }
    private Object createGame(Request req, Response res) throws DataAccessException{
        CreateGameRequest request= new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameResult result= service.createGame(request);
        return new Gson().toJson(result);
    }
    private Object joinGame(Request req, Response res) throws DataAccessException{
        JoinGameRequest request= new Gson().fromJson(req.body(), JoinGameRequest.class);
        service.joinGame(request);
        return new Gson().toJson(Map.of("message", "joined game"));
    }


}
