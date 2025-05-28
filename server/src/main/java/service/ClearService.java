package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class ClearService {
    private final MemoryUserDAO userAccess;
    private final MemoryAuthDAO authAccess;
    private final MemoryGameDAO gameAccess;

    public ClearService(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO) {
        this.userAccess = memoryUserDAO;
        this.authAccess = memoryAuthDAO;
        this.gameAccess = memoryGameDAO;
    }

    public void clearAll() {
        userAccess.clear();
        authAccess.clear();
        gameAccess.clear();
    }
}
