package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import services.UserServices;

import java.util.Map;

public class ClearHandler implements Route {
    private final UserServices service;

    public ClearHandler(UserServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) {
        service.clear();
        res.type("application/json");
        return new Gson().toJson(Map.of("message", "Database cleared."));
    }
}