package server.handler;

import com.google.gson.Gson;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ClearHandler implements Route {
    private final ClearService service;

    public ClearHandler(ClearService service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) {
        service.clearAll();
        res.type("application/json");
        return new Gson().toJson(Map.of("message", "Database cleared."));
    }
}