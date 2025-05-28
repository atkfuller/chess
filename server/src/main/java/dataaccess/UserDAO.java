package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    void clear() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;
    public ArrayList<UserData> getUsers() throws DataAccessException;
    }

