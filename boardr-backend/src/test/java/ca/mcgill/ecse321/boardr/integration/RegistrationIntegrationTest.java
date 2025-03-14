package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.service.EventService;
import ca.mcgill.ecse321.boardr.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ca.mcgill.ecse321.boardr.BoardrApplication.class)
@Transactional
public class RegistrationIntegrationTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    private UserAccount organizer;
    private UserAccount registrant;
    private BoardGameInstance gameInstance;
    private BoardGame boardGame;
    private GameOwner gameOwner;
    private Event event;

    @BeforeEach
    public void setUp() {
        // Clear the database
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
        boardGameInstanceRepository.deleteAll();

        // Setup board game (not persisted)
        boardGame = new BoardGame("Test Game", "A test board game");

        // Setup organizer and game owner
        organizer = userAccountRepository.save(new UserAccount("Organizer", "organizer@example.com", "password123"));
        gameOwner = new GameOwner(organizer);

        // Setup board game instance
        gameInstance = boardGameInstanceRepository.save(new BoardGameInstance(boardGame, gameOwner, "Good"));

        // Setup event
        event = eventService.createEvent(new Event(20250315, 1400, "Montreal", "A fun event", 10, gameInstance, organizer));

        // Setup registrant
        registrant = userAccountRepository.save(new UserAccount("Registrant", "registrant@example.com", "password456"));
    }

    @Test
    public void testRegisterForEvent_Success() {
        // Act
        Registration registration = registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId());

        // Assert
        assertNotNull(registration);
        assertNotNull(registration.getRegistrationKey());
        assertEquals(registrant, registration.getRegistrationKey().getRegistrant());
        assertEquals(event, registration.getRegistrationKey().getEvent());
        assertNotNull(registration.getRegistrationDate());

        // Verify in repository
        Registration retrieved = registrationRepository.findById(
            new Registration.RegistrationKey(registrant, event)).orElse(null);
        assertNotNull(retrieved);
        assertEquals(registration.getRegistrationKey(), retrieved.getRegistrationKey());
    }

    @Test
    public void testRegisterForEvent_InvalidEventId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(9999, registrant.getUserAccountId()); // Non-existent event ID
        });
        assertTrue(exception.getMessage().contains("Event not found"));
    }

    @Test
    public void testRegisterForEvent_InvalidUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(event.getEventId(), 9999); // Non-existent user ID
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    public void testRegisterForEvent_DuplicateRegistration_ThrowsException() {
        // Arrange
        registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId()); // Duplicate
        });
        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    public void testMultipleRegistrations_Success() {
        // Arrange
        UserAccount registrant2 = userAccountRepository.save(new UserAccount("Registrant2", "reg2@example.com", "pass789"));

        // Act
        Registration reg1 = registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId());
        Registration reg2 = registrationService.registerForEvent(event.getEventId(), registrant2.getUserAccountId());

        // Assert
        List<Registration> registrations = (List<Registration>) registrationRepository.findAll();
        assertEquals(2, registrations.size());
        assertTrue(registrations.stream().anyMatch(r -> r.getRegistrationKey().getRegistrant().equals(registrant)));
        assertTrue(registrations.stream().anyMatch(r -> r.getRegistrationKey().getRegistrant().equals(registrant2)));
    }
}