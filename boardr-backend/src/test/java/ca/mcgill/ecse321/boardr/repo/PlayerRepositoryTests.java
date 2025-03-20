package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.Player;
import ca.mcgill.ecse321.boardr.model.UserAccount;

/**
 * Integration Tests for the {@link PlayerRepository}.
 * Ensures persistence and retrieval of {@link Player} entities.
 * 
 * Test Scenario: Verifies creation, persistence, and retrieval of 
 * {@link Player} entities using Spring Boot, Jakarta Persistence, and Gradle.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject repository instances.
 * - Utilizes the @BeforeEach and @AfterEach annotations to clear the database before and after each test.
 * 
 * Test Cases:
 * 1. testCreateAndReadPlayer
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
public class PlayerRepositoryTests {

    @Autowired
    private EventRepository eventRepo;
    
    @Autowired
    private BoardGameRepository boardGameRepo;
    
    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepo;
    
    @Autowired
    private UserAccountRepository userAccountRepo;
    
    @Autowired
    private PlayerRepository playerRepo;
    
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
        playerRepo.deleteAll();
    }
    
    /**
     * Tests the creation and retrieval of a {@link Player}.
     * 
     * Steps:
     * 1. Creates and saves a {@link UserAccount}.
     * 2. Creates a {@link Player} and associates it with the created {@link UserAccount}.
     * 3. Retrieves the {@link Player} from the repository.
     * 4. Verifies that the {@link Player} attributes match expected values.
     * 
     * Assertions:
     * - Player should be present in the repository.
     * - The Player's ID and user role should match the created values.
     */

    @Test
    public void testCreateAndReadEvent() {
        //Create and save a BoardGame
        
        
        //Create user account for the game owner
        UserAccount userAccount = new UserAccount("Bobette", "bobette@mail.mcgill.ca.com", "password");
        userAccount = userAccountRepo.save(userAccount);
         
        //Create game owner account
        Player player = new Player(userAccount);
        player = playerRepo.save(player);
        
       
        //Retrieve board game instance from data base
        Optional<Player> playerFromDb = playerRepo.findById(player.getId());
        Player playerDb = playerFromDb.get();

        assertEquals(playerDb.getId(),player.getId());
        assertEquals(playerDb.getUserRole(),player.getUserRole());
    }
}