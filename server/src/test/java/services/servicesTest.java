package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.UserData;

import javax.xml.crypto.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class servicesTest {
        static final UserServices service = new UserServices();
        static UserDAO accessUser;
        static AuthDAO accessAuth;
        static GameDAO accessGame;
        @BeforeEach
        void clear() throws DataAccessException {
            service.clear();
        }
        /*test to implement
            logout
            logout unauthroized
            list games
            list games unauthroized
            create game
            create game unauthorized
            join game
            join game unauthroized
         */
        @Test
        void registerU() throws DataAccessException {
            UserData user=new UserData("atfuller", "teddy","good@gmail.com");
            RegisterRequest req= new RegisterRequest("atfuller", "teddy","good@gmail.com" );
            RegisterResult  res= service.register(req);
            var users = service.getUsers();
            var auth=service.getAuth();
            assertEquals(1, users.size());
            assertEquals(1, auth.size());
            assertTrue(users.contains(user));
        }
        void populateUsers() throws DataAccessException{
            RegisterResult  res= service.register(new RegisterRequest("atfuller", "teddy","good@gmail.com" ));
            res = service.register(new RegisterRequest("jdoe", "secure456", "john@example.com"));
            res = service.register(new RegisterRequest("msmith", "pass789", "mary@example.org"));
            res = service.register(new RegisterRequest("knguyen", "dragon2024", "khoa@example.net"));
            res = service.register(new RegisterRequest("aluna", "moonlight", "aluna@example.com"));
            res = service.register(new RegisterRequest("rpatel", "india@321", "raj@example.in"));
            res = service.register(new RegisterRequest("cjones", "purple!car", "carl@example.biz"));
            res = service.register(new RegisterRequest("zli", "123Abc!", "zhen@example.cn"));
            res = service.register(new RegisterRequest("sanders", "qwerty007", "sandy@example.co"));
            res = service.register(new RegisterRequest("lwilson", "wilson@pass", "laura@example.us"));
            accessUser=service.getUserAccess();
            accessAuth=service.getAuthAccess();
            accessGame=service.getGameAccess();
        }
        @Test
        void alreadyTaken() throws DataAccessException{
            populateUsers();
            DataAccessException ex = assertThrows(DataAccessException.class, () -> {
                service.register(new RegisterRequest("jdoe","123", "wrong"));
            });

            assertEquals("already taken username", ex.getMessage());
        }

    @Test
    void loginUser() throws DataAccessException{
        populateUsers();
        ArrayList<AuthData> auth= service.getAuth();
        UserData user= new UserData("msmith", "pass789", "mary@example.org");
        AuthData author= accessAuth.getAuthByUsername("msmith");
        LoginResult res= service.login(new LoginRequest("msmith", "pass789"));
        assertNotEquals(author.authToken(), res.authToken());
    }
    @Test
    void loginWrongPassword() throws DataAccessException{
        populateUsers();
        ArrayList<AuthData> auth= service.getAuth();
        UserData user= new UserData("msmith", "pass789", "mary@example.org");
        AuthData author= new AuthDAO().getAuthByUsername("msmith");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.login(new LoginRequest("msmith", "pass8"));
        });

        assertEquals("Error: unauthorized", ex.getMessage());
    }
    @Test
    void logoutCorrect() throws DataAccessException{
            populateUsers();
        ArrayList< AuthData> auth= service.getAuth();
        ArrayList<UserData> users= service.getUsers();
        UserData user= users.get(4);
        AuthDAO access= new AuthDAO();
        AuthData data=access.getAuthByUsername(user.username());
        service.logout(new LogoutRequest(data.authToken()));
        assertFalse(auth.contains(data));

    }


        /*
        @Test
        void listPets() throws ResponseException {
            List<Pet> expected = new ArrayList<>();
            expected.add(service.addPet(new Pet(0, "joe", PetType.FISH)));
            expected.add(service.addPet(new Pet(0, "sally", PetType.CAT)));
            expected.add(service.addPet(new Pet(0, "fido", PetType.DOG)));

            var actual = service.listPets();
            assertIterableEquals(expected, actual);
        }

        @Test
        void deletePet() throws ResponseException {
            List<Pet> expected = new ArrayList<>();
            var pet = service.addPet(new Pet(0, "joe", PetType.FISH));
            expected.add(service.addPet(new Pet(0, "sally", PetType.CAT)));
            expected.add(service.addPet(new Pet(0, "fido", PetType.DOG)));

            service.deletePet(pet.id());
            var actual = service.listPets();
            assertIterableEquals(expected, actual);
        }

        @Test
        void deleteAllPets() throws ResponseException {
            service.addPet(new Pet(0, "joe", PetType.FISH));
            service.addPet(new Pet(0, "sally", PetType.CAT));
            service.addPet(new Pet(0, "fido", PetType.DOG));

            service.deleteAllPets();
            assertEquals(0, service.listPets().size());
        }

        @Test
        void noDogsWithFleas() {
            assertThrows(ResponseException.class, () ->
                    service.addPet(new Pet(0, "fleas", PetType.DOG)));
        }
    */
}
