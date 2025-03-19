package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.dto.BoardGame.BoardGameCreationDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGame.BoardGameResponseDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceCreationDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceResponseDTO;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repo.GameOwnerRepository;
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

    private int createGameOwner(String name, String email, String password) { return userAccountRepository.save(new UserAccount(name, email, password)).getGameOwnerRoleId(); }
    private int createBoardGame(String name, String description) { return boardGameRepository.save(new BoardGame(name, description)).getGameId(); }


    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        boardGameInstanceRepository.deleteAll();
        boardGameRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    public void testCreateBoardGameInstance() {
        //arrange
        Integer gameOwnerID = createGameOwner("mark", "m@gmail.com", "123");
        Integer boardGameID = createBoardGame("Catan", "a fun game");

        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO = new BoardGameInstanceCreationDTO("fair", boardGameID, gameOwnerID);
        //act
        ResponseEntity<BoardGameInstanceResponseDTO> response = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO, BoardGameInstanceResponseDTO.class);

        //assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BoardGameInstanceResponseDTO createdInstance = response.getBody();
        assertNotNull(createdInstance);
        assertEquals("fair", createdInstance.getCondition());
        assertEquals(boardGameID, createdInstance.getBoardGameId());
        assertEquals(gameOwnerID, createdInstance.getGameOwnerId());
        assertTrue(createdInstance.getIndividualGameId() > 0);

    }

    @Test
    public void testGetBoardGameInstance() {
        //arrange
        Integer gameOwnerID = createGameOwner("mark", "m@gmail.com", "123");
        Integer boardGameID1 = createBoardGame("Catan", "a fun game");
        Integer boardGameID2 = createBoardGame("Dominion", "a deck building game");

        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO1 = new BoardGameInstanceCreationDTO("fair", boardGameID1, gameOwnerID);
        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO2 = new BoardGameInstanceCreationDTO("good", boardGameID2, gameOwnerID);

        ResponseEntity<BoardGameInstanceResponseDTO> response1 = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO1, BoardGameInstanceResponseDTO.class);
        ResponseEntity<BoardGameInstanceResponseDTO> response2 = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO2, BoardGameInstanceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());


        //act
        ResponseEntity<BoardGameInstanceResponseDTO[]> responseEntity = client.getForEntity("/boardgameinstances", BoardGameInstanceResponseDTO[].class);

        //assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        List<BoardGameInstanceResponseDTO> responseList = Arrays.asList(responseEntity.getBody());

        assertTrue(responseList.size() >= 2);
        List<String> conditionNames = responseList.stream().map(BoardGameInstanceResponseDTO::getCondition).toList();
        assertTrue(conditionNames.contains("fair"));
        assertTrue(conditionNames.contains("good"));
    }

    @Test
    public void testDeleteBoardGameInstance() {
        //arrange
        Integer gameOwnerID = createGameOwner("mark", "m@gmail.com", "123");
        Integer boardGameID = createBoardGame("Catan", "a fun game");
        BoardGameInstanceCreationDTO boardGameInstanceCreationDTO = new BoardGameInstanceCreationDTO("fair", boardGameID, gameOwnerID);
        ResponseEntity<BoardGameInstanceResponseDTO> response = client.postForEntity("/boardgameinstances", boardGameInstanceCreationDTO, BoardGameInstanceResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        Integer instanceID = response.getBody().getIndividualGameId();

        //act
        ResponseEntity<Void> deleteResponse = client.exchange("/boardgameinstances/{id}", HttpMethod.DELETE, null, Void.class, instanceID);

        //assert
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }


}
