package ca.mcgill.ecse321.boardr.integration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse; // Add this
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.service.EventService;

@SpringBootTest(
    classes = ca.mcgill.ecse321.boardr.BoardrApplication.class,
    properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
)
@ActiveProfiles("test")
@Transactional
public class EventIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private BoardGameRepository boardGameRepository; 

    private UserAccount organizer;
    private BoardGameInstance gameInstance;
    private BoardGame boardGame;
    private GameOwner gameOwner;

    @BeforeEach
    public void setUp() {
        eventRepository.deleteAll();
        boardGameInstanceRepository.deleteAll();
        userAccountRepository.deleteAll();
        boardGameRepository.deleteAll();

        organizer = userAccountRepository.save(new UserAccount("Name", "Email", "Password"));
        gameOwner = (GameOwner) organizer.getUserRole().stream()
            .filter(role -> role instanceof GameOwner)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("GameOwner role not found"));

        boardGame = new BoardGame("Test Game", "A test board game");
        boardGame = boardGameRepository.save(boardGame); // Persist boardGame

        gameInstance = new BoardGameInstance(boardGame, gameOwner, "Good");
        gameInstance = boardGameInstanceRepository.save(gameInstance);
    }

    @Test
    public void testCreateEvent_Success() {
        Event event = new Event(20250315, 1400, "Montreal", "A fun board game event", 10, gameInstance, organizer);
        Event savedEvent = eventService.createEvent(event);

        assertNotNull(savedEvent.getEventId());
        assertEquals(20250315, savedEvent.getEventDate());
        assertEquals(1400, savedEvent.getEventTime());
        assertEquals("Montreal", savedEvent.getLocation());
        assertEquals("A fun board game event", savedEvent.getDescription());
        assertEquals(10, savedEvent.getmaxParticipants());
        assertEquals(gameInstance, savedEvent.getboardGameInstance());
        assertEquals(organizer, savedEvent.getOrganizer());

        Event retrievedEvent = eventRepository.findById(savedEvent.getEventId()).orElse(null);
        assertNotNull(retrievedEvent);
        assertEquals(savedEvent.getEventId(), retrievedEvent.getEventId());
    }

    @Test
    public void testCreateEvent_NullOrganizer_ThrowsException() {
        Event event = new Event(20250315, 1400, "Montreal", "A fun board game event", 10, gameInstance, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(event));
        assertEquals("Organizer must be provided.", exception.getMessage());
    }

    @Test
    public void testCreateEvent_NullGameInstance_ThrowsException() {
        Event event = new Event(20250315, 1400, "Montreal", "A fun board game event", 10, null, organizer);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(event));
        assertEquals("A board game instance must be provided.", exception.getMessage());
    }

    @Test
    public void testDeleteEvent_Success() {
        Event event = new Event(20250315, 1400, "Montreal", "A fun board game event", 10, gameInstance, organizer);
        Event savedEvent = eventService.createEvent(event);

        eventService.deleteEvent(savedEvent.getEventId(), organizer.getUserAccountId());
        assertFalse(eventRepository.findById(savedEvent.getEventId()).isPresent());
    }

    @Test
    public void testDeleteEvent_WrongUser_ThrowsException() {
        UserAccount anotherUser = userAccountRepository.save(new UserAccount("anotherUser", "email", "password"));
        Event event = new Event(20250315, 1400, "Montreal", "A fun board game event", 10, gameInstance, organizer);
        Event savedEvent = eventService.createEvent(event);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            eventService.deleteEvent(savedEvent.getEventId(), anotherUser.getUserAccountId()));
        assertEquals("Only the organizer can delete the event.", exception.getMessage());
    }

    @Test
    public void testGetAllEvents_Success() {
        Event event1 = new Event(20250315, 1400, "Montreal", "Event 1", 10, gameInstance, organizer);
        Event event2 = new Event(20250316, 1500, "Toronto", "Event 2", 5, gameInstance, organizer);
        eventService.createEvent(event1);
        eventService.createEvent(event2);

        Iterable<Event> events = eventService.getAllEvents();
        List<Event> eventList = (List<Event>) events;
        assertEquals(2, eventList.size());
        assertTrue(eventList.stream().anyMatch(e -> e.getDescription().equals("Event 1")));
        assertTrue(eventList.stream().anyMatch(e -> e.getDescription().equals("Event 2")));
    }
}