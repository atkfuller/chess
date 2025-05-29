package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO userAccess;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;

    public ClearService(UserDAO UserDAO, AuthDAO AuthDAO, GameDAO GameDAO) {
        this.userAccess = UserDAO;
        this.authAccess = AuthDAO;
        this.gameAccess = GameDAO;
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
