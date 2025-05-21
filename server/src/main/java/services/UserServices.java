package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import model.AuthData;
import model.UserData;
import java.util.ArrayList;
import java.util.HashMap;
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
    public LoginResult login(LoginRequest request)throws DataAccessException{
        if(request.username()==null| request.password()==null){
            throw new DataAccessException(400, "Error: bad request");
        }
        UserData user=userAccess.getUser(request.username());
        if(user==null|!user.checkPassword(request.password())){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        String token=generateToken();
        AuthData auth= new AuthData(token, request.username());
        authAccess.createAuth(auth);
        return new LoginResult(request.username(), token);
    }
    public void logout(LogoutRequest request) throws DataAccessException{
        AuthData data=authAccess.getAuth(request.authToken());
        if(data==null){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        authAccess.deleteAuth(data);
    }
    public void clear(){
        userAccess.clear();
        authAccess.clear();
        gameAccess.clear();
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

    public UserDAO getUserAccess() {
        return userAccess;
    }

    public AuthDAO getAuthAccess() {
        return authAccess;
    }

    public GameDAO getGameAccess() {
        return gameAccess;
    }
    //public LoginResult login(LoginRequest loginRequest) {}
    //public void logout(LogoutRequest logoutRequest) {}
}
