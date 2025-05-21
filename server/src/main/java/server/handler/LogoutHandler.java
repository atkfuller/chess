package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import services.UserServices;
import model.LogoutRequest;

import java.util.Map;

public class LogoutHandler implements Route {
    private final UserServices service;

    public LogoutHandler(UserServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        LogoutRequest request = new LogoutRequest(authToken);
        service.logout(request);
        res.type("application/json");
        return new Gson().toJson(Map.of("message", "loggedOut User"));
    }
}
