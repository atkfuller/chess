package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import services.UserServices;
import model.JoinGameRequest;

import java.util.Map;

public class JoinGameHandler implements Route {
    private final UserServices service;

    public JoinGameHandler(UserServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        JoinGameRequest reqRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        JoinGameRequest request = new JoinGameRequest(authToken, reqRequest.playerColor(), reqRequest.gameID());
        service.joinGame(request);
        res.type("application/json");
        return new Gson().toJson(Map.of("message", "joined game"));
    }
}