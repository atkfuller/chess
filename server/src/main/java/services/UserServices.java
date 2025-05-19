package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

import java.AuthData;
import java.UserData;
import java.util.UUID;

public class UserServices {
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserDAO dao = null;
        UserData user= dao.getUser(registerRequest.username());
        if(user!=null){
            throw DataAccessException;//should be a alreadytaken exception
        }
        else{
            user= new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            dao.createUser(user);
            AuthData auth= new AuthData(generateToken(), registerRequest.username());
            AuthDAO aDao=null;
            aDao.createAuth(auth);
            RegisterResult result= new RegisterResult(registerRequest.username(), auth.authToken());
            return result;
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    //public LoginResult login(LoginRequest loginRequest) {}
    //public void logout(LogoutRequest logoutRequest) {}
}
