package service;

import dataaccess.*;

import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class UserServices {
    private final UserDAO userAccess;
    private final AuthDAO authAccess;;
    private final GameDAO gameAccess;
    public UserServices(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userAccess = userDAO;
        this.authAccess = authDAO;
        this.gameAccess = gameDAO;
    }
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest.username()==null| registerRequest.password()==null|registerRequest.email()==null){
            throw new DataAccessException(400, "Error: bad request");
        }
        UserData user=userAccess.getUser(registerRequest.username());
        if(user!=null){
            throw new DataAccessException(403, "Error: already taken username");
        }
        else{
            String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
            user= new UserData(registerRequest.username(), hashedPassword, registerRequest.email());
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
        if(user==null){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        else if(!checkPassword(user, request.password())){
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
    public boolean checkPassword(UserData user, String enteredPassword){
            return BCrypt.checkpw(enteredPassword,  user.hashedPasword());
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

    public MemoryUserDAO getUserAccess() {
        return userAccess;
    }

    public MemoryAuthDAO getAuthAccess() {
        return authAccess;
    }



}
