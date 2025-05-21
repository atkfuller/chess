package server.handler;

import com.google.gson.Gson;
import service.GameServices;
import spark.Request;
import spark.Response;
import spark.Route;
import model.CreateGameRequest;
import model.CreateGameResult;

public class CreateGameHandler implements Route {
    private final GameServices service;

    public CreateGameHandler(GameServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        CreateGameRequest reqRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameRequest request = new CreateGameRequest(authToken, reqRequest.gameName());
        CreateGameResult result = service.createGame(request);
        res.type("application/json");
        return new Gson().toJson(result);
    }
}