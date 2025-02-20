package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.BoardGame;

/**
 * Integration Tests for the {@link BoardGameRepository}.
 * Ensures {@link BoardGame} entities are correctly persisted and retrieved.
 * 
 * Test Scenario: Verifies creation, persistence, and retrieval of 
 * {@link BoardGame} entities using Spring Boot, Jakarta Persistence, and Gradle.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject the repository instance.
 * - Utilizes @BeforeEach and @AfterEach annotations to clear the database before and after each test.
 * 
 * Test Cases:
 * 1. testCreateAndReadBoardGame
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
public class BoardGameRepositoryTests {

    @Autowired
    private BoardGameRepository boardGameRepository;

    /**
     * Clears the database before and after each test to maintain a fresh state.
    */

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Ensure a clean slate for each test
        boardGameRepository.deleteAll();
    }

    /**
     * Tests the creation and retrieval of a {@link BoardGame} entity.
     * 
     * Steps:
     * 1. Create a new {@link BoardGame} object.
     * 2. Save it to the repository.
     * 3. Verify that an ID was assigned upon saving.
     * 4. Retrieve the {@link BoardGame} from the repository using its ID.
     * 5. Validate that the retrieved attributes match the original values.
     * 
     * Assertions:
     * - {@link BoardGame} ID should be assigned after saving.
     * - {@link BoardGame} should be retrievable from the repository by ID.
     * - The attributes of the retrieved {@link BoardGame} should match the original values.
     */

    @Test
    public void testCreateAndReadBoardGame() {
        // Create a BoardGame object
        BoardGame game = new BoardGame("Catan", "A popular strategy board game.");
        
        // Save it to the repository
        BoardGame savedGame = boardGameRepository.save(game);
        
        // Check that it was assigned an ID 
        assertNotNull(savedGame.getGameId(), "BoardGame ID should not be null after saving");
        
        // Retrieve the BoardGame by its ID
        Optional<BoardGame> retrieved = boardGameRepository.findById(savedGame.getGameId());
        assertTrue(retrieved.isPresent(), "BoardGame should be retrievable from the repository");
        
        // Verify that the attributes match
        BoardGame retrievedGame = retrieved.get();
        assertEquals("Catan", retrievedGame.getName(), "BoardGame name should match");
        assertEquals("A popular strategy board game.", retrievedGame.getDescription(), "BoardGame description should match");
    }
}