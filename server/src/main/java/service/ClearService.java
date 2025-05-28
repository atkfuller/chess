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

    public void clearAll() throws DataAccessException {
        userAccess.clear();
        authAccess.clear();
        gameAccess.clear();
    }
}
