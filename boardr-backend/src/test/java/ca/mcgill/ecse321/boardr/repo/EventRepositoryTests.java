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
        assertEquals(retrievedEvent.getboardGameInstance().getindividualGameId(),boardGameInstance.getindividualGameId());
        assertEquals(organizer.getUserAccountId(), retrievedEvent.getOrganizer().getUserAccountId());

    }

}