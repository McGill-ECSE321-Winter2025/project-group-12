package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
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

        // Create a Registration using a composite key (RegistrationKey)
        Registration.RegistrationKey key = new Registration.RegistrationKey(registrant, event);
        Registration registration = new Registration(key);
        registration = registrationRepo.save(registration);

        Optional<Registration> registrationFromDb = registrationRepo.findById(key);
        assertTrue(registrationFromDb.isPresent(), "Registration should be retrievable via findById");

        Registration retrievedRegistration = registrationFromDb.get();
        // Verify/read that the registration's key references the correct user and event
        assertEquals(registrant.getUserAccountId(), retrievedRegistration.getRegistrationKey().getRegistrant().getUserAccountId());
        assertEquals(event.getEventId(), retrievedRegistration.getRegistrationKey().getEvent().getEventId());
        assertEquals(organizer.getUserAccountId(),retrievedRegistration.getRegistrationKey().getEvent().getOrganizer().getUserAccountId());
        // Verify/read that the registration date is set
        assertNotNull(retrievedRegistration.getRegistrationDate(), "Registration date should not be null");
    }
}
