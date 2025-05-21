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
        String authToken= req.headers("authorization");
        LogoutRequest request= new LogoutRequest(authToken);
        service.logout(request);
        return new Gson().toJson(Map.of("message", "loggedOut User"));
    }
    private Object listGames(Request req, Response res) throws DataAccessException{
        String authToken= req.headers("authorization");
        ListGameRequest request=new ListGameRequest(authToken);
        ListGameResult result= service.listGame(request);
        return new Gson().toJson(result);
    }
    private Object createGame(Request req, Response res) throws DataAccessException{
        String authToken= req.headers("authorization");
        CreateGameRequest reqRequest= new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameRequest request= new CreateGameRequest(authToken, reqRequest.gameName());
        createGameResult result= service.createGame(request);
        return new Gson().toJson(result);
    }
    private Object joinGame(Request req, Response res) throws DataAccessException{
        String authToken= req.headers("authorization");
        JoinGameRequest reqRequest= new Gson().fromJson(req.body(), JoinGameRequest.class);
        JoinGameRequest request= new JoinGameRequest(authToken,reqRequest.playerColor(),reqRequest.gameID());
        service.joinGame(request);
        return new Gson().toJson(Map.of("message", "joined game"));
    }


}
