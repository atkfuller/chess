package services;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.UserData;

import javax.xml.crypto.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class servicesTest {
        static final UserServices service = new UserServices();
        static UserDAO accessUser;
        static AuthDAO accessAuth;
        static GameDAO accessGame;
        static ArrayList<GameData> allGames;
        @BeforeEach
        void clear() throws DataAccessException {
            service.clear();
        }
        /*test to implement
            create game
            create game unauthorized
            create game bad request
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
            accessGame.addGame(new GameData(1, "alice", "bob", "Classic Match", new ChessGame()));
            accessGame.addGame(new GameData(2, "charlie", "diana", "Opening Practice", new ChessGame()));
            accessGame.addGame(new GameData(3, "edward", "fiona", "Blitz Showdown", new ChessGame()));
            accessGame.addGame(new GameData(4, "george", "harriet", "Endgame Tactics", new ChessGame()));
            accessGame.addGame(new GameData(5, "ivan", "julia", "Queen's Gambit", new ChessGame()));
            accessGame.addGame(new GameData(6, "kevin", "laura", "King's Defense", new ChessGame()));
            accessGame.addGame(new GameData(7, "maria", "nathan", "Rook Battle", new ChessGame()));
            accessGame.addGame(new GameData(8, "oliver", "paula", "Pawn Storm", new ChessGame()));
            accessGame.addGame(new GameData(9, "quentin", "rachel", "Checkmate Drill", new ChessGame()));
            accessGame.addGame(new GameData(10, "sam", "tina", "Training Match", new ChessGame()));
            allGames=accessGame.listGames();

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
        ArrayList<AuthData> auth = service.getAuth();
        ArrayList<UserData> users = service.getUsers();
        UserData user = users.get(4);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        service.logout(new LogoutRequest(data.authToken()));
        assertFalse(auth.contains(data));

    }
    @Test
    void logoutWrong() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users= service.getUsers();
        UserData user= users.get(4);
        AuthData data=accessAuth.getAuthByUsername(user.username());
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.logout(new LogoutRequest("wrongtoken"));
        });

        assertEquals("Error: unauthorized", ex.getMessage());
    }
    @Test
    void listGameCorrect() throws DataAccessException{
        populateUsers();
        ArrayList< AuthData> auth= service.getAuth();
        ArrayList<UserData> users= service.getUsers();
        UserData user= users.get(5);
        AuthData data=accessAuth.getAuthByUsername(user.username());
        ListGameResult list=service.listGame(new ListGameRequest(data.authToken()));
        assertEquals(list.games(), allGames);

    }
    @Test
    void listGameWrong() throws DataAccessException{
        populateUsers();
        ArrayList< AuthData> auth= service.getAuth();
        ArrayList<UserData> users= service.getUsers();
        UserData user= users.get(5);
        AuthData data=accessAuth.getAuthByUsername(user.username());
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.listGame(new ListGameRequest("wrong token"));
        });

        assertEquals("Error: unauthorized", ex.getMessage());

    }
    @Test
    void createGameCorrect() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = service.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        CreateGameRequest request = new CreateGameRequest(data.authToken(), "myGame");
        createGameResult result=service.createGame(request);
        ArrayList<GameData> games= service.getGames();
        for(GameData gData: games){
            if(gData.gameName()=="myGame"){
                assertSame("myGame", gData.gameName());
            }
        }
    }
    void createGameBad() throws DataAccessException{
        populateUsers();
        ArrayList<UserData> users = service.getUsers();
        UserData user = users.get(2);
        AuthData data = accessAuth.getAuthByUsername(user.username());
        CreateGameRequest request = new CreateGameRequest(data.authToken(), null);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.createGame(request);
        });

        assertEquals("Error: bad request", ex.getMessage());

    }
    void createGameUnAuthorized() throws DataAccessException{
        populateUsers();
        CreateGameRequest request = new CreateGameRequest("bad token", "myGame");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            service.createGame(request);
        });

        assertEquals("Error: unauthorized", ex.getMessage());

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
