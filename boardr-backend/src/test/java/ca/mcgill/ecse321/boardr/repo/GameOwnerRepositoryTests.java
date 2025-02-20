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
 * Integration Tests for the {@link GameOwnerRepository}.
 * Ensures persistence and retrieval of {@link GameOwner} entities.
 * 
 * Test Scenario: Verifies creation, persistence, and retrieval of 
 * {@link GameOwner} entities using Spring Boot, Jakarta Persistence, and Gradle.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject repository instances.
 * - Utilizes the @BeforeEach and @AfterEach annotations to clear the database before and after each test.
 * 
 * Test Cases:
 * 1. testCreateAndReadGameOwner
 * 
 * Dependencies:
 * - Gradle
 * - Jakarta Persistence
 * - Spring Boot
 * 
 * Author: Yoon, Kyujin, Jun Ho
 * Version: 1.0
 */

@SpringBootTest
public class GameOwnerRepositoryTests {

    @Autowired
    private EventRepository eventRepo;
    
    @Autowired
    private BoardGameRepository boardGameRepo;
    
    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepo;
    
    @Autowired
    private UserAccountRepository userAccountRepo;
    
    @Autowired
    private GameOwnerRepository gameOwnerRepo;
    
    /**
     * Clears all repositories before and after each test.
     */

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear all repositories to ensure a fresh database state
        eventRepo.deleteAll();
        boardGameInstanceRepo.deleteAll();
        userAccountRepo.deleteAll();
        boardGameRepo.deleteAll();
        gameOwnerRepo.deleteAll();
    }
    
    /**
     * Tests the creation and retrieval of a {@link GameOwner}.
     * 
     * Steps:
     * 1. Creates and saves a {@link UserAccount}.
     * 2. Creates a {@link GameOwner} and associates it with the created {@link UserAccount}.
     * 3. Retrieves the {@link GameOwner} from the repository.
     * 4. Verifies that the {@link GameOwner} attributes match expected values.
     * 
     * Assertions:
     * - GameOwner should be present in the repository.
     * - The GameOwner's ID and role should match.
     */

    @Test
    public void testCreateAndReadEvent() {
        //Create and save a BoardGame
        
        
        //Create user account for the game owner
        UserAccount userAccount = new UserAccount("Bobette", "bobette@mail.mcgill.ca.com", "password");
        userAccount = userAccountRepo.save(userAccount);
         
        //Create game owner account
        GameOwner owner = new GameOwner(userAccount);
        owner = gameOwnerRepo.save(owner);
        
       
        //Retrieve board game instance from data base
        Optional<GameOwner> gameOwnerFromDb = gameOwnerRepo.findById(owner.getId());
        GameOwner gameOwner = gameOwnerFromDb.get();

        assertEquals(gameOwner.getId(),owner.getId());
        assertEquals(gameOwner.getUserRole(),owner.getUserRole());
        
    }
}