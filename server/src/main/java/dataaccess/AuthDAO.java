package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear();

    void createAuth(AuthData autho);

    AuthData getAuth(String authToken);

    AuthData getAuthByUsername(String username);

    void deleteAuth(AuthData aData);
}
