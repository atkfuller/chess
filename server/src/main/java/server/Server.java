package server;

import dataaccess.DataAccessException;
import com.google.gson.Gson;
import server.handler.*;
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
        Spark.post("/user", new RegisterHandler(service));
        Spark.delete("/db", new ClearHandler(service));
        Spark.post("/session", new LoginHandler(service));
        Spark.delete("/session", new LogoutHandler(service));
        Spark.get("/game", new ListGamesHandler(service));
        Spark.post("/game", new CreateGameHandler(service));
        Spark.put("/game", new JoinGameHandler(service));


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
