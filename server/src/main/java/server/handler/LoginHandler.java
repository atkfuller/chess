package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import services.UserServices;
import model.LoginRequest;
import model.LoginResult;

public class LoginHandler implements Route {
    private final UserServices service;

    public LoginHandler(UserServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result = service.login(loginRequest);
        res.type("application/json");
        return new Gson().toJson(result);
    }
}
