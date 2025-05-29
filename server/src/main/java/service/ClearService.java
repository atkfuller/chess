package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO userAccess;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;

    public ClearService(UserDAO user, AuthDAO auth, GameDAO rename) {
        this.userAccess = user;
        this.authAccess = auth;
        this.gameAccess = rename;

    }

    public void clearAll(){
        try {
            userAccess.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            authAccess.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            gameAccess.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
