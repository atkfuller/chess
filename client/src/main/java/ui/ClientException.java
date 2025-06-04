package ui;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class ClientException extends Exception{
    final private int statusCode;
    public ClientException(int code, String message) {
        super(message);
        this.statusCode=code;
    }
    public ClientException(String message, Throwable ex, int statusCode) {
        super(message, ex);
        this.statusCode = statusCode;
    }
    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ClientException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return new ClientException(status, message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
