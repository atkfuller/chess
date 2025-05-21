package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import services.UserServices;
import model.RegisterRequest;
import model.RegisterResult;

public class RegisterHandler implements Route {
    private final UserServices service;

    public RegisterHandler(UserServices service) {
        this.service = service;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = service.register(registerRequest);
        res.type("application/json");
        return new Gson().toJson(result);
    }
}
