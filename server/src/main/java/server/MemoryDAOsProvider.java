package server;

import dataaccess.*;
import service.ClearService;
import service.GameServices;
import service.UserServices;

public class MemoryDAOsProvider implements IDAOsProvider{
    public UserDAO userDAO = new MemoryUserDAO();

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public AuthDAO authDAO = new MemoryAuthDAO();
    public GameDAO gameDAO = new MemoryGameDAO();

    public GameDAO getGameDAO() {
        return gameDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }
}
