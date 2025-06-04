package server;
import dataaccess.DataAccessException;

import com.google.gson.Gson;
import model.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws DataAccessException {
        var path="/db";
        this.makeRequest("DELETE", path, null, null);
    }
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        var path="/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }
    public LoginResult login(LoginRequest request) throws DataAccessException {
        var path="/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }
    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        var path="/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
    }
    public ListGameResult listGames(ListGameRequest request) throws DataAccessException {
        var path="/game";
        return  this.makeRequest("GET", path, request, ListGameResult.class);
    }
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        var path="/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class);
    }
    public void joinGame(JoinGameRequest request) throws DataAccessException {
        var path= "/game";
        this.makeRequest("PUT", path, request, null);
    }
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        return this.makeRequestAuth(method, path, null, request, responseClass);
    }
    private <T> T makeRequestAuth(String method, String path, String authToken, Object request, Class<T> responseClass) throws DataAccessException {
        System.out.println("Connecting to: " + serverUrl + path);
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if(authToken!=null) {
                http.addRequestProperty("Authorization", authToken);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException{
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw DataAccessException.fromJson(respErr);
                }
            }

            throw new DataAccessException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}


