package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.Pet;
import model.PetType;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import server.IDAOsProvider;
import server.MemoryDAOsProvider;
import server.MySqlDAOsProvider;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    private IDAOsProvider getDataAccess(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException {
        IDAOsProvider db;
        if (databaseClass.equals(MySqlDAOsProvider.class)) {
            db = new MySqlDAOsProvider();
        } else {
            db = new MemoryDAOsProvider();
        }
        //db.deleteAllPets();
        return db;
    }
    private UserDAO getUserDAO(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException{
        IDAOsProvider provider=getDataAccess(databaseClass);
        return provider.getUserDAO();
    }
    private AuthDAO getAuthDAO(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException{
        IDAOsProvider provider=getDataAccess(databaseClass);
        return provider.getAuthDAO();
    }
    private GameDAO getGameDAO(Class<? extends IDAOsProvider> databaseClass) throws DataAccessException{
        IDAOsProvider provider=getDataAccess(databaseClass);
        return provider.getGameDAO();
    }


    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class, MemoryDAOsProvider.class})
    void addUser(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        UserDAO dataAccess = getUserDAO(dbClass);
        var user = new UserData("hello", "joe", "email@email.com";
        assertDoesNotThrow(() -> dataAccess.createUser(user));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class, MemoryDAOsProvider.class})
    void deleteAllUsers(Class<? extends IDAOsProvider> dbClass) throws Exception {
         UserDAO dataAccess = getUserDAO(dbClass);
        dataAccess.createUser(new UserData('1', "joe", "123@m"));
        dataAccess.createUser(new UserData('2', "sally", "456@m"));

        dataAccess.clear();

        var actual = dataAccess.getUsers();
        assertEquals(0, actual.size());
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class, MemoryDAOsProvider.class})
    void addAuth(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDAO(dbClass);
        var auth = new AuthData("token", "myuser");
        assertDoesNotThrow(() -> dataAccess.createAuth(auth));
    }
    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class, MemoryDAOsProvider.class})
    void listPets(Class<? extends IDAOsProvider> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDAO(dbClass);

        List<GameData> expected = new ArrayList<>();
        expected.add(dataAccess.addPet(new Pet(0, "joe", PetType.FISH)));
        expected.add(dataAccess.addPet(new Pet(0, "sally", PetType.CAT)));
        expected.add(dataAccess.addPet(new Pet(0, "fido", PetType.DOG)));

        var actual = dataAccess.listPets();
        assertPetCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class, MemoryDAOsProvider.class})
    void deletePet(Class<? extends DataAccess> dbClass) throws ResponseException {
        DataAccess dataAccess = getDataAccess(dbClass);

        List<Pet> expected = new ArrayList<>();
        var deletePet = dataAccess.addPet(new Pet(0, "joe", PetType.FISH));
        expected.add(dataAccess.addPet(new Pet(0, "sally", PetType.CAT)));
        expected.add(dataAccess.addPet(new Pet(0, "fido", PetType.DOG)));

        dataAccess.deletePet(deletePet.id());

        var actual = dataAccess.listPets();
        assertPetCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDAOsProvider.class, MemoryDAOsProvider.class})
    void deleteAllPets(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.addPet(new Pet(0, "joe", PetType.FISH));
        dataAccess.addPet(new Pet(0, "sally", PetType.CAT));

        dataAccess.deleteAllPets();

        var actual = dataAccess.listPets();
        assertEquals(0, actual.size());
    }


    public static void assertPetEqual(Pet expected, Pet actual) {
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.type(), actual.type());
    }

    public static void assertPetCollectionEqual(Collection<Pet> expected, Collection<Pet> actual) {
        Pet[] actualList = actual.toArray(new Pet[]{});
        Pet[] expectedList = expected.toArray(new Pet[]{});
        assertEquals(expectedList.length, actualList.length);
        for (var i = 0; i < actualList.length; i++) {
            assertPetEqual(expectedList[i], actualList[i]);
        }
    }
}
