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
 * Author: Yoon, Kyujin, Jun Ho
 * Version: 1.0
 */

@SpringBootTest
public class BoardGameInstanceRepositoryTests {

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
        BoardGame boardGame = new BoardGame("Catan", "A popular strategy board game.");
        boardGame = boardGameRepo.save(boardGame);
        
        
        //Create user account for the game owner
        UserAccount userAccount = new UserAccount("Bobette", "bobette@mail.mcgill.ca.com", "password");
        userAccount = userAccountRepo.save(userAccount);
         
        //Create game owner account
        GameOwner owner = new GameOwner(userAccount);
        owner = gameOwnerRepo.save(owner);
        
        //Create a board game instance with the owner
        BoardGameInstance boardGameInstance = new BoardGameInstance(boardGame, owner, "New");
        boardGameInstance = boardGameInstanceRepo.save(boardGameInstance);
        
        //Retrieve board game instance from data base
        Optional<BoardGameInstance> boardGameInstanceFromDb = boardGameInstanceRepo.findById(boardGameInstance.getindividualGameId());
        assertTrue(boardGameInstanceFromDb.isPresent(), "Event should be present in the repository");
        BoardGameInstance retrievedBoardGameInstance = boardGameInstanceFromDb.get();

        // Verify that the attributes match
        assertEquals("New", retrievedBoardGameInstance.getCondition(), "The condition should match");
        assertTrue(retrievedBoardGameInstance.isAvailable(), "The instance should be available by default");
        assertNotNull(retrievedBoardGameInstance.getBoardGame(), "Associated BoardGame should not be null");
        assertEquals("Catan", retrievedBoardGameInstance.getBoardGame().getName(), "BoardGame name should match");
        assertEquals(retrievedBoardGameInstance.getGameOwner().getId(),boardGameInstance.getGameOwner().getId());
        
        
    }
}