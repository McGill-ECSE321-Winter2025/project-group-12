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

/**
 * Integration Tests for the {@link EventRepository}.
 * Ensures {@link Event} entities are correctly persisted and retrieved.
 * 
 * Test Scenario: Verifies the creation, persistence, and retrieval of
 * {@link Event} entities using Spring Boot, Jakarta Persistence, and Gradle.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject the repository instances for related entities (BoardGame, UserAccount, GameOwner, etc.).
 * - Utilizes @BeforeEach and @AfterEach annotations to clear the database before and after each test to maintain a fresh state.
 * 
 * Test Cases:
 * 1. testCreateAndReadEvent
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
public class EventRepositoryTests {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    /**
     * Clears the database before and after each test to maintain a fresh state.
     */

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clean up all repositories to ensure a fresh state for each test
        eventRepository.deleteAll();
        boardGameInstanceRepository.deleteAll();
        userAccountRepository.deleteAll();
        boardGameRepository.deleteAll();
        gameOwnerRepository.deleteAll();
    }

    /**
     * Tests the creation and retrieval of an {@link Event} entity.
     *
     * Steps:
     * 1. Create a {@link BoardGame} and save it to the repository.
     * 2. Create a {@link UserAccount} for the event organizer and save it.
     * 3. Create a {@link GameOwner} linked to the {@link UserAccount}.
     * 4. Create a {@link BoardGameInstance} linked to the {@link GameOwner}.
     * 5. Create and save a new {@link Event} for the {@link BoardGameInstance}.
     * 6. Retrieve and verify that all attributes match the expected values.
     *
     * Assertions:
     * - {@link Event} ID should not be null after being saved.
     * - {@link Event} should be retrievable from the repository.
     * - The event date, time, location, description, and max participants should match the expected values.
     * - The references to {@link BoardGameInstance} and {@link UserAccount} (organizer) should not be null.
     * - The IDs for the {@link BoardGameInstance} and {@link UserAccount} (organizer) should match the saved values.
     */

    @Test
    public void testCreateAndReadEvent() {
        // Create and persist a BoardGame
        BoardGame boardGame = new BoardGame("Catan", "A popular strategy board game.");
        boardGame = boardGameRepository.save(boardGame);
        
        UserAccount userAccount = new UserAccount("Mat","mat@mail.mcgill.ca","password");
        userAccount = userAccountRepository.save(userAccount);
        // Create and persist a GameOwner for the BoardGameInstance
        GameOwner owner = new GameOwner(userAccount);
        owner = gameOwnerRepository.save(owner);
        
        // Create and persist a BoardGameInstance using the BoardGame and GameOwner
        BoardGameInstance boardGameInstance = new BoardGameInstance(boardGame, owner, "New");
        boardGameInstance = boardGameInstanceRepository.save(boardGameInstance);
        
        // Create and persist a UserAccount to act as the Event organizer
        UserAccount organizer = new UserAccount("Matty", "matty@mail.mcgill.ca", "password123");
        organizer = userAccountRepository.save(organizer);
        
        // Create and persist an Event with attributes and references
        int eventDate = 20250218;  
        int eventTime = 1430;       
        String location = "Montreal";
        String description = "Board game meetup";
        int maxParticipants = 10;
        
        Event event = new Event(eventDate, eventTime, location, description, maxParticipants, boardGameInstance, organizer);
        event = eventRepository.save(event);
        
        // Retrieve the Event by its ID and verify attributes and references
        Optional<Event> eventFromDb = eventRepository.findById(event.getEventId());
        assertTrue(eventFromDb.isPresent(), "Event should be present in the repository");
        
        Event retrievedEvent = eventFromDb.get();

        // Verify basic attributes

        assertEquals(eventDate, retrievedEvent.getEventDate());
        assertEquals(eventTime, retrievedEvent.getEventTime());
        assertEquals(location, retrievedEvent.getLocation());
        assertEquals(description, retrievedEvent.getDescription());
        assertEquals(maxParticipants, retrievedEvent.getmaxParticipants());

        // Verify references

        assertNotNull(retrievedEvent.getboardGameInstance(), "BoardGameInstance should not be null");
        assertNotNull(retrievedEvent.getOrganizer(), "Organizer should not be null");
        assertEquals("New", retrievedEvent.getboardGameInstance().getCondition());
        assertEquals(retrievedEvent.getboardGameInstance().getIndividualGameId(),boardGameInstance.getIndividualGameId());
        assertEquals(organizer.getUserAccountId(), retrievedEvent.getOrganizer().getUserAccountId());

    }

}