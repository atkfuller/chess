package services;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.UserData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class servicesTest {
        static final UserServices service = new UserServices();
        /*
        @BeforeEach
        void clear() throws DataAccessException {
            service.deleteAllPets();
        }
        */
        @Test
        void registerPet() throws DataAccessException {
            UserData user=new UserData("atfuller", "teddy","good@gmail.com");
            RegisterRequest req= new RegisterRequest("atfuller", "teddy","good@gmail.com" );
            RegisterResult  res= service.register(req);
            var users = service.getUsers();
            var auth=service.getAuth();
            assertEquals(1, users.size());
            assertEquals(1, auth.size());
            assertTrue(users.contains(user));
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
