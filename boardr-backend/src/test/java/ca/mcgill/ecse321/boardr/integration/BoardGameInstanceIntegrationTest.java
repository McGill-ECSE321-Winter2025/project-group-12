package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceCreationDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceResponseDTO;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the BoardGameInstance API.
 * This class tests the creation, retrieval, and deletion of board game instances.
 * 
 * @author Jione Ban
 * @date 2025-03-19
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardGameInstanceIntegrationTest {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    /**
     * Helper method to create a game owner in the database.
     * 
     * @param name     the name of the game owner
     * @param email    the email of the game owner
     * @param password the password of the game owner
     * @return the ID of the created game owner
     */
    private int createGameOwner(String name, String email, String password) {
        return userAccountRepository.save(new UserAccount(name, email, password)).getGameOwnerRoleId();
    }

    /**
     * Helper method to create a board game in the database.
     * 
     * @param name        the name of the board game
     * @param description the description of the board game
     * @return the ID of the created board game
     */
    private int createBoardGame(String name, String description) {
        return boardGameRepository.save(new BoardGame(name, description)).getGameId();
    }

    /**
     * Clears the database before and after each test.
     */
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        boardGameInstanceRepository.deleteAll();
        boardGameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    /**
     * Tests the creation of a board game instance.
     * Verifies that the instance is created successfully and the response contains the correct data.
     */
    @Test
    public void testCreateBoardGameInstance() {
        // arrange
        Integer gameOwnerID = createGameOwner("mark", "m@gmail.com", "123");
        Integer boardGameID = createBoardGame("Catan", "a fun game");

        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO = new BoardGameInstanceCreationDTO("fair", boardGameID, gameOwnerID);

        // act
        ResponseEntity<BoardGameInstanceResponseDTO> response = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO, BoardGameInstanceResponseDTO.class);

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BoardGameInstanceResponseDTO createdInstance = response.getBody();
        assertNotNull(createdInstance);
        assertEquals("fair", createdInstance.getCondition());
        assertEquals(boardGameID, createdInstance.getBoardGameId());
        assertEquals(gameOwnerID, createdInstance.getGameOwnerId());
        assertTrue(createdInstance.getIndividualGameId() > 0);
    }

    /**
     * Tests the retrieval of all board game instances.
     * Verifies that the correct instances are returned and their data matches the expected values.
     */
    @Test
    public void testGetBoardGameInstance() {
        // arrange
        Integer gameOwnerID = createGameOwner("mark", "m@gmail.com", "123");
        Integer boardGameID1 = createBoardGame("Catan", "a fun game");
        Integer boardGameID2 = createBoardGame("Dominion", "a deck building game");

        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO1 = new BoardGameInstanceCreationDTO("fair", boardGameID1, gameOwnerID);
        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO2 = new BoardGameInstanceCreationDTO("good", boardGameID2, gameOwnerID);

        ResponseEntity<BoardGameInstanceResponseDTO> response1 = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO1, BoardGameInstanceResponseDTO.class);
        ResponseEntity<BoardGameInstanceResponseDTO> response2 = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO2, BoardGameInstanceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        // act
        ResponseEntity<BoardGameInstanceResponseDTO[]> responseEntity = client.getForEntity("/boardgameinstances", BoardGameInstanceResponseDTO[].class);

        // assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        List<BoardGameInstanceResponseDTO> responseList = Arrays.asList(responseEntity.getBody());

        assertTrue(responseList.size() >= 2);
        List<String> conditionNames = responseList.stream().map(BoardGameInstanceResponseDTO::getCondition).toList();
        assertTrue(conditionNames.contains("fair"));
        assertTrue(conditionNames.contains("good"));
    }

    /**
     * Tests the deletion of a board game instance.
     * Verifies that the instance is deleted successfully and the response status is OK.
     */
    @Test
    public void testDeleteBoardGameInstance() {
        // arrange
        Integer gameOwnerID = createGameOwner("mark", "m@gmail.com", "123");
        Integer boardGameID = createBoardGame("Catan", "a fun game");
        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO = new BoardGameInstanceCreationDTO("fair", boardGameID, gameOwnerID);
        ResponseEntity<BoardGameInstanceResponseDTO> response = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO, BoardGameInstanceResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        Integer instanceID = response.getBody().getIndividualGameId();

        // act
        ResponseEntity<Void> deleteResponse = client.exchange("/boardgameinstances/{id}", HttpMethod.DELETE, null, Void.class, instanceID);

        // assert
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }
}
