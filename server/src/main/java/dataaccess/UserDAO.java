package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear();

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;
}
