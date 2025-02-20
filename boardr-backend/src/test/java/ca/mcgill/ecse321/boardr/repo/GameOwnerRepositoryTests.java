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
 * Integration Tests for the {@link BoardGameInstanceRepository}.
 * Ensures persistence and retrieval of {@link BoardGameInstance} entities.
 * 
 * Test Scenario: Verifies creation, persistence, and retrieval of 
 * {@link BoardGameInstance} entities using Spring Boot, Jakarta Persistence, and Gradle.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject repository instances.
 * - Utilizes the @BeforeEach and @AfterEach annotations to clear the database before and after each test.
 * 
 * Test Cases:
 * 1. testCreateAndReadEvent
 * 
 * Dependencies:
 * - Gradle
 * - Jakarta Persistence
 * - Spring Boot
 * 
 * Author: Yoon, Kyujin
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
     * Tests the creation and retrieval of a {@link BoardGameInstance}.
     * 
     * Steps:
     * 1. Creates and saves a {@link BoardGame}.
     * 2. Creates a {@link UserAccount} and associates it with a {@link GameOwner}.
     * 3. Creates and saves a {@link BoardGameInstance} linked to the {@link GameOwner}.
     * 4. Retrieves the {@link BoardGameInstance} from the repository.
     * 5. Verifies that all attributes match expected values.
     * 
     * Assertions:
     * - BoardGameInstance should be present in the repository.
     * - The condition should match "New".
     * - The instance should be available by default.
     * - Associated {@link BoardGame} and {@link GameOwner} should match.
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