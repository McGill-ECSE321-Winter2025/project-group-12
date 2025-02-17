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

@SpringBootTest  // Loads the full Spring Boot context for testing
public class BoardGameInstanceRepositoryTests {

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear both repositories to ensure a clean slate for each test
        boardGameInstanceRepository.deleteAll();
        boardGameRepository.deleteAll();
        gameOwnerRepository.deleteAll();
    }

    @Test
    public void testWriteBoardGameInstance() {
        // Create a BoardGame object and save it
        BoardGame game = new BoardGame("Catan", "A popular strategy board game.");
        game = boardGameRepository.save(game);

        // Create a GameOwner; using the default constructor for testing
        GameOwner owner = new GameOwner();
        owner = gameOwnerRepository.save(owner);
        // Create a BoardGameInstance with a condition and verify default availability (true)
        String condition = "New";
        BoardGameInstance instance = new BoardGameInstance(game, owner, condition);

        // Save the instance to the repository
        BoardGameInstance savedInstance = boardGameInstanceRepository.save(instance);

        // Validate that it was assigned an ID and its attributes are set correctly
        assertNotNull(savedInstance.getindividualGameId());
        assertEquals(condition, savedInstance.getCondition());
        assertTrue(savedInstance.isAvailable());
        assertNotNull(savedInstance.getBoardGame());
        assertEquals("Catan", savedInstance.getBoardGame().getName());
        assertNotNull(savedInstance.getGameOwner());
    }

    @Test
    public void testReadBoardGameInstance() {
        // Create and save a BoardGame
        BoardGame game = new BoardGame("Monopoly", "Classic board game.");
        game = boardGameRepository.save(game);

        // Create a GameOwner
        GameOwner owner = new GameOwner();

        owner = gameOwnerRepository.save(owner);

        // Create and save a BoardGameInstance with a given condition
        String condition = "Used";
        BoardGameInstance instance = new BoardGameInstance(game, owner, condition);
        instance = boardGameInstanceRepository.save(instance);

        // Retrieve the instance by its ID
        Optional<BoardGameInstance> retrievedInstance = boardGameInstanceRepository.findById(instance.getindividualGameId());
        assertTrue(retrievedInstance.isPresent());
        assertEquals("Monopoly", retrievedInstance.get().getBoardGame().getName());
        assertEquals(condition, retrievedInstance.get().getCondition());
    }

    @Test
    public void testDeleteBoardGameInstance() {
        // Create and save a BoardGame
        BoardGame game = new BoardGame("Risk", "Strategy game.");
        game = boardGameRepository.save(game);

        // Create a GameOwner
        
        GameOwner owner = new GameOwner();
        owner = gameOwnerRepository.save(owner);
        // Create and save a BoardGameInstance
        BoardGameInstance instance = new BoardGameInstance(game, owner, "Fair");
        instance = boardGameInstanceRepository.save(instance);

        // Delete the instance
        boardGameInstanceRepository.deleteById(instance.getindividualGameId());

        // Verify the instance is deleted
        Optional<BoardGameInstance> deletedInstance = boardGameInstanceRepository.findById(instance.getindividualGameId());
        assertFalse(deletedInstance.isPresent());
    }
}
