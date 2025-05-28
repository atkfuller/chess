package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public interface IDAOsProvider {
    UserDAO getUserDAO();

    AuthDAO getAuthDAO();

    GameDAO getGameDAO();
}
