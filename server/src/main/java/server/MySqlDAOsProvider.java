package server;

import dataaccess.*;

public class MySqlDAOsProvider implements IDAOsProvider {
    public UserDAO userDAO = new MySqlUserDAO();

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public AuthDAO authDAO = new MySqlAuthDAO();

    @Override
    public GameDAO getGameDAO() {
        return gameDAO;
    }

    public GameDAO gameDAO = new MySqlGameDAO();

    public MySqlDAOsProvider() throws DataAccessException {
    }
}
