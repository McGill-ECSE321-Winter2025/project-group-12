package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.Registration;

/**
 * Integration Tests for the {@link RegistrationRepository}.
 * Ensures {@link Registration} entities are correctly persisted and retrieved.
 * 
 * Test Scenario: Verifies the creation, persistence, and retrieval of
 * {@link Registration} entities using Spring Boot, Jakarta Persistence, and Gradle.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject the repository instances for related entities (BoardGame, UserAccount, GameOwner, etc.).
 * - Utilizes @BeforeEach and @AfterEach annotations to clear the database before and after each test to maintain a fresh state.
 * 
 * Test Cases:
 * 1. testCreateAndReadRegistration
 * 
 * Dependencies:
 * - Gradle
 * - Jakarta Persistence
 * - Spring Boot
 * 
 * Author: Yoon, Jun Ho 
 * Version: 1.0
 */

@SpringBootTest
public class RegistrationRepositoryTests {

    @Autowired
    private RegistrationRepository registrationRepo;
    
    @Autowired
    private UserAccountRepository userRepo;
    
    @Autowired
    private EventRepository eventRepo;
    
    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;
    
    @Autowired
    private BoardGameRepository boardGameRepository;
    
    @Autowired
    private GameOwnerRepository gameOwnerRepository;

     /**
     * Clears the repositories before and after each test to maintain a fresh database state.
     */

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear the repositories to ensure a fresh database state
        registrationRepo.deleteAll();
        eventRepo.deleteAll();
        userRepo.deleteAll();
        boardGameInstanceRepository.deleteAll();
        boardGameRepository.deleteAll();
        gameOwnerRepository.deleteAll();
    }

    /**
     * Tests the creation and retrieval of a {@link Registration} entity.
     *
     * Steps:
     * 1. Create a {@link UserAccount} (registrant) and save it to the repository.
     * 2. Create a {@link BoardGame} and save it.
     * 3. Create a {@link UserAccount} for the game owner and save it.
     * 4. Create a {@link GameOwner} linked to the {@link UserAccount}.
     * 5. Create and save a {@link BoardGameInstance} linked to the {@link GameOwner}.
     * 6. Create a {@link UserAccount} for the event organizer.
     * 7. Create and save an {@link Event} organized by the organizer.
     * 8. Create a {@link Registration} entity using a composite key for the registrant and event.
     * 9. Verify that the {@link Registration} entity can be retrieved from the repository.
     *
     * Assertions:
     * - The registration's composite key should reference the correct user and event.
     * - The registration date should not be null.
     */

    @Test
    public void testCreateAndReadRegistration() {
    
        //Create a user that will register
        UserAccount registrant = new UserAccount("Leon", "leon@mail.mcgill.ca", "password");
        registrant = userRepo.save(registrant);
        assertNotNull(registrant.getUserAccountId(), "UserAccount should have an ID after being saved");

        //Create board game 
        BoardGame boardGame = new BoardGame("Catan", "A popular strategy board game.");
        boardGame = boardGameRepository.save(boardGame);

        //Create user that owns a game
        UserAccount owner = new UserAccount("Leonette", "leonette@mail.mcgill.ca","password");
        owner = userRepo.save(owner);
        GameOwner gameOwner = new GameOwner(owner);
        gameOwner = gameOwnerRepository.save(gameOwner);

        //Create a board game instance with the game owner
        BoardGameInstance boardGameInstance = new BoardGameInstance(boardGame, gameOwner, "New");
        boardGameInstance = boardGameInstanceRepository.save(boardGameInstance);

      
        //Create an organizer
        UserAccount organizer = new UserAccount("Germane", "germane@mail.mcgill.ca","password");
        organizer = userRepo.save(organizer);

        // Create an event organized by the organizer
        int eventDate = 20250218;  // For simplicity, using int values (you might use Date or LocalDate in a real app)
        int eventTime = 1430;
        String location = "Montreal";
        String description = "Board game meetup";
        int maxParticipants = 10;
        Event event = new Event(eventDate, eventTime, location, description, maxParticipants, boardGameInstance, organizer);
        event = eventRepo.save(event);
        assertNotNull(event.getEventId(), "Event should have an ID after being saved");

        // 6. Create a Registration using a composite key (RegistrationKey)

        Registration.RegistrationKey key = new Registration.RegistrationKey(registrant, event);
        Registration registration = new Registration(key);
        registration = registrationRepo.save(registration);

        // 7. Retrieve the Registration using the built-in findById() method

        Optional<Registration> registrationFromDb = registrationRepo.findById(key);
        assertTrue(registrationFromDb.isPresent(), "Registration should be retrievable via findById");

        Registration retrievedRegistration = registrationFromDb.get();

        // Verify that the registration's key references the correct user and event
        assertEquals(registrant.getUserAccountId(), retrievedRegistration.getRegistrationKey().getRegistrant().getUserAccountId());
        assertEquals(event.getEventId(), retrievedRegistration.getRegistrationKey().getEvent().getEventId());
        assertEquals(organizer.getUserAccountId(),retrievedRegistration.getRegistrationKey().getEvent().getOrganizer().getUserAccountId());
        // Verify that the registration date is set
        assertNotNull(retrievedRegistration.getRegistrationDate(), "Registration date should not be null");
    }
}
