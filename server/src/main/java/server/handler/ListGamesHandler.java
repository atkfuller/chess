package server.handler;

import com.google.gson.Gson;
import service.GameServices;
import spark.Request;
import spark.Response;
import spark.Route;
import model.ListGameRequest;
import model.ListGameResult;

public class ListGamesHandler implements Route {
    private final GameServices service;

    public ListGamesHandler(GameServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        ListGameRequest request = new ListGameRequest(authToken);
        ListGameResult result = service.listGames(request);
        res.type("application/json");
        return new Gson().toJson(result);
    }
}

