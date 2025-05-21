package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import services.UserServices;
import model.ListGameRequest;
import model.ListGameResult;

public class ListGamesHandler implements Route {
    private final UserServices service;

    public ListGamesHandler(UserServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        ListGameRequest request = new ListGameRequest(authToken);
        ListGameResult result = service.listGame(request);
        res.type("application/json");
        return new Gson().toJson(result);
    }
}

