package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final UserDAO userAccess;
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userAccess = userDAO;
        this.authAccess = authDAO;
        this.gameAccess = gameDAO;
    }

    public void clearAll() {
        userAccess.clear();
        authAccess.clear();
        gameAccess.clear();
    }
}
