package ca.mcgill.ecse321.boardr.integration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.Registration;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
import ca.mcgill.ecse321.boardr.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.service.EventService;
import ca.mcgill.ecse321.boardr.service.RegistrationService;

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

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private GameOwnerRepository gameOwnerRepository; 

    private UserAccount organizer;
    private UserAccount registrant;
    private BoardGameInstance gameInstance;
    private BoardGame boardGame;
    private GameOwner gameOwner;
    private Event event;

    @BeforeEach
    public void setUp() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
        userAccountRepository.deleteAll();
        boardGameInstanceRepository.deleteAll();
        boardGameRepository.deleteAll();
        gameOwnerRepository.deleteAll(); // Clear GameOwner table

        boardGame = new BoardGame("Test Game", "A test board game");
        boardGame = boardGameRepository.save(boardGame);

        organizer = userAccountRepository.save(new UserAccount("Organizer", "organizer@example.com", "password123"));
        gameOwner = new GameOwner(organizer);
        gameOwner = gameOwnerRepository.save(gameOwner); // Persist GameOwner

        gameInstance = boardGameInstanceRepository.save(new BoardGameInstance(boardGame, gameOwner, "Good"));

        event = eventService.createEvent(new Event(20250315, 1400, "Montreal", "A fun event", 10, gameInstance, organizer));

        registrant = userAccountRepository.save(new UserAccount("Registrant", "registrant@example.com", "password456"));
    }

    @Test
    public void testRegisterForEvent_Success() {
        Registration registration = registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId());
        assertNotNull(registration);
        assertNotNull(registration.getRegistrationKey());
        assertEquals(registrant, registration.getRegistrationKey().getRegistrant());
        assertEquals(event, registration.getRegistrationKey().getEvent());
        assertNotNull(registration.getRegistrationDate());

        Registration retrieved = registrationRepository.findById(
            new Registration.RegistrationKey(registrant, event)).orElse(null);
        assertNotNull(retrieved);
        assertEquals(registration.getRegistrationKey(), retrieved.getRegistrationKey());
    }

    @Test
    public void testRegisterForEvent_InvalidEventId_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(9999, registrant.getUserAccountId());
        });
        assertTrue(exception.getMessage().contains("Event not found"));
    }

    @Test
    public void testRegisterForEvent_InvalidUserId_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(event.getEventId(), 9999);
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    public void testRegisterForEvent_DuplicateRegistration_ThrowsException() {
        registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId());
        });
        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    public void testMultipleRegistrations_Success() {
        UserAccount registrant2 = userAccountRepository.save(new UserAccount("Registrant2", "reg2@example.com", "pass789"));
        Registration reg1 = registrationService.registerForEvent(event.getEventId(), registrant.getUserAccountId());
        Registration reg2 = registrationService.registerForEvent(event.getEventId(), registrant2.getUserAccountId());

        List<Registration> registrations = (List<Registration>) registrationRepository.findAll();
        assertEquals(2, registrations.size());
        assertTrue(registrations.stream().anyMatch(r -> r.getRegistrationKey().getRegistrant().equals(registrant)));
        assertTrue(registrations.stream().anyMatch(r -> r.getRegistrationKey().getRegistrant().equals(registrant2)));
    }
}