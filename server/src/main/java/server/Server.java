package server;

import dataaccess.DataAccessException;
import com.google.gson.Gson;
import services.RegisterRequest;
import services.RegisterResult;
import services.UserServices;
import spark.*;

import javax.xml.crypto.Data;
import model.UserData;

public class Server {
    private final UserServices service;
    public Server(){
        service=null;
    }
    public Server(UserServices serv){
        this.service=serv;
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.post("/register", this::registerUser);
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

}
