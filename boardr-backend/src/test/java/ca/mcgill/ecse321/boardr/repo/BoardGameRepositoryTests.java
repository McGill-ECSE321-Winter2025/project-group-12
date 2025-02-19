package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.BoardGame;

@SpringBootTest  
public class BoardGameRepositoryTests {

    @Autowired
    private BoardGameRepository boardGameRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Ensure a clean slate for each test
        boardGameRepository.deleteAll();
    }

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