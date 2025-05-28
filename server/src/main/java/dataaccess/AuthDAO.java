package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {
    void clear() throws DataAccessException;

    void createAuth(AuthData autho) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    AuthData getAuthByUsername(String username) throws DataAccessException;

    void deleteAuth(AuthData aData) throws DataAccessException ;
    public ArrayList<AuthData> getAuthencation();
}
