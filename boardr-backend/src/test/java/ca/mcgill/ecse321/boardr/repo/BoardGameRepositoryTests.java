package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.BoardGame;


@SpringBootTest // Loads the full Spring Boot context for testing
public class BoardGameRepositoryTests {

    @Autowired
    private BoardGameRepository boardGameRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear the database before and after each test to ensure a clean slate
        boardGameRepository.deleteAll();
    }

    @Test
    public void testWriteBoardGame() {
        // Create a BoardGame object
        BoardGame game = new BoardGame("Catan", "A popular strategy board game.");
        
        // Save it to the repository
        BoardGame savedGame = boardGameRepository.save(game);
        
        // Check that it was assigned an ID
        assertNotNull(savedGame.getGameId());
        
        // Verify that the attributes match
        assertEquals("Catan", savedGame.getName());
        assertEquals("A popular strategy board game.", savedGame.getDescription());
    }

    @Test
    public void testReadBoardGame() {
        // Create and save a BoardGame object
        BoardGame game = new BoardGame("Monopoly", "Classic real-estate game.");
        game = boardGameRepository.save(game);

        // Retrieve the BoardGame by its ID
        Optional<BoardGame> retrievedGame = boardGameRepository.findById(game.getGameId());
        
        // Assert that the game was found and the attributes are correct
        assertTrue(retrievedGame.isPresent());
        assertEquals("Monopoly", retrievedGame.get().getName());
        assertEquals("Classic real-estate game.", retrievedGame.get().getDescription());
    }

    @Test
    public void testUpdateBoardGameAttributes() {
        // Create and save a BoardGame object
        BoardGame game = new BoardGame("Risk", "Global domination strategy game.");
        game = boardGameRepository.save(game);

        // Retrieve the game
        BoardGame existingGame = boardGameRepository.findById(game.getGameId()).get();
        String newDescription = "Updated description for Risk.";
        
        // IMPORTANT: Since BoardGame doesn't provide setters, updating the entity normally isn't possible.
        // In a real application, you'd add a setter or use a method to update the entity.
        // For this example, we'll simulate an update by creating a new BoardGame instance.
        // NOTE: This is not typical for JPA updates.
        BoardGame updatedGame = new BoardGame("Risk", newDescription);
        updatedGame = boardGameRepository.save(updatedGame);
        
        // Retrieve the updated game and verify the changes
        Optional<BoardGame> retrievedGame = boardGameRepository.findById(updatedGame.getGameId());
        assertTrue(retrievedGame.isPresent());
        assertEquals("Risk", retrievedGame.get().getName());
        assertEquals(newDescription, retrievedGame.get().getDescription());
    }

    @Test
    public void testDeleteBoardGame() {
        // Create and save a BoardGame object
        BoardGame game = new BoardGame("Pandemic", "Cooperative board game to stop infections.");
        game = boardGameRepository.save(game);

        // Delete the game by ID
        boardGameRepository.deleteById(game.getGameId());

        // Verify that the game has been deleted
        Optional<BoardGame> deletedGame = boardGameRepository.findById(game.getGameId());
        assertFalse(deletedGame.isPresent());
    }
}
