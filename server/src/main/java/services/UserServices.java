package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import model.AuthData;
import model.UserData;
import java.util.ArrayList;
import java.util.UUID;

public class UserServices {
    private final UserDAO userAccess;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    public UserServices(){
        userAccess= new UserDAO();
        authAccess= new AuthDAO();
        gameAccess= new GameDAO();
    }
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
       UserData user=userAccess.getUser(registerRequest.username());
        if(user!=null){
            throw new DataAccessException(403, "already taken username");
        }
        else{
            user= new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userAccess.createUser(user);
            AuthData auth= new AuthData(generateToken(), registerRequest.username());
            authAccess.createAuth(auth);
            return new RegisterResult(registerRequest.username(), auth.authToken());
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public ArrayList<UserData> getUsers(){
        return userAccess.getUsers();
    }
    public ArrayList<AuthData> getAuth(){
        return authAccess.getAuthencation();
    }
    //public LoginResult login(LoginRequest loginRequest) {}
    //public void logout(LogoutRequest logoutRequest) {}
}
